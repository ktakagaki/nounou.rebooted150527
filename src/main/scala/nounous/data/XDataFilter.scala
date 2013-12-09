package nounous.data

import nounous.util.forJava
import scala.collection.immutable.Vector

/** Base class for Sources encoding data as Int arrays.
  * This object is immutable.
  * Each trace of data must share the following variables:
  * sampling, start, length, xBits, absGain, absOffset, absUnit
  */
abstract class XDataFilter( upstream: XData ) extends XData {

  override def segmentLength(seg: Int): Int = upstream.segmentLength(seg)

  def length: Int = upstream.length

  // frames
  override def startFrames: Vector[Int] = upstream.startFrames
  override def endFrames: Vector[Int] = upstream.endFrames
  override def segmentLengths: Vector[Int] = upstream.segmentLengths

  // timestamps
  override def startTimestamps: Vector[Long] = upstream.startTimestamps
  override def endTimestamps: Vector[Long] = upstream.endTimestamps

  // sampling rate information
  override def sampleRate: Double = upstream.sampleRate
  override def sampleInterval: Double = 1.0/sampleRate

  final lazy val timestampsPerFrame = sampleInterval * 1000000D
  final lazy val framesPerTimestamp = 1D/timestampsPerFrame

  // frameToTimestamp, timestampToFrame
  /** Timestamp of the given data frame index (in microseconds).
    */
  def frameToTimestamp(frame:Int): Long = {
    require( isValidFrame(frame) )
    if(segments == 1){
      (startTimestamps(0) + frame.toDouble * timestampsPerFrame).toLong
    } else {
      var tempret = 0L
      var seg = 0
      while(seg < segments - 1 && tempret == 0){
        if( startFrames(seg) <= frame && frame < startFrames(seg+1) ){
          tempret = startTimestamps(seg) + ((frame - startFrames(seg)).toDouble * timestampsPerFrame).toLong
        } else {
          seg += 1
        }
      }
      if(tempret == 0){
        startTimestamps(segments-1) + ((frame - startFrames(segments-1)).toDouble * timestampsPerFrame).toLong
      }
      tempret
    }
  }
  /** Closest frame index to the given timestamp. Will give beginning or end frames, if timestamp is
    * out of range.
    */
  def timestampToFrame(timestamp: Long): Int = {
    if(timestamp <= startTimestamps(0) ){
      0
    } else {
      var tempret = 0
      var seg = 0
      while(seg < segments - 1 && tempret == 0){
        if( timestamp < endTimestamps(seg) ){
          tempret = startFrames(seg) + ((timestamp-startTimestamps(seg)) * framesPerTimestamp).toInt
        } else if(timestamp < startTimestamps(seg+1)) {
          tempret = if(timestamp - endTimestamps(seg) < startTimestamps(seg+1) - timestamp) endFrames(seg) else startFrames(seg+1)
        } else {
          seg += 1
        }
      }
      if(tempret == 0){
        if(timestamp <= endTimestamps(segments -1)){
          tempret = startFrames(segments-1) + ((timestamp-startTimestamps(segments-1)) * framesPerTimestamp).toInt
        } else {
          tempret = endFrames(segments-1)
        }
      }
      tempret
    }
  }


  //channel information
  /**Get the name of a given channel.*/
  override def channelNames: Vector[String] = upstream.channelNames
  override def channelCount: Int = upstream.channelCount
  /** Is this channel valid?
    */
  final def isValidChannel(channel: Int) = (0 < channel && channel < channelCount)


  //internal data scaling information and absolute
  /**The number (eg 1024) multiplied to original raw data from the recording instrument
   *(usu 14-16 bit) to obtain internal Int representation.
   */
  val xBits : Int = 1024
  /**(xBits:Int).toDouble buffered, since it will be used often.
   */
  lazy val xBitsD: Double = xBits.toDouble

  /**Used to calculate the absolute value (mV, etc) based on internal representation.<p>
   * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(absolute value)=(internal value)*dataAbsoluteGain + dataAbsoluteOffset
   * absoluteGain must take into account the extra bits used to pad Int values. 
   */
  val absGain: Double
  
  /**Used to calculate the absolute value (mV, etc) based on internal representation.<p>
   * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(absolute value)=(internal value)*dataAbsoluteGain + dataAbsoluteOffset
   */
  val absOffset: Double
  
  /**The name of the absolute units, as a String (eg mv).
   */
  val absUnit: String

  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
   * absUnit (e.g. "mV")
   */
  def toAbs(data: Int) = data.toDouble * absGain + absOffset
  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
   * absUnit (e.g. "mV")
   */
  def toAbs(data: Vector[Int]): Vector[Double] = data.map( toAbs _ )
  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
   * absUnit (e.g. "mV")
   */
  def toAbs(data: Vector[Vector[Int]]): Vector[Vector[Double]] = data.map( toAbs _ )


  //reading a point
  /** OVERRIDE: Read a single point from the data, in internal integer scaling.
    */
  final def readPoint(channel: Int, frame: Int): Int = {
    require(isValidChannel(channel), "Invalid channel: " + channel.toString)
    require(isValidFrame(frame), "Invalid frame: " + frame.toString)
    readPointImpl(channel, frame)
  }
  /** Read a single point from the data, in absolute unit scaling (as recorded).
    */
  final def readPointAbs(channel: Int, frame: Int): Double = toAbs(readPoint(channel, frame))
  /** MUST OVERRIDE: Read a single point from the data, in internal integer scaling.
    */
  def readPointImpl(channel: Int, frame: Int): Int

  //reading a trace
  /** Read a single trace from the data, in internal integer scaling.
    */
  final def readTrace(channel: Int): Array[Int] = {
    require(isValidChannel(channel), "Invalid channel: " + channel.toString)
    readTraceImpl(channel)
  }
  /** Read a single trace (within the span) from the data, in internal integer scaling.
    */
  final def readTrace(channel: Int, span: Span): Vector[Int] = {
    span match {
      case Span.All => readTraceImpl(channel)
      case _ => readTraceImpl(channel, span)
    }
  }
  /** Read a single trace (within the span) from the data, in absolute unit scaling (as recorded).
    */
  final def readTraceAbs(channel: Int, span: Span = Span.All): Vector[Double] = toAbs(readTrace(channel, span))
  /** CAN OVERRIDE: Read a single data trace from the data, in internal integer scaling.
    * Should return a defensive clone.
    */
  def readTraceImpl(channel: Int): Vector[Int] = {
    val res = new Array[Int]( length )
    forJava(0, res.length, 1, (c: Int) => (res(c) = readPointImpl(channel, c)))
    res
  }
  /** CAN OVERRIDE: Read a single data trace from the data, in internal integer scaling.
    * Should return a defensive clone.
    */
  def readTraceImpl(channel: Int, span:Span): Vector[Int] = {
    val range = span.getRange( length )
    val res = new Array[Int]( range.length )
    forJava(range.start, range.end, range.step, (c: Int) => (res(c) = readPointImpl(channel, c)))
    res.toVector
  }

  //reading a frame
  final def read(channels: Vector[Int], frame: Int): Vector[Int] = {
    require(isValidFrame(frame), "Invalid frame: " + frame.toString)
    require(channels.forall(isValidChannel), "Invalid channels: " + channels.toString)
    readFrameImpl(channels, frame)
  }
  final def read(frame: Int): Vector[Int] = {
    require(isValidFrame(frame), "Invalid frame: " + frame.toString)
    readFrameImpl(frame)
  }
  def readFrameImpl(frame: Int): Vector[Int] = {
    val res = new Array[Int](channelCount)
    forJava(0, channelCount, 1, (channel: Int) => res(channel) = readPointImpl(channel, frame))
    res.toVector
  }
  def readFrameImpl(channels: Vector[Int], frame: Int): Vector[Int] = {
    val res = new Array[Int]( channels.length)
    forJava(0, channels.length, 1, (channel: Int) => res(channel) = readPointImpl(channel, frame))
    res.toVector
  }

  override def isCompatible(that: X): Boolean = {
    that match {
      case x: XDataFilter => {
        (this.absGain == x.absGain) && (this.absOffset == x.absOffset)&& (this.absUnit == x.absUnit) &&
          (this.length == x.length) &&
          (this.sampleRate == x.sampleRate) && (this.startFrames == x.startFrames) && (this.startTimestamps== x.startTimestamps) &&
          (this.xBits == x.xBits)
      }
      case _ => false
    }
  }

}
