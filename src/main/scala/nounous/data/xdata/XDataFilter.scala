package nounous.data.xdata

import nounous.data.Span
import nounous.util.forJava
import scala.collection.immutable.Vector

/** Base class for classes taking an xdata object, modifying it in some way, and responding to queries for data with this
  * modified information. This class is mutable---the parent object can be changed, as can the internal data.
  * The default implementation is that all variables are just passed through from the parent object
  * with buffering for simple variables. You just need to override the information that is changed.
  */
class XDataFilter( protected var upstream: XData ) extends XData {

  def flushBuffer(): Unit = {
    //segments, length
    _segments = upstream.segments
    _length = upstream.length

    //timestamps
    _startTimestamp = upstream.startTimestamp
    _endTimestamp = upstream.endTimestamp

    //sample rate information: override flush from xdata
    _sampleRate = upstream.sampleRate
    _sampleInterval = upstream.sampleInterval
    _timestampsPerFrame = upstream.timestampsPerFrame
    _framesPerTimestamp = upstream.framesPerTimestamp

    //channel information
    _channelName = upstream.channelName
    _channelCount = upstream.channelCount

    //internal data scaling and absolute
    _xBits = upstream.xBits
    _absGain = upstream.absGain
    _absOffset = upstream.absOffset
    _absUnit = upstream.absUnit

  }


  //segments, length
  override def segments = _segments
  protected var _segments: Int = _
  override def length = _length
  var _length: Vector[Int] = _

  //timestamps
  override def startTimestamp = _startTimestamp
  var _startTimestamp: Vector[Long] = _ // change from def to var
  override def endTimestamp = _endTimestamp
  var _endTimestamp: Vector[Long] = _ // change from def to var

  //sample rate information
  override def sampleRate: Double = _sampleRate
  protected var _sampleRate: Double = _
  override def sampleInterval: Double = _sampleInterval
  protected var _sampleInterval: Double = _
  override def timestampsPerFrame: Double = _timestampsPerFrame
  protected var _timestampsPerFrame: Double = _
  override def framesPerTimestamp: Double = _framesPerTimestamp
  protected var _framesPerTimestamp: Double = _

  //channel information
  override def channelName = _channelName
  var _channelName: Vector[String] = _ // change from val to var
  override def channelCount = _channelCount
  protected var _channelCount: Int = _

  //internal data scaling and absolute
  override def xBits = _xBits
  protected var _xBits: Int = _
  override def absGain  = _absGain
  protected var _absGain: Double = _
  override def absOffset  = _absOffset
  protected var _absOffset: Double = _
  override def absUnit  = _absUnit
  protected var _absUnit: String = _

  flushBuffer()

  override def readPointImpl(segment: Int, channel: Int, frame: Int): Int = upstream.readPointImpl(segment, channel, frame)
  override def readTraceImpl(segment: Int, channel: Int) = upstream.readTraceImpl(segment, channel)
  override def readTraceImpl(segment: Int, channel: Int, span: Span) = upstream.readTraceImpl(segment, channel, span)
  override def readFrameImpl(segment: Int, frame: Int) = upstream.readFrameImpl(segment, frame)
  override def readFrameImpl(segment: Int, frame: Int, channels: Vector[Int]) = upstream.readFrameImpl(segment, frame, channels)


}
