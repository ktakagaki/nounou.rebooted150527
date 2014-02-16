package nounou.data

import nounou._
import scala.Vector
import java.io.DataInput
import nounou.data.traits.{XConcatenatable, XFramesImmutable, XAbsoluteImmutable}

/**
 * Created by Kenta on 12/14/13.
 */
abstract class XDataChannel extends X with XFramesImmutable with XAbsoluteImmutable with XConcatenatable {

  /**MUST OVERRIDE: name of the given channel.*/
  val channelName: String

  //<editor-fold desc="reading a point">
  /** Read a single point from the data, in internal integer scaling, after checking values.
    * Implement via readPointImpl.
    */
  final def readPoint(frame: Int, segment: Int): Int = {
    //require(isValidFrame(frame, segment), "Invalid frame/segment: " + (frame, segment).toString)
    if( isValidFrame(frame, segment) ) readPointImpl(frame, currentSegment = segment) else 0
  }
  //</editor-fold>

  /** MUST OVERRIDE: Read a single point from the data, in internal integer scaling.
    */
  def readPointImpl(frame: Int, segment: Int): Int

  //<editor-fold desc="reading a trace">
  /** Read a single trace from the data, in internal integer scaling.
    */
  final def readTrace(segment: Int): Vector[Int] = {
    val range = FrameRange.All.getValidRange( segmentLengths(segment) )
    readTraceImpl(range, currentSegment = segment)
  }
  /** Read a single trace (within the span) from the data, in internal integer scaling.
    */
  final def readTrace(range: FrameRange, segment: Int): Vector[Int] = {

    val segLen =  segmentLengths(segment)
    val preLength = range.preLength( segLen )
    val postLength = range.postLength( segLen )

    vectZeros( preLength ) ++ readTraceImpl(range.getValidRange( segLen ), (currentSegment = segment)) ++ vectZeros( postLength )

    //val span = range.getRangeWithoutNegativeIndexes( segmentLengths(segment) )
    //readTraceImpl(span, currentSegment = segment)
//    span match {
//      case Span.All => readTraceImpl(currentSegment = segment)
//      case _ => readTraceImpl(span, currentSegment = segment)
//      }
  }
  //</editor-fold>

//  /** CAN OVERRIDE: Read a single data trace from the data, in internal integer scaling.
//    * Should return a defensive clone.
//    */
//  def readTraceImpl(segment: Int): Vector[Int] = readTraceImpl(Span.All, segment)
//
  /** CAN OVERRIDE: Read a single data trace from the data, in internal integer scaling.
    * Should return a defensive clone.
    */
  def readTraceImpl(range: Range.Inclusive, segment: Int): Vector[Int] = {
    //Impls only get real ranges
    //val realRange = range.getRangeWithoutNegativeIndexes( segmentLengths(segment) )
    val totalLengths = segmentLengths( segment )
    val res = new Array[Int]( range.length )
    forJava(range.start, range.end + 1, range.step, (c: Int) => (res(c) = readPointImpl(c, segment)))
    res.toVector
  }

  // <editor-fold desc="XConcatenatable">

  override def isCompatible(that: X): Boolean = {
    that match {
      case x: XDataChannel => {
        (super[XFramesImmutable].isCompatible(x) && super[XAbsoluteImmutable].isCompatible(x))
      }
      case _ => false
    }
  }

  override def :::(that: X): XDataChannelArray = {
    that match {
      case t: XDataChannelArray => {
        if(this.isCompatible(t(0))){
          new XDataChannelArray( t.array.+:(this) )
        } else {
          throw new IllegalArgumentException("types are not compatible, and cannot be concatenated.")
        }
      }
      case t: XDataChannelNull => new XDataChannelArray( this )
      case t: XDataChannel => {
        if(this.isCompatible(t)){
          new XDataChannelArray( Vector(this, t) )
        } else {
          throw new IllegalArgumentException("the XDataChannelArray type and XDataChannel type are not compatible, and cannot be concatenated.")
        }
      }
      case _ => throw new IllegalArgumentException("the two X types are not compatible, and cannot be concatenated.")
    }
  }

  // </editor-fold>

  override def toString() = {
    "XDataChannel( " + segmentCount + " segments, with lengths " + segmentLengths + ", fs=" + sampleRate + ")"
  }

}

abstract class XDataChannelFilestream extends XDataChannel {

  val fileHandle: DataInput

}

class XDataChannelNull extends XDataChannel {
  val channelName: String = "XDataChannelNull()"

  def readPointImpl(frame: Int, segment: Int): Int = 0

  override val absGain: Double = 1d
  override val absOffset: Double = 0d
  override val absUnit: String = "XDataChannelNull"
  override val segmentLengths: Vector[Int] = Vector[Int]()
  override val segmentStartTSs: Vector[Long] = Vector(0L)
  override val sampleRate: Double = 1d
}

class XDataChannelPreloaded(val data: Vector[Int],
                            val channelName: String,
                            override val absGain: Double,
                            override val absUnit: String,
                            override val segmentLengths: Vector[Int],
                            override val sampleRate: Double,
                            override val absOffset: Double,
                            override val segmentStartTSs: Vector[Long] )  extends XDataChannel {

  def readPointImpl(frame: Int, segment: Int): Int = 0

}
