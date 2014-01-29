package nounou.data.xdata

import nounou.data.Span
import scala.collection.immutable.{HashMap, Vector}
import scala.collection.mutable

/** Base class for classes taking an xdata object, modifying it in some way, and responding to queries for data with this
  * modified information. This class is mutable---the parent object can be changed, as can the internal data.
  * The default implementation is that all variables are just passed through from the parent object
  * with buffering for simple variables, but not for data. You just need to override the information that is changed.
  */
class XDataFilterBufferTrace( override var upstream: XData, val buffPageLength: Int = 16000, val buffLengthLimit: Int = 10000 ) extends XDataFilter( upstream ) {

  var buff: Array[HashMap[Int, Vector[Int]]] = ???
  var buffStack: mutable.Stack[(Int, Int)] = ???

  override def flushBuffer(): Unit = {
    super[XDataFilter].flushBuffer()
    buff = (for(ch <- 0 until channelCount) yield ( new HashMap[Int, Vector[Int]] )).toArray
    buffStack = new mutable.SynchronizedStack[(Int, Int)]
  }

  flushBuffer()

//  override def readPointImpl(segment: Int, channel: Int, frame: Int): Int = upstream.readPointImpl(segment, channel, frame)
//  override def readTraceImpl(segment: Int, channel: Int) = upstream.readTraceImpl(segment, channel)
//  override def readTraceImpl(segment: Int, channel: Int, span: Span) = upstream.readTraceImpl(segment, channel, span)
//  override def readFrameImpl(segment: Int, frame: Int) = upstream.readFrameImpl(segment, frame)
//  override def readFrameImpl(segment: Int, frame: Int, channels: Vector[Int]) = upstream.readFrameImpl(segment, frame, channels)


}
