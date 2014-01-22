package nounous.data.xdata

import nounous.data.traits._
import nounous.data.{Span, X}
import nounous.util.forJava
import scala.collection.immutable.Vector

/** Base class for data encoded as Int arrays.
  * This object is mutable, to allow inheritance by XDataFIlter.
  * Each trace of data must share the following variables:
  * sampling, start, length, xBits, absGain, absOffset, absUnit
  */
abstract class XData extends X with XFrames with XChannels with XAbsolute {

  //<editor-fold desc="reading a point">

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
  //</editor-fold>
  /** MUST OVERRIDE: Read a single point from the data, in internal integer scaling.
    */
  def readPointImpl(segment: Int, channel: Int, frame: Int): Int


  //<editor-fold desc="reading a trace">

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
  //</editor-fold>
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

  //<editor-fold desc="reading a frame">

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
  //</editor-fold>
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
        (super[XFrames].isCompatible(x)) && (super[XAbsolute].isCompatible(x))
        //not channel info
      }
      case _ => false
    }
  }

}

/** Immutable version of XData.
  *
  * Must override the following:
  * +  def readPointImpl(segment: Int, channel: Int, frame: Int): Int
  * +  (from XFramesImmutable)
  * ++   val segments: Int
  * ++   val length: : Vector[Int]
  * ++   val startTimestamp: Vector[Long]
  * ++   val sampleRate: Double
  * +  (from XChannelsImmutable)
  * ++   val channelName: Vector[String]
  * +  (from XAbsoluteImmutable)
  * ++   val absGain: Double
  * ++   val absOffset: Double
  * ++   val absUnit: String
  *
  * Can override the following:
  * +   def readTraceImpl(segment: Int, channel: Int): Vector[Int]
  * +   def readTraceImpl(segment: Int, channel: Int, span:Span): Vector[Int]
  * +   def readFrameImpl(segment: Int, frame: Int): Vector[Int]
  * +   def readFrameImpl(segment: Int, frame: Int, channels: Vector[Int]): Vector[Int]
  * +  (from XAbsoluteImmutable)
  * ++   val xBits: Int = 1024
  */
abstract class XDataImmutable extends XData with XFramesImmutable with XChannelsImmutable with XAbsoluteImmutable {
}
