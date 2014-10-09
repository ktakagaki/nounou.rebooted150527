package nounou.data

import nounou._
import scala.Vector
import java.io.DataInput
import nounou.data.traits.{XDataTimingImmutable, XConcatenatable, XDataTiming, XDataScale}
import breeze.linalg.{DenseVector => DV}
import nounou.data.ranges.{RangeFrAll, RangeFr}

/**
 * Created by Kenta on 12/14/13.
 */
abstract class XDataChannel extends X with XDataTiming with XDataScale with XConcatenatable {

  /**MUST OVERRIDE: name of the given channel.*/
  val channelName: String

  //<editor-fold desc="reading a point">
  /** Read a single point from the data, in internal integer scaling, after checking values.
    * Implement via readPointImpl.
    */
  final def readPoint(frame: Int, segment: Int): Int = {
    //require(isValidFr(frame, segment), "Invalid frame/segment: " + (frame, segment).toString)
    if( isValidFrsg(frame, segment) ) readPointImpl(frame, segment) else 0
  }
  //</editor-fold>

  /** MUST OVERRIDE: Read a single point from the data, in internal integer scaling.
    */
  def readPointImpl(frame: Int, segment: Int): Int

  //<editor-fold desc="reading a trace">
  /** Read a single trace from the data, in internal integer scaling.
    */
  final def readTrace(segment: Int): DV[Int] = {
    val range = RangeFrAll().getValidRange( this )
    readTraceImpl(range, segment)
  }
  /** Read a single trace (within the span) from the data, in internal integer scaling.
    */
  final def readTrace(range: RangeFr): DV[Int] = {

    val seg = range.getRealSegment(this)
    val segLen =  segmentLength(seg)
    val preLength = range.preLength( segLen )
    val postLength = range.postLength( segLen )



    DV.vertcat( DV.zeros[Int]( preLength ), readTraceImpl(range.getValidRange(this), seg), DV.zeros[Int]( postLength ) )

  }
  //</editor-fold>

  /** CAN OVERRIDE: Read a single data trace from the data, in internal integer scaling.
    * Should return a defensive clone.
    */
  def readTraceImpl(range: Range.Inclusive, segment: Int): DV[Int] = {
    //Impls only get real nounou.data.ranges
    //val realRange = range.getRangeWithoutNegativeIndexes( segmentLengths(segment) )
    //val totalLengths = segmentLength( segment )
    val res = DV.zeros[Int](range.length) //new Array[Int]( range.length )
    nounou.util.forJava(range.start, range.end + 1, range.step, (c: Int) => (res(c) = readPointImpl(c, segment)))
    res
  }

  // <editor-fold desc="XConcatenatable">

  override def isCompatible(that: X): Boolean = {
    that match {
      case x: XDataChannel => {
        println(super[XDataTiming].isCompatible(x))
        super[XDataTiming].isCompatible(x) &&
          super[XDataScale].isCompatible(x)
      }
      case _ => false
    }
  }

  override def :::(that: X): XDataChannelArray = {
    that match {
      case t: XDataChannelArray => {
        if(this.isCompatible(t(0))){
          new XDataChannelArray( t.array :+ this )
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
    "XDataChannel( " + segmentCount + " segments, with lengths " + segmentLength + ", fs=" + sampleRate + ")"
  }

}

abstract class XDataChannelFilestream extends XDataChannel {

  val fileHandle: DataInput

}

class XDataChannelNull extends XDataChannel {
  val channelName: String = "XDataChannelNull()"

  override def readPointImpl(frame: Int, segment: Int): Int = 0

  override val absGain: Double = 1d
  override val absOffset: Double = 0d
  override val absUnit: String = "XDataChannelNull"
  override val scaleMax: Int = 0
  override val scaleMin: Int = 0
  override val segmentLength: Array[Int] = Array[Int]()
  override val segmentStartTs: Array[Long] = Array(0L)
  override val sampleRate: Double = 1d

  /** Number of segments in data.
    */
  override val segmentCount: Int = 0

  /** OVERRIDE: End timestamp for each segment. Implement by overriding _endTimestamp
    */
  override val segmentEndTs: Array[Long] = Array[Long]()
}

class XDataChannelPreloaded(val data: Array[DV[Int]],
                            override val xBits: Int,
                            override val absGain: Double,
                            override val absUnit: String,
                            override val absOffset: Double,
                            override val scaleMax: Int,
                            override val scaleMin: Int,
                            override val channelName: String,
                            override val segmentStartTs: Array[Long],
                            override val sampleRate: Double
 )  extends XDataChannel with XDataTimingImmutable{

  loggerRequire(data.length == segmentStartTs.length,
    "Given data and segmentStartTs must have the same length. Actual lengths were {}, {}.",
    data.length.toString, segmentStartTs.length.toString)

  override lazy val segmentLength = Array( data.length )
  override def readPointImpl(frame: Int, segment: Int): Int = data(segment)(frame)

}

class XDataChannelPreloadedSingleSegment
                   (data: DV[Int],
                    xBits: Int,
                    absGain: Double,
                    absUnit: String,
                    absOffset: Double,
                    scaleMax: Int,
                    scaleMin: Int,
                    channelName: String,
                    segmentStartTS: Long,
                    sampleRate: Double
                    )
  extends XDataChannelPreloaded(Array(data), xBits, absGain, absUnit, absOffset, scaleMax, scaleMin, channelName, Array(segmentStartTS), sampleRate)
