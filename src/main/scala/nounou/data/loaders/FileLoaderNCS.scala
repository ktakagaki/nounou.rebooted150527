package nounou.data.loaders

import java.io.File
import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}
import breeze.linalg.{DenseVector, accumulate}
import nounou.data.{X, Span, XDataChannelFilestream}
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
  /**Sample rate, Hz*/
  val sampleRate = 32000D
  val absOffset = 0D
  val absUnit: String = "microV"
  //val absGain = ???


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
      fHand.seek( headerBytes  )

      ///qwTimeStamp
      var thisRecTS = fHand.readUInt64Shifted()
      var lastRecTS = thisRecTS
      var tempStartTimestamps = Vector[Long]( lastRecTS )
        var tempLengths = Vector[Int]() //tempLengths defined with -1 at head for looping convenience, will be dropped later
        var tempSegmentStartFrame = 0

      //dwChannelNumber
      fHand.skipBytes(4)

      //dwSampleFreq
      val dwSampleFreq = fHand.readUInt32
      require(dwSampleFreq == sampleRate, "Reported sampling frequency for record " + 0 + " is different from file sampling frequency " + dwSampleFreq)

      //dwNumValidSamples
      val dwNumValidSamples = fHand.readUInt32
      require(dwNumValidSamples == recordSampleCount, "Currently can only deal with records which are " + recordSampleCount + " samples long.")

      //snSamples
      fHand.skipBytes(recordSampleCount*2)


      //ToDo 3: Implement cases where timestamps skip just a slight amount d/t DAQ problems

      // <editor-fold defaultstate="collapsed" desc=" read loop ">

      for(rec <- 1 until tempNoRecords){
        //qwTimeStamp
        thisRecTS = fHand.readUInt64Shifted
        if(thisRecTS > lastRecTS + tempRecTSIncrement){

          //Append timestamp for record rec as a new segment start
          tempStartTimestamps = tempStartTimestamps :+ thisRecTS

          //Append length of previous segment as segment length
          tempLengths = tempLengths :+ (rec*512 - tempSegmentStartFrame)
          tempSegmentStartFrame = rec*512
          //tempLengths = tempLengths :+ (rec - tempLengths(tempLengths.length-1))
        } else {
          require(thisRecTS == lastRecTS + tempRecTSIncrement, "timestamp is going backward in time at record number " + rec)
        }
        lastRecTS = thisRecTS

        //dwChannelNumber
        fHand.skipBytes(4)

        //dwSampleFreq
        val dwSampleFreq = fHand.readUInt32
        require(dwSampleFreq == sampleRate, "Reported sampling frequency for record " + rec + ", " + dwSampleFreq + " is different from file sampling frequency " + sampleRate )

        //dwNumValidSamples
        val dwNumValidSamples = fHand.readUInt32
        require(dwNumValidSamples == recordSampleCount, "Currently can only deal with records which are " + recordSampleCount + " samples long.")

        //snSamples
        fHand.skipBytes(recordSampleCount*2)
      }

      //Last record cleanup
      tempStartTimestamps = tempStartTimestamps :+ thisRecTS
      tempLengths = tempLengths :+ (tempNoRecords*512 - tempSegmentStartFrame)


// </editor-fold>

      List[X](
        new XDataChannelNCS(fileHandle = fHand,
                            absGain = 1.0E6 * tempADBitVolts / xBitsD,
                            segmentLengths = tempLengths,
                            segmentStartTSs = tempStartTimestamps,
                            channelName = tempAcqEntName
        )
      )

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

  def recordStartByte(recNo: Int) = t.headerBytes + t.recordBytes * recNo.toLong// + 1

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

  
  override def readTraceImpl(span: Span, segment: Int): Vector[Int] = {

    val range = span.getRange( segmentLengths( segment ) )

    var (currentRecord, currentIndex) = frameSegmentToRecordIndex( range.start, segment )
    val (endReadRecord, endReadIndex) = frameSegmentToRecordIndex( range.end - 1, segment )
//    println( "err eri " + (endReadRecord, endReadIndex) )

    val tempRet = ListBuffer[Int]()//var tempRet = Vector[Int]()

    while(currentRecord < endReadRecord){
      fileHandle.seek( dataByteLocationRI(currentRecord, currentIndex) )
      tempRet ++= fileHandle.readInt16(512 - currentIndex).map( _.toInt* xBits )
      //tempRet = tempRet ++ fileHandle.readInt16(512 - currentIndex).map( _.toInt* xBits )
      currentRecord += 1
      currentIndex = 0
    }
    //if(currentIndex <= endReadIndex){
      fileHandle.seek( dataByteLocationRI(currentRecord, currentIndex) )
      tempRet ++= fileHandle.readInt16(endReadIndex - currentIndex + 1).map( _.toInt* xBits )
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
