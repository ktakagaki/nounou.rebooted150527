package nounou.data.formats

import java.io.File
import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}
import nounou.data.{X, XDataChannelFilestream}
import nounou._
import scala.collection.mutable.ListBuffer

/**
 * @author ktakagaki
 */
object FileLoaderNCS extends FileLoaderNLX {

  //Constants for NCS files
  //val headerBytes = 16384L FileLoaderNLX
  /**Size of each record, in bytes*/
  override val recordBytes = 1044
  /**Number of samples per record*/
  val recordSampleCount= 512
  val recordNonDataBytes = recordBytes - recordSampleCount * 2
  /**Sample rate, Hz*/
  val sampleRate = 32000D
  val absOffset = 0D
  val absUnit: String = "microV"
  //val absGain = ???
  def recordStartByte(recNo: Int) = headerBytes + recordBytes * recNo.toLong

  //must be declared here, and values used in companion object... object cannot override class
  val xBits = 1024
  lazy val xBitsD = xBits.toDouble

  override def loadImpl(file: File): List[X] = {

      fHand = new RandomAccessFile(file, "r")(ByteConverterLittleEndian)

      // <editor-fold desc="parse the Neuralynx header">

      nlxHeaderLoad()
      val tempAcqEntName = nlxHeaderParserS("AcqEntName", "NoName")
      val tempRecordSize = nlxHeaderParserI("RecordSize", "0")
        require(tempRecordSize == recordBytes, "NCS file with non-standard record size: " + tempRecordSize)
      val tempSampleFreqD = nlxHeaderParserD("SamplingFrequency", "1")
        require(tempSampleFreqD == sampleRate, "NCS file with non-standard sampling frequency: " + tempSampleFreqD)
      val tempADBitVolts = nlxHeaderParserD("ADBitVolts", "1")

      // </editor-fold>

      val tempRecTSIncrement = 16000L //(1000000D * tempRecLength.toDouble/tempSampleFreqD).toLong
      //require(tempRecTSIncrement == 16000L, "NCS file with non-standard combi of record length and sampling")

      //Calculate the number of records in the ncs file, based on the file length
      val tempNoRecords = ((fHand.length - headerBytes).toDouble/tempRecordSize.toDouble).toInt

      //Loop through the file and process record start timestamps
      //   if a start timestamp jumps in time, a new segment is defined

      //First record dealt separately
      fHand.seek( headerBytes )

      ///qwTimeStamp
      var thisRecTS = fHand.readUInt64Shifted()
      var lastRecTS = thisRecTS
      var tempStartTimestamps = Vector[Long]( lastRecTS )
        var tempLengths = Vector[Int]() //tempLengths defined with -1 at header for looping convenience, will be dropped later
        var tempSegmentStartFrame = 0

      //dwChannelNumber
      fHand.jumpBytes(4)

      //dwSampleFreq
      val dwSampleFreq = fHand.readUInt32
      require(dwSampleFreq == sampleRate, "Reported sampling frequency for record " + 0 + " is different from file sampling frequency " + dwSampleFreq)

      //dwNumValidSamples
      val dwNumValidSamples = fHand.readUInt32
      require(dwNumValidSamples == recordSampleCount, "Currently can only deal with records which are " + recordSampleCount + " samples long.")

//      //snSamples
//      fHand.jumpBytes(recordSampleCount*2)

      //ToDo 3: Implement cases where timestamps skip just a slight amount d/t DAQ problems

      // <editor-fold defaultstate="collapsed" desc=" read loop ">

      var rec = 1 //already dealt with rec=0
      var lastRecJump = 1
      var lastTriedJump = 4096
      while(rec < tempNoRecords){

        fHand.seek( recordStartByte(rec) )
        //qwTimeStamp
        thisRecTS = fHand.readUInt64Shifted

            if(thisRecTS > lastRecTS + tempRecTSIncrement*lastRecJump){

                if( lastRecJump != 1 ){
                  //Went over change in segment, rewind and try a step of 1
                  rec = rec - lastRecJump + 1
                  fHand.seek( recordStartByte(rec) )
                  lastRecJump = 1
                  //qwTimeStamp
                  thisRecTS = fHand.readUInt64Shifted

                  if(thisRecTS > lastRecTS + tempRecTSIncrement/*lastRecJump*/){
                    //We got the correct start of a segment, with lastRecJump of 1!!!

                    //Append timestamp for record rec as a new segment start
                    tempStartTimestamps = tempStartTimestamps :+ thisRecTS
                    //Append length of previous segment as segment length
                    tempLengths = tempLengths :+ (rec*512 - tempSegmentStartFrame)
                    //New segment's start frame
                    tempSegmentStartFrame = rec*512

                    //reset next jump attempt count
                    lastTriedJump = 4096

                  } else {
                    //We went ahead by lastRecJump = 1, but the record was just one frame ahead in the same jump
                    if( lastTriedJump > 1 ){
                      //Jump less next loop
                      lastTriedJump = lastTriedJump / 2
                    }
                  }

                } else {
                  //We went forward lastRecJump = 1, and hit a new segment

                  //Append timestamp for record rec as a new segment start
                  tempStartTimestamps = tempStartTimestamps :+ thisRecTS
                  //Append length of previous segment as segment length
                  tempLengths = tempLengths :+ (rec*512 - tempSegmentStartFrame)
                  //New segment's start frame
                  tempSegmentStartFrame = rec*512

                  //reset next jump attempt count
                  lastTriedJump = 4096

                }

            } //else { } //advanced correctly within segment

            //reset marker for lastTS
            lastRecTS = thisRecTS

            //VARIOUS CHECKS, NOT NECESSARY
            //dwChannelNumber
            fHand.jumpBytes(4)
            //dwSampleFreq
            val dwSampleFreq = fHand.readUInt32
            require(dwSampleFreq == sampleRate, "Reported sampling frequency for record " + rec + ", " + dwSampleFreq + " is different from file sampling frequency " + sampleRate )
            //dwNumValidSamples
            val dwNumValidSamples = fHand.readUInt32
            require(dwNumValidSamples == recordSampleCount, "Currently can only deal with records which are " + recordSampleCount + " samples long.")


            //Loop advancement
            if( rec == tempNoRecords -1 ){
              //was on last record
              rec += 1 //this will cause break in while
            } else if (rec + lastTriedJump < tempNoRecords ) {
              //try the jump in lastTriedJump
              lastRecJump = lastTriedJump
              rec += lastRecJump
            } else {
              //jump to the end of the file
              lastRecJump = tempNoRecords-1-rec
              lastTriedJump = lastRecJump
              rec += lastRecJump
            }


      }
      //Last record cleanup: Append length of previous segment as segment length
      tempLengths = tempLengths :+ (tempNoRecords*512 - tempSegmentStartFrame)

// </editor-fold>

    val xDataChannelNCS = new XDataChannelNCS(fileHandle = fHand,
      absGain = 1.0E6 * tempADBitVolts / xBitsD,
      segmentLengths = tempLengths,
      segmentStartTSs = tempStartTimestamps,
      channelName = tempAcqEntName
    )

    logger.info( "loaded {}", xDataChannelNCS )
    List[X]( xDataChannelNCS )

  }



}


class XDataChannelNCS
( override val fileHandle: RandomAccessFile,
                       override val absGain: Double,
                       override val segmentLengths: Vector[Int],
                       override val segmentStartTSs: Vector[Long],
                       override val channelName: String
                       ) extends XDataChannelFilestream {

  val t = FileLoaderNCS
  override val absOffset: Double = t.absOffset
  override val absUnit: String = t.absUnit
  override val sampleRate: Double = t.sampleRate
  override val xBits = t.xBits

  def dataByteLocationRI(record: Int, index: Int) = {
    //val (record, index) = frameSegmentToRecordIndex(frame, segment)
    recordStartByte(record) + 20L + (index * 2)
  }

  def recordStartByte(recNo: Int) = t.recordStartByte(recNo)

  def frameSegmentToRecordIndex(frame: Int, segment: Int) = {
    val cumFrame = segmentStartFrames(segment) + frame
//    println("cf " + cumFrame + " rl " + t.recordSampleCount)
//    println( ( cumFrame / t.recordSampleCount, cumFrame % t.recordSampleCount) )
    ( cumFrame / t.recordSampleCount, cumFrame % t.recordSampleCount)
  }



  def readPointImpl(frame: Int, segment: Int): Int = {
    val (record, index) = frameSegmentToRecordIndex(frame, segment)
    fileHandle.seek( dataByteLocationRI( record, index ) )
    fileHandle.readInt16 * xBits
  }

  override def readTraceImpl(range: Range.Inclusive, segment: Int): Vector[Int] = {
    var (currentRecord: Int, currentIndex: Int) = frameSegmentToRecordIndex( range.start, segment )
    val (endReadRecord: Int, endReadIndex: Int) = frameSegmentToRecordIndex( range.end, segment ) //range is exclusive of last
    //println( range.start + " d " + range.end)
    //println( "err eri " + (endReadRecord, endReadIndex) )

    val tempRet = ListBuffer[Int]()//var tempRet = Vector[Int]()

    fileHandle.seek( dataByteLocationRI(currentRecord, currentIndex) )
    tempRet ++= fileHandle.readInt16(512 - currentIndex).map( _.toInt* xBits )
    currentRecord += 1
    fileHandle.jumpBytes(t.recordNonDataBytes)
    while(currentRecord < endReadRecord){
      tempRet ++= fileHandle.readInt16(512 /*- currentIndex*/).map( _.toInt* xBits )
      //tempRet = tempRet ++ fileHandle.readInt16(512 - currentIndex).map( _.toInt* xBits )
      currentRecord += 1
      //currentIndex = 0
      fileHandle.jumpBytes(t.recordNonDataBytes)
    }
    //if(currentIndex <= endReadIndex){
      //fileHandle.seek( dataByteLocationRI(currentRecord, 0) )
      tempRet ++= fileHandle.readInt16(endReadIndex /*- currentIndex*/ + 1).map( _.toInt* xBits )
    //}

    tempRet.toVector

}


  //
  //    //val res = new Array[Int]( range.length )
  //    val ((startRec, startIndex), (endRec, endIndex)) =
  //      if(range.step > 0) ( frameToRecordIndex( range.start ), frameToRecordIndex( range.end ))
  //      else ( frameToRecordIndex( range.end ), frameToRecordIndex( range.start ) )
  //
  //    val tempret =
  //      if(startRec == endRec){
  //        readRecord(startRec, startIndex, endIndex)
  //      } else {
  //        val startRecData = readRecord(startRec, startIndex, t.recordBytes - 1)
  //        val endRecData = readRecord(endRec, 0, endIndex )
  //
  //        if(startRec + 1 < endRec){
  //          startRecData ++:
  //          (for( rec <- startRec + 1 until endRec) yield readRecord(rec)).flatten ++:
  //          endRecData
  //        } else {
  //          startRecData ++: endRecData
  //        }
  //      }
  //
  //    if(range.step > 0) tempret else tempret.reverse
}

//  def readRecord(recNo: Int): Vector[Int] = {
//    fileHandle.seek( recordStartByte(recNo) )
//    fileHandle.readInt16(t.recordSampleCount).toVector.map(_ * xBits)
//  }
//
//  def readRecord(recNo: Int, startI: Int, endI: Int): Vector[Int] = {
//    require( startI >= 0 && endI < 512 && startI <= endI)
//    fileHandle.seek( recordStartByte(recNo) + startI*2 )
//    fileHandle.readInt16( (endI-startI) ).toVector.map(_ * xBits)
//  }
