package nounou.elements.data

import nounou._
import nounou.elements.NNElement
import java.io.DataInput
import nounou.elements.traits.{NNDataTimingImmutable, NNDataTiming, NNDataScale, NNConcatenatable}
import breeze.linalg.{DenseVector => DV}
import nounou.elements.ranges.{SampleRangeSpecifier, SampleRangeValid, SampleRangeAll}

/**
 * Created by Kenta on 12/14/13.
 */
abstract class NNDataChannel extends NNElement
                              with NNDataTiming with NNDataScale with NNConcatenatable {

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
  final def readTrace(segment: Int): Array[Int] = {
    val range = NN.SampleRangeAll().getSampleRangeValid( this )
    readTraceDVImpl(range).toArray
  }
  /** Read a single trace (within the span) from the data, in internal integer scaling.
    */
  final def readTraceDV(range: SampleRangeSpecifier): DV[Int] = {

    val (preLength, seg, postLength) = range.getSampleRangeValidPrePost(this)
//    val segLen =  segmentLength(seg)
//    val preLength = range.preLength( segLen )
//    val postLength = range.postLength( segLen )

    DV.vertcat( DV.zeros[Int]( preLength ), readTraceDVImpl(seg), DV.zeros[Int]( postLength ) )

  }
  //</editor-fold>

  /** CAN OVERRIDE: Read a single data trace from the data, in internal integer scaling.
    * Should return a defensive clone.
    */
  def readTraceDVImpl(range: SampleRangeValid): DV[Int] = {
    //Impls only get real nounou.data.ranges
    //val realRange = range.getRangeWithoutNegativeIndexes( segmentLengths(segment) )
    //val totalLengths = segmentLength( segment )
    val res = DV.zeros[Int](range.length) //new Array[Int]( range.length )
    nounou.util.forJava(range.start, range.last + 1, range.step, (c: Int) => (res(c) = readPointImpl(c, range.segment)))
    res
  }

  // <editor-fold desc="XConcatenatable">

  override def isCompatible(that: NNElement): Boolean = {
    that match {
      case x: NNDataChannel => {
        println(super[NNDataTiming].isCompatible(x))
        super[NNDataTiming].isCompatible(x) &&
          super[NNDataScale].isCompatible(x)
      }
      case _ => false
    }
  }

  override def :::(that: NNElement): NNDataChannelArray = {
    that match {
      case t: NNDataChannelArray => {
        if(this.isCompatible(t(0))){
          new NNDataChannelArray( t.array :+ this )
        } else {
          throw new IllegalArgumentException("types are not compatible, and cannot be concatenated.")
        }
      }
      case t: NNDataChannelNull => new NNDataChannelArray( this )
      case t: NNDataChannel => {
        if(this.isCompatible(t)){
          new NNDataChannelArray( Vector(this, t) )
        } else {
          throw new IllegalArgumentException("the XDataChannelArray type and XDataChannel type are not compatible, and cannot be concatenated.")
        }
      }
      case _ => throw new IllegalArgumentException("the two X types are not compatible, and cannot be concatenated.")
    }
  }

  // </editor-fold>

  override def toString() = {
    "XDataChannel( " + segmentCount + " segments, fs=" + sampleRate + ")"
  }

}

abstract class NNDataChannelFilestream extends NNDataChannel {

  val fileHandle: DataInput

}

class NNDataChannelNull extends NNDataChannel {
  val channelName: String = "XDataChannelNull()"

  override def readPointImpl(frame: Int, segment: Int): Int = 0

  override val absGain: Double = 1d
  override val absOffset: Double = 0d
  override val absUnit: String = "XDataChannelNull"
  override val scaleMax: Int = 0
  override val scaleMin: Int = 0
  override def segmentLengthImpl(segment: Int): Int = 0
  override val segmentStartTs: Array[Long] = Array(0L)
  override val sampleRate: Double = 1d

  /** Number of segments in data.
    */
  override val segmentCount: Int = 0

  /** OVERRIDE: End timestamp for each segment. Implement by overriding _endTimestamp
    */
  override val segmentEndTs: Array[Long] = Array[Long]()
}

class NNDataChannelPreloaded(val data: Array[DV[Int]],
                            override val xBits: Int,
                            override val absGain: Double,
                            override val absUnit: String,
                            override val absOffset: Double,
                            override val scaleMax: Int,
                            override val scaleMin: Int,
                            override val channelName: String,
                            override val segmentStartTs: Array[Long],
                            override val sampleRate: Double
 )  extends NNDataChannel with NNDataTimingImmutable {

  loggerRequire(data.length == segmentStartTs.length,
    "Given data and segmentStartTs must have the same length. Actual lengths were {}, {}.",
    data.length.toString, segmentStartTs.length.toString)

  override lazy val segmentLengths = Array( data.length )
  override def readPointImpl(frame: Int, segment: Int): Int = data(segment)(frame)

}

class NNDataChannelPreloadedSingleSegment
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
  extends NNDataChannelPreloaded(Array(data), xBits, absGain, absUnit, absOffset, scaleMax, scaleMin, channelName, Array(segmentStartTS), sampleRate)
