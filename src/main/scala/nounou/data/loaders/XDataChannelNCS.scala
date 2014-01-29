package nounou.data.loaders

import java.io.File
import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}
import breeze.linalg.{DenseVector, accumulate}
import nounou.data.{X, Span}
import nounou.data.xdata.XDataChannelFilestream

/**
 * @author ktakagaki
 */
object XDataChannelNCS extends FileLoaderNLX {

  //Constants for NCS files
  //val headerBytes = 16384L FileLoaderNLX
  /**Size of each record, in bytes*/
  val recordSize = 1044
  /**Number of samples per record*/
  val recordLength= 512
  /**Sample rate, Hz*/
  val sampleRate = 32000D
  val absOffset = 0D
  val absUnit: String = "uV"
  //val absGain = ???


  //must be declared here, and values used in companion object... object cannot override class
  val xBits = 1024
  lazy val xBitsD = xBits.toDouble

  override def loadImpl(file: File): List[X] = {

      val fHand = new RandomAccessFile(file, "r")(ByteConverterLittleEndian)

      // <editor-fold desc="parse the Neuralynx header">

      val tempNlxHeader = fHand.readChar(headerBytes).toString
      val tempAcqEntName = simpleParser( tempNlxHeader, "AcqEntName", "NoName")
      val tempRecordSize = simpleParser( tempNlxHeader, "RecordSize", "0").toInt
        require(tempRecordSize == recordSize, "NCS file with non-standard record size: " + tempRecordSize)
      val tempSamplingFrequencyD = simpleParser( tempNlxHeader, "SamplingFrequency", "1").toDouble
        require(tempSamplingFrequencyD == sampleRate, "NCS file with non-standard sampling frequency: " + tempSamplingFrequencyD)
      val tempADBitVolts = simpleParser( tempNlxHeader, "ADBitVolts", "1").toDouble
      // </editor-fold>

      val tempRecTSIncrement = 16000L //(1000000D * tempRecLength.toDouble/tempSamplingFrequencyD).toLong
        //require(tempRecTSIncrement == 16000L, "NCS file with non-standard combi of record length and sampling")

      //Calculate the number of records in the ncs file, based on the file length
      val tempNoRecords = ((fHand.length - headerBytes).toDouble/tempRecordSize.toDouble).toInt

      //Loop through the file and process record start timestamps
      //   if a start timestamp jumps in time, a new segment is defined
      //fHand.seek( recordStartByte(0) )
      var qwTimeStamp = fHand.readUInt64Shifted
      var tempStartTimestamps = Vector[Long]( qwTimeStamp )
      var tempLengths = Vector[Int]() //tempLengths defined with -1 at head for looping convenience, will be dropped later
      var tempRecStartFrame = 0

      //ToDo 3: Implement cases where timestamps skip just a slight amount d/t DAQ problems
      for(rec <- 0 until tempNoRecords){
        fHand.skipBytes(4) //dwChannelNumber

        val dwSampleFreq = fHand.readUInt32
        require(dwSampleFreq == sampleRate, "Reported sampling frequency for record " + 0 + " is different from file sampling frequency " + dwSampleFreq)

        val dwNumValidSamples = fHand.readUInt32
        require(dwNumValidSamples == recordLength, "Currently can only deal with records which are " + recordLength + " samples long.")

        fHand.skipBytes(recordLength*2)

        val nextTimestamp = fHand.readUInt64Shifted
        if(nextTimestamp > qwTimeStamp + tempRecTSIncrement){

          //Append timestamp for record rec as a new segment start
          tempStartTimestamps = tempStartTimestamps :+ nextTimestamp

          //Append length of previous segment as segment length
          tempLengths = tempLengths :+ (rec*512 - tempRecStartFrame)
          tempRecStartFrame = rec*512
          //tempLengths = tempLengths :+ (rec - tempLengths(tempLengths.length-1))
        } else {
          require(nextTimestamp < qwTimeStamp + tempRecTSIncrement, "timestamp is going backward in time at record number " + rec)
        }
        qwTimeStamp = nextTimestamp
      }
      tempLengths = tempLengths :+ (tempNoRecords*512 - tempRecStartFrame)


      return List[X](
        new XDataChannelNCS(fileHandle = fHand,
                            absGain = tempADBitVolts*(10^6)/xBitsD,
                            segmentLengths = tempLengths,
                            segmentStartTSs = tempStartTimestamps,
                            channelName = tempAcqEntName             )
      )

  }


  def simpleParser(text: String, valueName: String, default: String) = {
    val pattern = ("-" + valueName + """[ ]+(\S+)""").r
    pattern.findFirstIn(text) match {
      case Some(pattern(v)) => v
      case _ => default
    }

  }


}


class XDataChannelNCS( override val fileHandle: RandomAccessFile,
                       override val absGain: Double,
                       override val segmentLengths: Vector[Int],
                       override val segmentStartTSs: Vector[Long],
                       override val channelName: String
                       ) extends XDataChannelFilestream {

  val t = XDataChannelNCS
  override val absOffset: Double = XDataChannelNCS.absOffset
  override val absUnit: String = XDataChannelNCS.absUnit
  override val sampleRate: Double = XDataChannelNCS.sampleRate
  override val xBits = t.xBits

  def dataByteLocationRI(record: Int, index: Int) = {
    //val (record, index) = frameSegmentToRecordIndex(frame, segment)
    (recordStartByte(record) + 20L + (index * 2))
  }

  def recordStartByte(recNo: Int) = t.headerBytes + t.recordSize * recNo.toLong// + 1

  def frameSegmentToRecordIndex(frame: Int, segment: Int) = {
    val cumFrame = segmentStartFrames(segment) + frame
    ( cumFrame / t.recordSize, cumFrame % t.recordSize)
  }



  def readPointImpl(frame: Int, segment: Int): Int = {
    val (record, index) = frameSegmentToRecordIndex(frame, segment)
    fileHandle.seek( dataByteLocationRI( record, index ) )
    fileHandle.readInt16 * xBits
  }

  
  override def readTraceImpl(span:Span, segment: Int): Vector[Int] = {
    val range = span.getRange( segmentLengths( currentSegment = segment ) )

    val startReadRI = frameSegmentToRecordIndex( range.start, segment )
    val (endReadRecord, endReadIndex) = frameSegmentToRecordIndex( range.end, segment )
    var (currentRecord, currentIndex) = startReadRI
    var tempRet = Vector[Int]()

    while(currentRecord < endReadRecord){
      fileHandle.seek( dataByteLocationRI(currentRecord, currentIndex) )
      tempRet = tempRet ++ fileHandle.readInt16(512 - currentIndex + 1).map( _.toInt* xBits )
      currentRecord += 1
      currentIndex = 0
    }
    if(currentIndex <= endReadIndex){
      fileHandle.seek( dataByteLocationRI(currentRecord, currentIndex) )
      tempRet = tempRet ++ fileHandle.readInt16(endReadIndex - currentIndex + 1).map( _.toInt* xBits )
    }

    tempRet
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
//        val startRecData = readRecord(startRec, startIndex, t.recordSize - 1)
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
//    fileHandle.readInt16(t.recordLength).toVector.map(_ * xBits)
//  }
//
//  def readRecord(recNo: Int, startI: Int, endI: Int): Vector[Int] = {
//    require( startI >= 0 && endI < 512 && startI <= endI)
//    fileHandle.seek( recordStartByte(recNo) + startI*2 )
//    fileHandle.readInt16( (endI-startI) ).toVector.map(_ * xBits)
//  }

}
