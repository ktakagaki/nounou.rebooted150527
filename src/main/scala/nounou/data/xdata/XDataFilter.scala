package nounou.data

import nounou.util.forJava
import scala.collection.immutable.Vector

/** Base class for classes taking an xdata object, modifying it in some way, and responding to queries for data with this
  * modified information. This class is mutable---the parent object can be changed, as can the internal data.
  * The default implementation is that all variables are just passed through from the parent object
  * with buffering for simple variables, but not for data. You just need to override the information that is changed.
  */
class XDataFilter( var _upstream: XData ) extends XData {

  def upstream = _upstream
  def upstream_= ( newUpstream: XData): Unit = {
    _upstream =  newUpstream
    flushBuffer
  }

  def flushBuffer(): Unit = {
    //segmentCount, length
    _segments = upstream.segmentCount
    _length = upstream.segmentLengths

    //timestamps
    _startTimestamp = upstream.segmentStartTSs
    _endTimestamp = upstream.segmentEndTSs

    //sample rate information: override flush from xdata
    _sampleRate = upstream.sampleRate
    _sampleInterval = upstream.sampleInterval
    _timestampsPerFrame = upstream.tsPerFrame
    _framesPerTimestamp = upstream.framesPerTS

    //channel information
    _channelName = upstream.channelNames
    _channelCount = upstream.channelCount

    //internal data scaling and absolute
    _xBits = upstream.xBits
    _absGain = upstream.absGain
    _absOffset = upstream.absOffset
    _absUnit = upstream.absUnit

  }


  //segmentCount, length
  override def segmentCount = _segments
  protected var _segments: Int = _
  override def segmentLengths = _length
  var _length: Vector[Int] = _

  //timestamps
  override def segmentStartTSs = _startTimestamp
  var _startTimestamp: Vector[Long] = _ // change from def to var
  override def segmentEndTSs = _endTimestamp
  var _endTimestamp: Vector[Long] = _ // change from def to var

  //sample rate information
  override def sampleRate: Double = _sampleRate
  protected var _sampleRate: Double = _
  override def sampleInterval: Double = _sampleInterval
  protected var _sampleInterval: Double = _
  override def tsPerFrame: Double = _timestampsPerFrame
  protected var _timestampsPerFrame: Double = _
  override def framesPerTS: Double = _framesPerTimestamp
  protected var _framesPerTimestamp: Double = _

  //channel information
  override def channelNames = _channelName
  var _channelName: Vector[String] = _ // change from def to var
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

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = upstream.readPointImpl(channel, frame, segment)
//  override def readTraceImpl(channel: Int, segment: Int) = upstream.readTraceImpl(channel, segment)
  override def readTraceImpl(channel: Int, span: Span, segment: Int) = upstream.readTraceImpl(channel, span, segment)
  override def readFrameImpl(frame: Int, segment: Int) = upstream.readFrameImpl(frame, segment)
  override def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int) = upstream.readFrameImpl(frame, channels, segment)

  override def :::(target: X): XData = {
    require(false, "cannot append an XDataFilter!")
    this
  }
}
