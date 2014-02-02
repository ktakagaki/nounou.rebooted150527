package nounou.data.filters

import scala.collection.mutable.{ArrayBuffer, HashMap}
import scala.math.min
import nounou.data.Span

/** Base class for classes taking an xdata object, modifying it in some way, and responding to queries for data with this
  * modified information. This class is mutable---the parent object can be changed, as can the internal data.
  * The default implementation is that all variables are just passed through from the parent object
  * with buffering for simple variables, but not for data. You just need to override the information that is changed.
  */
trait XDataFilterBufferTrace extends XDataFilter {

  private val buffer: HashMap[(Int, Int, Int), Vector[Int]] = new ReadingHashMapBuffer()
  private val garbageQue: ArrayBuffer[(Int, Int, Int)] = new ArrayBuffer[(Int, Int, Int)]()

  lazy val bufferPageLength: Int = (32768 / 2) //default page length will be 32 kB
  lazy val garbageQueBound: Int = 1073741824 / 8 / (bufferPageLength * 2)  //default buffer maximum size will be 128 MB

  def flushBuffer(): Unit = {
    buffer.clear()
    garbageQue.clear()
  }

  def getBufferPage(frame: Int) = frame/bufferPageLength
  def getBufferIndex(frame: Int) = frame%bufferPageLength

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = {
    buffer( (channel, getBufferPage(frame), segment) )( getBufferIndex(frame) )
  }
  override def readTraceImpl(channel: Int, span: Span, segment: Int): Vector[Int] = {
    val tempret = ArrayBuffer[Int]()
    val (startFrame, endFrame) = span.getStartEndIndexes( segmentLengths(segment) )
    val startPage = getBufferPage(startFrame)
    val startIndex = getBufferIndex(startFrame)
    val endPage = getBufferPage(endFrame)
    val endIndex = getBufferIndex(endFrame)

    if(startPage == endPage) buffer( (channel, startPage, segment) ).slice(startIndex, endIndex)
    else {
      tempret ++= buffer( (channel, startPage, segment) ).slice(startFrame, bufferPageLength-1)  //deal with startPage separately
      if( startPage + 1 <= endPage ) {
        for(page <- startPage + 1 to endPage - 1) {
          tempret ++= buffer( (channel, page, segment) )
        }
      }
      tempret ++= buffer( (channel, endPage, segment) ).slice(0, endFrame)  //deal with endPage separately
      tempret.toVector
    }
  }

  //redirection function to deal with scope issues regarding super
  private def tempTraceReader(ch: Int, span: Span, segment: Int) = super.readTraceImpl(ch, span, segment)

  class ReadingHashMapBuffer extends HashMap[(Int, Int, Int), Vector[Int]] {

    override def default( key: (Int, Int, Int)  ) = {
      val startFrame = key._2 * bufferPageLength
      val endFramePlusOne: Int = scala.math.min( startFrame + bufferPageLength, segmentLengths( key._3 ) )
      if(garbageQue.size >= garbageQueBound ) garbageQue.drop(1)
      garbageQue.append( key )
      tempTraceReader( key._1, new Span(startFrame, endFramePlusOne), key._3 )
    }
  }
}
