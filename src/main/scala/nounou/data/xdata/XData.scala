package nounou.data

import nounou._
import nounou.util.forJava
import scala.collection.immutable.Vector
import nounou.data.traits._

/** Base class for data encoded as Int arrays.
  * This object is mutable, to allow inheritance by [[nounou.data.filters.XDataFilter]].
  * Each trace of data must share the following variables:
  * sampling, start, length, xBits, absGain, absOffset, absUnit
  */
abstract class XData extends X with XConcatenatable with XFrames with XChannels with XAbsolute {


  //<editor-fold desc="reading a point">
  /** Read a single point from the data, in internal integer scaling, after checking values.
    * Implement via readPointImpl.
    */
  final def readPoint(channel: Int, frame: Int, segment: Int): Int = {
    require(isValidChannel(channel), "Invalid channel: " + channel.toString)
    //require(isValidFrame(frame, segment), "Invalid frame/segment: " + (frame, segment).toString)
    if( isValidFrame(frame, segment) ) readPointImpl(channel, frame, currentSegment = segment) else 0
  }

  /** Read a single point from current segment (or segment 0 if not initialized), in internal integer scaling.
    */
  final def readPoint(channel: Int, frame: Int): Int = readPoint(channel, frame, currentSegment)
  /** Read a single point from the data, in absolute unit scaling (as recorded).
    */
  final def readPointAbs(channel: Int, frame: Int, segment: Int): Double = toAbs(readPoint(channel, frame, segment))
  /** Read a single point from current segment (or segment 0 if not initialized), in absolute unit scaling (as recorded).
    */
  final def readPointAbs(channel: Int, frame: Int): Double = toAbs(readPoint(channel, frame))
  //</editor-fold>

  /** MUST OVERRIDE: Read a single point from the data, in internal integer scaling.
    */
  def readPointImpl(channel: Int, frame: Int, segment: Int): Int


  //<editor-fold desc="reading a trace">

  /** Read a single trace from current segment (or segment 0 if not initialized), in internal integer scaling.
    */
  final def readTrace(channel: Int): Vector[Int] = readTrace(channel, FrameRange.All, currentSegment)

  /** Read a single trace (within the span) from current segment (or segment 0 if not initialized), in internal integer scaling.
    */
  final def readTrace(channel: Int, range: FrameRange): Vector[Int] = readTrace(channel, range, currentSegment)

  /** Read a single trace from the data, in internal integer scaling.
    */
  final def readTrace(channel: Int, range: FrameRange, segment: Int): Vector[Int] = {

    require(isValidChannel(channel), "Invalid channel: " + channel.toString)

    //val realRange = range.getRangeWithoutNegativeIndexes( segmentLengths(segment) )
//    require(realRange.max < segmentLengths(segment), "Span is out of range, realRange.max >= segmentLengths(segment)!")
//    require(realRange.min >= 0, "Span is out of range, realRange.min < 0 !")
    val segLen =  segmentLengths(segment)
    val preLength = range.preLength( segLen )
    val postLength = range.postLength( segLen )

    vectZeros( preLength ) ++ readTraceImpl(channel, range.validRange( segLen ), (currentSegment = segment)) ++ vectZeros( postLength )
  }

  /** Read a single trace (within the span) from current segment (or segment 0 if not initialized), in absolute unit scaling (as recorded).
    */
  final def readTraceAbs(channel: Int, range: FrameRange = FrameRange.All): Vector[Double] = toAbs(readTrace(channel, range))

  /** Read a single trace (within the span) from the data, in absolute unit scaling (as recorded).
    */
  final def readTraceAbs(channel: Int, range: FrameRange, segment: Int): Vector[Double] = toAbs(readTrace(channel, range, segment))

  //</editor-fold>

//  /** CAN OVERRIDE: Read a single data trace from the data, in internal integer scaling.
//    * Should return a defensive clone.
//    */
//  def readTraceImpl(channel: Int, segment: Int): Vector[Int] = {
//    val res = new Array[Int]( length(segment) )
//    forJava(0, res.length, 1, (c: Int) => (res(c) = readPointImpl(channel, c, segment)))
//    res.toVector
//  }

  /** CAN OVERRIDE: Read a single data trace from the data, in internal integer scaling.
    * Should return a defensive clone.
    */
  def readTraceImpl(channel: Int, range: FrameRange, segment: Int): Vector[Int] = {
    //    span match {
    //      case Span.All => readTraceImpl(channel, segment)
    //      case _ => readTraceImpl(channel, span, segment)
    //    }
    val res = new Array[Int]( range.length )
    forJava(range.start, range.last + 1, range.step, (c: Int) => (res(c) = readPointImpl(channel, c, segment)))
    res.toVector
  }

  //<editor-fold desc="reading a frame">

  /** Read a single frame from the data, in internal integer scaling, for just the specified channels.
    */
  final def readFrame(frame: Int, channels: Vector[Int], segment: Int): Vector[Int] = {

    //require(isValidFrame(frame, segment), "Invalid frame/segment: " + (frame, segment).toString)
    require(channels.forall(isValidChannel), "Invalid channels: " + channels.toString)
    if( isValidFrame(frame, segment) ) readFrameImpl(frame, channels, (currentSegment = segment) ) else vectZeros( channels.length )

  }

  /** Read a single frame from the data, in internal integer scaling.
    */
  final def readFrame(frame: Int, segment: Int): Vector[Int] = {

    //require(isValidFrame(frame, segment), "Invalid frame/segment: " + (frame, segment).toString)
    if( isValidFrame(frame, segment) ) readFrameImpl(frame, (currentSegment = segment) ) else vectZeros( channelCount )

  }

  //</editor-fold>

  /** CAN OVERRIDE: Read a single frame from the data, in internal integer scaling.
    * Should return a defensive clone.
    */
  def readFrameImpl(frame: Int, segment: Int): Vector[Int] = {
    val res = new Array[Int](channelCount)
    forJava(0, channelCount, 1, (channel: Int) => res(channel) = readPointImpl(channel, frame, segment))
    res.toVector
  }
  /** CAN OVERRIDE: Read a single frame from the data, for just the specified channels, in internal integer scaling.
    * Should return a defensive clone.
    */
  def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int): Vector[Int] = {
    val res = new Array[Int]( channels.length )
    forJava(0, channels.length, 1, (channel: Int) => res(channel) = readPointImpl(channel, frame, segment))
    res.toVector
  }



  // <editor-fold defaultstate="collapsed" desc="XConcatenatable">

  override def isCompatible(that: X): Boolean = {
    that match {
      case x: XData => {
        (super[XFrames].isCompatible(x)) && (super[XAbsolute].isCompatible(x))
        //not channel info
      }
      case _ => false
    }
  }

  override def :::(x: X): XData

  // </editor-fold>

}

/** Immutable version of XData.
  *
  * Must override the following:
  * +  def readPointImpl(channel: Int, frame: Int, segment: Int): Int
  * +  (from XFramesImmutable)
  * ++   val segmentCount: Int
  * ++   val length: : Vector[Int]
  * ++   val segmentStartTSs: Vector[Long]
  * ++   val sampleRate: Double
  * +  (from XChannelsImmutable)
  * ++   val channelNames: Vector[String]
  * +  (from XAbsoluteImmutable)
  * ++   val absGain: Double
  * ++   val absOffset: Double
  * ++   val absUnit: String
  *
  * Can override the following:
  * +   def readTraceImpl(channel: Int, segment: Int): Vector[Int]
  * +   def readTraceImpl(channel: Int, span:Span, segment: Int): Vector[Int]
  * +   def readFrameImpl(frame: Int, segment: Int): Vector[Int]
  * +   def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int): Vector[Int]
  * +  (from XAbsoluteImmutable)
  * ++   val xBits: Int = 1024
  */
abstract class XDataImmutable extends XData with XFramesImmutable with XChannelsImmutable with XAbsoluteImmutable {
}

object XDataNull extends XDataImmutable{
  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = 0
  override val absGain: Double = 1D
  override val absOffset: Double = 0D
  override val absUnit: String = "Null unit"
  override val segmentLengths: Vector[Int] = Vector[Int]()
  override val segmentStartTSs: Vector[Long] = Vector[Long]()
  override val sampleRate: Double = 1D
  override val channelNames: Vector[String] = Vector[String]()

  override def :::(x: X): XDataImmutable = x match {
    case XDataNull => this
    case _ => require(false, "cannot append incompatible data types (XDataNull)"); this
  }
}
