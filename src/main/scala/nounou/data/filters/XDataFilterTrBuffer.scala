package nounou.data.filters

import nounou._
import breeze.linalg.RangeExtender
import scala.collection.mutable.{ArrayBuffer, HashMap}
import com.typesafe.scalalogging.slf4j.Logging

/** Buffer filter, which will save intermediate calculation results for an XData object.
  */
trait XDataFilterTrBuffer extends XDataFilterTr {

  private val buffer: HashMap[(Int, Int, Int), Vector[Int]] = new ReadingHashMapBuffer()
  private val garbageQue: ArrayBuffer[(Int, Int, Int)] = new ArrayBuffer[(Int, Int, Int)]()

  lazy val bufferPageLength: Int = (32768 / 2) //default page length will be 32 kB
  lazy val garbageQueBound: Int = 1073741824 / 8 / (bufferPageLength * 2)  //default buffer maximum size will be 128 MB

  logger.debug("initialized XDataFilterTrBuffer w/ bufferPageLength={} and garbageQueBound={}", bufferPageLength.toString, garbageQueBound.toString)


  def flushBuffer(): Unit = {
    logger.debug( "flushBuffer() pre, buffer.size={}, garbageQue.length={}", buffer.size.toString, garbageQue.length.toString )
    buffer.clear()
    garbageQue.clear()
  }

  def getBufferPage(frame: Int) = frame/bufferPageLength
  def getBufferIndex(frame: Int) = frame%bufferPageLength

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = {
    buffer( (channel, getBufferPage(frame), segment) )( getBufferIndex(frame) )
  }
  override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): Vector[Int] = {
    val totalLength = segmentLengths(segment)
    val tempret = ArrayBuffer[Int]()
      tempret.sizeHint(range.length )
    val startPage =  getBufferPage( range.start)
    val startIndex = getBufferIndex(range.start)
    val endPage =    getBufferPage( range.end )
    val endIndex =   getBufferIndex(range.end )

    if(startPage == endPage) buffer( (channel, startPage, segment) ).slice(startIndex, endIndex)
    else {
      tempret ++= buffer( (channel, startPage, segment) ).slice(startIndex, bufferPageLength-1)  //deal with startPage separately
      if( startPage + 1 <= endPage ) {
        for(page <- startPage + 1 to endPage - 1) {
          tempret ++= buffer( (channel, page, segment) )
        }
      }
      tempret ++= buffer( (channel, endPage, segment) ).slice(0, endIndex)  //deal with endPage separately
      tempret.toVector
    }
  }

  //redirection function to deal with scope issues regarding super
  private def tempTraceReader(ch: Int, range: Range.Inclusive, segment: Int) = super.readTraceImpl(ch, range, segment)

  class ReadingHashMapBuffer extends HashMap[(Int, Int, Int), Vector[Int]] {

    override def default( key: (Int, Int, Int)  ) = {
      val startFrame = key._2 * bufferPageLength
      val endFramePlusOne: Int = scala.math.min( startFrame + bufferPageLength, segmentLengths( key._3 ) )
      if(garbageQue.size >= garbageQueBound ) garbageQue.drop(1)
      garbageQue.append( key )
      tempTraceReader( key._1, new Range.Inclusive(startFrame, endFramePlusOne, 1), key._3  )
    }
  }

}
