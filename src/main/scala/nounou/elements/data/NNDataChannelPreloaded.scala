package nounou.elements.data

import breeze.linalg.{DenseVector => DV}
import nounou.elements.traits.{NNDataScale, NNDataTiming}

/**
 * Created by ktakagaki on 15/05/21.
 */
class NNDataChannelPreloaded(val data: DV[Int], timingEntry: NNDataTiming, scaleEntry: NNDataScale,
                             override val channelName: String
                              )  extends NNDataChannel {

  setTiming(timingEntry)
  setScale(scaleEntry)

  //ToDo2 test the DV length against segment lengths
  //override lazy val segmentLengths = Array( data.length )
  override def readPointImpl(frame: Int, segment: Int): Int = data( timing.segmentStartFrame(segment) + frame)

}

//class NNDataChannelPreloadedSingleSegment
//                   (data: DV[Int],
//                    xBits: Int,
//                    absGain: Double,
//                    absUnit: String,
//                    absOffset: Double,
//                    scaleMax: Int,
//                    scaleMin: Int,
//                    channelName: String,
//                    segmentStartTS: Long,
//                    sampleRate: Double
//                    )
//  extends NNDataChannelPreloaded(Array(data), xBits, absGain, absUnit, absOffset, scaleMax, scaleMin, channelName, Array(segmentStartTS), sampleRate)
//

//
//  override def :::(that: NNElement): NNDataChannelArray = {
//    that match {
//      case t: NNDataChannelArray => {
//        if(this.isCompatible(t(0))){
//          new NNDataChannelArray( t.array :+ this )
//        } else {
//          throw new IllegalArgumentException("types are not compatible, and cannot be concatenated.")
//        }
//      }
//      case t: NNDataChannelNull => new NNDataChannelArray( this )
//      case t: NNDataChannel => {
//        if(this.isCompatible(t)){
//          new NNDataChannelArray( Vector(this, t) )
//        } else {
//          throw new IllegalArgumentException("the XDataChannelArray type and XDataChannel type are not compatible, and cannot be concatenated.")
//        }
//      }
//      case _ => throw new IllegalArgumentException("the two X types are not compatible, and cannot be concatenated.")
//    }
//  }
//

//class NNDataChannelNull extends NNDataChannel {
//  val channelName: String = "XDataChannelNull()"
//
//  override def readPointImpl(frame: Int, segment: Int): Int = 0
//
//  override val absGain: Double = 1d
//  override val absOffset: Double = 0d
//  override val absUnit: String = "XDataChannelNull"
//  override val scaleMax: Int = 0
//  override val scaleMin: Int = 0
//  override def segmentLengthImpl(segment: Int): Int = 0
//  override val segmentStartTs: Array[Long] = Array(0L)
//  override val sampleRate: Double = 1d
//
//  /** Number of segments in data.
//    */
//  override val segmentCount: Int = 0
//
//  /** OVERRIDE: End timestamp for each segment. Implement by overriding _endTimestamp
//    */
//  override val segmentEndTs: Array[Long] = Array[Long]()
//}
