package nounou.io.neuralynx

import java.io.File

import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}
import breeze.linalg.{DenseVector => DV, convert}
import nounou.elements.NNElement
import nounou.elements.data.NNDataChannelFilestream
import nounou.elements.ranges.SampleRangeValid
import nounou.elements.traits.{NNDataScale, NNDataTiming}

/**
* @author ktakagaki
*/
class FileAdapterNCS extends FileAdapterNeuralynx {

  override val canLoadExtensions = Array("ncs")
  def load( file: File ): Array[NNElement] = loadImpl( file )

  //Constants for NCS files
  //val headerBytes = 16384L FileAdapterNLX
  /**Size of each record, in bytes*/
  override val recordBytes = 1044
  /**Sample rate, Hz*/
  var sampleRate: Double = -1d
  /**Number of samples per record*/
  val recordSampleCount= 512
  val recordNonDataBytes = recordBytes - recordSampleCount * 2
  val absOffset = 0D
  val absUnit: String = "microV"
  //val absGain = ???
  def recordStartByte(recNo: Int) = headerBytes + recordBytes * recNo.toLong

  //must be declared here, and values used in companion object... object cannot override class
  val xBits = 1024
  lazy val xBitsD = xBits.toDouble

  private def loadImpl(file: File): Array[NNElement] = {

      fHand = new RandomAccessFile(file, "r")(ByteConverterLittleEndian)

      // <editor-fold desc="parse the Neuralynx header">

      nlxHeaderLoad()
      val tempAcqEntName = nlxHeaderParserS("AcqEntName", "NoName")
      val tempRecordSize = nlxHeaderParserI("RecordSize", "0")
        require(tempRecordSize == recordBytes,
          s"NCS file with non-standard record size: $tempRecordSize")
      sampleRate = nlxHeaderParserD("SamplingFrequency", "1")
      require(sampleRate >= 1000d,
          //tempSampleFreqD == sampleRate,
          s"NCS file with non-standard sampling frequency: $sampleRate")
      val tempADBitVolts = nlxHeaderParserD("ADBitVolts", "1")

      // </editor-fold>

      val tempRecTSIncrement = (1000000D * tempRecordSize.toDouble/sampleRate).toLong

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
      require(dwSampleFreq == sampleRate,
        "Reported sampling frequency for record 0 is different from file sampling frequency " +
          dwSampleFreq)

      //dwNumValidSamples
      val dwNumValidSamples = fHand.readUInt32
      require(dwNumValidSamples == recordSampleCount,
        s"Currently can only deal with records which are $recordSampleCount samples long.")

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
                //Went over change in segment, rewind and try a stepMs of 1
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
                    //Jump less at next loop
                    lastTriedJump = lastTriedJump / 2
                  }
                }

              } else {
                //lastRecJump = 1, we've found the start of a new segment

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

        // <editor-fold defaultstate="collapsed" desc=" VARIOUS CHECKS, NOT NECESSARY ">
        //dwChannelNumber
        fHand.jumpBytes(4)
        //dwSampleFreq
        val dwSampleFreq = fHand.readUInt32
        require(dwSampleFreq == sampleRate,
          s"Reported sampling frequency for record $rec, $dwSampleFreq, " +
            s"is different from file sampling frequency $sampleRate )" )
        //dwNumValidSamples
        val dwNumValidSamples = fHand.readUInt32
        require(dwNumValidSamples == recordSampleCount,
          s"Currently can only deal with records which are $recordSampleCount samples long.")
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" loop 'rec' advancement ">
        if( rec == tempNoRecords -1 ){
          //was on lastValid record
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
        // </editor-fold>


      }
      //Last record cleanup: Append length of previous segment as segment length
      tempLengths = tempLengths :+ (tempNoRecords*512 - tempSegmentStartFrame)

// </editor-fold>

    //println("tempADBitVolts " + tempADBitVolts)

    val xDataChannelNCS = new NNDataChannelNCS(
                  fileHandle = fHand,
                  NNDataTiming.apply(sampleRate, tempLengths.toArray, tempStartTimestamps.toArray),
                  NNDataScale.apply(Short.MinValue.toInt*xBits, Short.MaxValue.toInt*xBits,
                          absGain = 1.0E6 * tempADBitVolts / xBitsD,
                          absOffset = 0d,
                          absUnit = "microV"),
                  channelName = tempAcqEntName
                                              )
    //println("absGain " + xDataChannelNCS.scale.absGain)
    logger.info( "loaded {}", xDataChannelNCS )
    Array[NNElement]( xDataChannelNCS )

  }
}

object FileAdapterNCS {
  val instance = new FileAdapterNCS
}



/**A specialized immutable [nounou.data.XDataChannelFileStream] for NCS files.
  */
class NNDataChannelNCS( override val fileHandle: RandomAccessFile,
                        timingEntry: NNDataTiming, scaleEntry: NNDataScale,
                        override val channelName: String ) extends NNDataChannelFilestream {

  val t = FileAdapterNCS.instance

  setTiming( timingEntry )
  setScale( scaleEntry )

  // <editor-fold defaultstate="collapsed" desc=" recordIndex ">

  def recordIndexStartByte(record: Int, index: Int) = {
    t.recordStartByte(record) + 20L + (index * 2)
  }

  def fsToRecordIndex(frame: Int, segment: Int) = {
    val cumFrame = timing.segmentStartFrame(segment) + frame
    ( cumFrame / t.recordSampleCount, cumFrame % t.recordSampleCount)
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" data implementations ">

  override def readPointImpl(frame: Int, segment: Int): Int = {
    val (record, index) = fsToRecordIndex( frame, segment )
    fileHandle.seek( recordIndexStartByte( record, index ) )
    fileHandle.readInt16 * scale.xBits
  }

  override def readTraceDVImpl(range: SampleRangeValid): DV[Int] = {
    //println("XDataChannelNCS " + range.toString())
    var (currentRecord: Int, currentIndex: Int) = fsToRecordIndex(range.start, range.segment)
    val (endReadRecord: Int, endReadIndex: Int) = fsToRecordIndex(range.last, range.segment) //range is inclusive of lastValid

    println( "curr " + (currentRecord, currentIndex).toString )
    println( "end " + (endReadRecord, endReadIndex).toString )
    //ToDo1 program step
    //val step = range.step

    val tempRet = DV.zeros[Int](range.last-range.start+1)//range.length)//DV[Int]()
    var currentTempRetPos = 0

    fileHandle.seek( recordIndexStartByte(currentRecord, currentIndex) )

    if(currentRecord == endReadRecord){
      //if the whole requested trace fits in one record
      val writeLen = (endReadIndex - currentIndex) + 1
      val writeEnd = currentTempRetPos + writeLen
      //      println( "writeLen " + writeLen.toString + " writeEnd " + writeEnd.toString )
      //ToDo 3: improve breeze dv requirement documentation
      tempRet(currentTempRetPos until writeEnd ) := convert( DV(fileHandle.readInt16(writeLen)), Int)  * scale.xBits
      currentTempRetPos = writeEnd
    } else {
      //if the requested trace spans multiple records

      //read data contained in first record
      var writeEnd = currentTempRetPos + (512 - currentIndex)
      tempRet(currentTempRetPos until writeEnd ) := convert( DV(fileHandle.readInt16(512 - currentIndex)), Int)  * scale.xBits
      currentRecord += 1
      currentTempRetPos = writeEnd
      fileHandle.jumpBytes(t.recordNonDataBytes)

      //read data from subsequent records, excluding lastValid record
      while (currentRecord < endReadRecord) {
        writeEnd = currentTempRetPos + 512
        tempRet(currentTempRetPos until writeEnd ) := convert( DV(fileHandle.readInt16(512 /*- currentIndex*/)), Int) * scale.xBits
        currentRecord += 1
        currentTempRetPos = writeEnd
        fileHandle.jumpBytes(t.recordNonDataBytes)
      }

      //read data contained in lastValid record
      writeEnd = currentTempRetPos + endReadIndex + 1
      tempRet(currentTempRetPos until writeEnd ) := convert( DV(fileHandle.readInt16(endReadIndex + 1)), Int) * scale.xBits

    }

    tempRet( 0 until tempRet.length by range.step )

    // </editor-fold>
  }

}