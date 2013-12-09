package nounous.data

import nounous.util.forJava
import scala.collection.immutable.Vector

/** Base class for Sources encoding data as Int arrays.
  * This object is immutable.
  * Each trace of data must share the following variables:
  * sampling, start, length, xBits, absGain, absOffset, absUnit
  */
abstract class XData extends X {

  //segments, length, and isValidFrame
  /** Number of segments.
    */
  def segments: Int

  /**Total number of frames in each segment.
   */
  def length: Vector[Int]

  /** Is this frame valid?
    */
  final def isValidFrame(segment: Int, frame: Int) = (0 <= frame && frame < length(segment))

  //timestamps
  /** OVERRIDE: List of starting timestamps for each segment.
     */
  def startTimestamp: Vector[Long]
  /** OVERRIDE: End timestamp for each segment. Implement by overriding _endTimestamp
    */
  def endTimestamp: Vector[Long]

  //sampling rate information
  /**OVERRIDE: Sampling rate of frame data in Hz
    */
  def sampleRate: Double
  /**Buffered inverse of sampling, in seconds: Double
    */
  def sampleInterval = 1.0/sampleRate
  /**Buffered timestamps (microseconds) between frames.
    */
  def timestampsPerFrame = sampleInterval * 1000000D
  /**Buffered frames between timestamps (microseconds).
    */
  def framesPerTimestamp = 1D/timestampsPerFrame


  //frameToTimestamp, timestampToFrame
  /** Timestamp of the given data frame index (in microseconds).
    */
  final def frameToTimestamp(segment: Int, frame:Int): Long = {
    require( isValidFrame(segment, frame) )
    startTimestamp(segment) + (frame.toDouble * timestampsPerFrame).toLong
  }
  /** Closest frame index to the given timestamp. Will give beginning or end frames, if timestamp is
    * out of range.
    */
  final def timestampToFrame(timestamp: Long): (Int, Int) = {
    if(timestamp <= startTimestamp(0) ){
      (0, 0)
    } else {
      var tempret = (0, 0)
      var seg = 0
      while(seg < segments - 1 && tempret == (0, 0)){
        if( timestamp < endTimestamp(seg) ){
          tempret = (seg, ((timestamp-startTimestamp(seg)) * framesPerTimestamp).toInt)
        } else if(timestamp < startTimestamp(seg+1)) {
          tempret = if(timestamp - endTimestamp(seg) < startTimestamp(seg+1) - timestamp) (seg, length(seg)-1) else (seg + 1, 0)
        } else {
          seg += 1
        }
      }
      if(tempret == (0, 0)){
        if(timestamp <= endTimestamp(segments -1)){
          tempret = ( segments - 1, ((timestamp - startTimestamp(segments-1)) * framesPerTimestamp).toInt )
        } else {
          tempret = ( segments -1, length(segments-1)-1)
        }
      }
      tempret
    }
  }

  //channel information
  /**Get the name of a given channel.*/
  def channelName: Vector[String]
  def channelCount = channelName.length

  /** Is this channel valid?
    */
  final def isValidChannel(channel: Int) = (0 < channel && channel < channelCount)

  //internal data scaling and absolute
  /**The number (eg 1024) multiplied to original raw data from the recording instrument
   *(usu 14-16 bit) to obtain internal Int representation.
   */
  def xBits = 1024
  /**(xBits:Int).toDouble buffered, since it will be used often.
   */
  lazy val xBitsD = xBits.toDouble

  /**Used to calculate the absolute value (mV, etc) based on internal representation.<p>
   * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(absolute value)=(internal value)*dataAbsoluteGain + dataAbsoluteOffset
   * absoluteGain must take into account the extra bits used to pad Int values. 
   */
  def absGain: Double
  
  /**Used to calculate the absolute value (mV, etc) based on internal representation.<p>
   * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(absolute value)=(internal value)*dataAbsoluteGain + dataAbsoluteOffset
   */
  def absOffset: Double
  
  /**The name of the absolute units, as a String (eg mv).
   */
  def absUnit: String

  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
   * absUnit (e.g. "mV")
   */
  final def toAbs(data: Int) = data.toDouble * absGain + absOffset
  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
   * absUnit (e.g. "mV")
   */
  final def toAbs(data: Vector[Int]): Vector[Double] = data.map( toAbs _ )
  //ToDo 3: toAbs erasure
//  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
//   * absUnit (e.g. "mV")
//   */
//  final def toAbs(data: Vector[Vector[Int]]): Vector[Vector[Double]] = data.map( toAbs _ )
//  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
//    * absUnit (e.g. "mV")
//    */
//  final def toAbs(data: Vector[Vector[Vector[Int]]]): Vector[Vector[Vector[Double]]] = data.map( toAbs _ )



  //reading a point
  /** Read a single point from the data, in internal integer scaling, after checking values.
    * Implement via readPointImpl.
    */
  final def readPoint(segment: Int, channel: Int, frame: Int): Int = {
    require(isValidChannel(channel), "Invalid channel: " + channel.toString)
    require(isValidFrame(segment, frame), "Invalid segment/frame: " + (segment, frame).toString)
    readPointImpl(segment, channel, frame)
  }
  /** Read a single point from segment 0 of the data, in internal integer scaling.
    */
  final def readPoint0(channel: Int, frame: Int): Int = readPoint(0, channel, frame)
  /** Read a single point from the data, in absolute unit scaling (as recorded).
    */
  final def readPointAbs(segment: Int, channel: Int, frame: Int): Double = toAbs(readPoint(segment, channel, frame))
  /** Read a single point from segment 0 of the data, in absolute unit scaling (as recorded).
    */
  final def readPoint0Abs(channel: Int, frame: Int): Double = toAbs(readPoint0(channel, frame))
  /** MUST OVERRIDE: Read a single point from the data, in internal integer scaling.
    */
  def readPointImpl(segment: Int, channel: Int, frame: Int): Int

  //reading a trace
  /** Read a single trace from the data, in internal integer scaling.
    */
  final def readTrace(segment: Int, channel: Int): Vector[Int] = {
    require(isValidChannel(channel), "Invalid channel: " + channel.toString)
    readTraceImpl(segment, channel)
  }
  /** Read a single trace from segment 0 of the data, in internal integer scaling.
    */
  final def readTrace0(channel: Int): Vector[Int] = readTrace(0, channel)
  /** Read a single trace (within the span) from the data, in internal integer scaling.
    */
  final def readTrace(segment: Int, channel: Int, span: Span): Vector[Int] = {
    span match {
      case Span.All => readTraceImpl(segment, channel)
      case _ => readTraceImpl(segment, channel, span)
    }
  }
  /** Read a single trace (within the span) from segment 0 of the data, in internal integer scaling.
    */
  final def readTrace0(channel: Int, span: Span) = readTrace(0, channel, span)
  /** Read a single trace (within the span) from the data, in absolute unit scaling (as recorded).
    */
  final def readTraceAbs(segment: Int, channel: Int, span: Span = Span.All): Vector[Double] = toAbs(readTrace(segment, channel, span))
  /** Read a single trace (within the span) from segment 0 of the data, in absolute unit scaling (as recorded).
    */
  final def readTrace0Abs(channel: Int, span: Span = Span.All): Vector[Double] = readTraceAbs(0, channel, span)
  /** CAN OVERRIDE: Read a single data trace from the data, in internal integer scaling.
    * Should return a defensive clone.
    */
  def readTraceImpl(segment: Int, channel: Int): Vector[Int] = {
    val res = new Array[Int]( length(segment) )
    forJava(0, res.length, 1, (c: Int) => (res(c) = readPointImpl(segment, channel, c)))
    res.toVector
  }
  /** CAN OVERRIDE: Read a single data trace from the data, in internal integer scaling.
    * Should return a defensive clone.
    */
  def readTraceImpl(segment: Int, channel: Int, span:Span): Vector[Int] = {
    val range = span.getRange( length(segment) )
    val res = new Array[Int]( range.length )
    forJava(range.start, range.end, range.step, (c: Int) => (res(c) = readPointImpl(segment, channel, c)))
    res.toVector
  }

  //reading a frame
  /** Read a single frame from the data, in internal integer scaling, for just the specified channels.
    */
  final def readFrame(segment: Int, frame: Int, channels: Vector[Int]): Vector[Int] = {
    require(isValidFrame(segment, frame), "Invalid segment/frame: " + (segment, frame).toString)
    require(channels.forall(isValidChannel), "Invalid channels: " + channels.toString)
    readFrameImpl(segment, frame, channels)
  }
  /** Read a single frame from the data, in internal integer scaling.
    */
  final def readFrame(segment: Int, frame: Int): Vector[Int] = {
    require(isValidFrame(segment, frame), "Invalid segment/frame: " + (segment, frame).toString)
    readFrameImpl(segment, frame)
  }
  /** CAN OVERRIDE: Read a single frame from the data, in internal integer scaling.
    * Should return a defensive clone.
    */
  def readFrameImpl(segment: Int, frame: Int): Vector[Int] = {
    val res = new Array[Int](channelCount)
    forJava(0, channelCount, 1, (channel: Int) => res(channel) = readPointImpl(segment, channel, frame))
    res.toVector
  }
  /** CAN OVERRIDE: Read a single frame from the data, for just the specified channels, in internal integer scaling.
    * Should return a defensive clone.
    */
  def readFrameImpl(segment: Int, frame: Int, channels: Vector[Int]): Vector[Int] = {
    val res = new Array[Int]( channels.length )
    forJava(0, channels.length, 1, (channel: Int) => res(channel) = readPointImpl(segment, channel, frame))
    res.toVector
  }

  override def isCompatible(that: X): Boolean = {
    that match {
      case x: XData => {
        (this.segments == x.segments) &&(this.length == x.length) &&
          (this.startTimestamp == x.startTimestamp) &&
          (this.sampleRate == x.sampleRate) &&
          //not channel info
          (this.xBits == x.xBits) && (this.absGain == x.absGain) && (this.absOffset == x.absOffset) && (this.absUnit == x.absUnit)
      }
      case _ => false
    }
  }

}
