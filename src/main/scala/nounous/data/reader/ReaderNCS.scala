package nounous.data.reader

import java.io.File
import breeze.io.RandomAccessFileLE
import nounous.data.X
import nounous.data.xdata.XDataChannelFilestream
import scala.util.matching.Regex

/**
 * Created by Kenta on 12/16/13.
 */
object XDataChannelNCS extends ReaderNLX {

  override def read(file: File): List[X] = {

    val fHand = new RandomAccessFileLE(file, "r")
    val fLength = fHand.length

    val tempNlxHeader = fHand.readChar(headerBytes).toString

    val tempAcqEntName = simpleParser( tempNlxHeader, "AcqEntName", "NoName")
    val tempRecordSize = simpleParser( tempNlxHeader, "RecordSize", "0").toInt
    require(tempRecordSize == 1044, "NCS file with non-standard record size: " + tempRecordSize)
    val tempSamplingFrequency = simpleParser( tempNlxHeader, "SamplingFrequency", "1").toDouble
    require(tempSamplingFrequency == 32000D, "NCS file with non-standard sampling frequency: " + tempSamplingFrequency)
    val tempADBitVolts = simpleParser( tempNlxHeader, "ADBitVolts", "1").toDouble

    val tempNoRecords = (fLength - headerBytes)/tempRecordSize
    val tempLength = tempNoRecords * 512

    fHand.seek( recordStartByte(0) )
    var tempTS = fHand.readUInt64Shifted()
    var tempTimestamps = Vector[Long]( tempTS )


    for(rec <- 1 until tempNoRecords){
      if(tempTS + )
    }

    val recordSize = 1044L
    def recordStartByte(recNo: Int) = headerBytes + recordSize * recNo.toLong


//    val tempFirstPage


    return List[X]()

  }

  def simpleParser(text: String, valueName: String, default: String) = {
    val pattern = ("-" + valueName + """[ ]+(\S+)""").r
    pattern.findFirstIn(text) match {
      case Some(pattern(v)) => v
      case _ => default
    }

  }


}

abstract class XDataChannelNCS extends XDataChannelFilestream {

  val headerBytes = 16384L
  override def dataByteLocation(frame: Int) = recordStartByte(frame / 512) + (19) + ((frame % 512) * 2).toLong
  override val length = 1

  def readPointImpl(segment: Int, frame: Int): Int = ???

//  override val absGain: Double = _
//  override val absOffset: Double = _
//  override val absUnit: String = _
//  override val segments: Int = _
//  override val length: Vector[Int] = _
//  override val startTimestamp: Vector[Long] = _
//  override val sampleRate: Double = _
//  val channelName: String = _
}
