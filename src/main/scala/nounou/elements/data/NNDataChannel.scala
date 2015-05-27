package nounou.elements.data

import nounou._
import nounou.elements.NNElement
import java.io.DataInput
import nounou.elements.traits.{NNDataTimingElement, NNDataScaleElement, NNDataTiming, NNDataScale}
import breeze.linalg.{DenseVector => DV}
import nounou.elements.ranges.{SampleRangeSpecifier, SampleRangeValid, SampleRangeAll}

/**
 * Created by Kenta on 12/14/13.
 */
abstract class NNDataChannel extends NNElement
                              with NNDataTimingElement with NNDataScaleElement {

  /**MUST OVERRIDE: name of the given channel.*/
  val channelName: String

  //<editor-fold desc="reading a point">
  /** Read a single point from the data, in internal integer scaling, after checking values.
    * Implement via readPointImpl.
    */
  final def readPoint(frame: Int, segment: Int): Int = {
    //require(isValidFr(frame, segment), "Invalid frame/segment: " + (frame, segment).toString)
    if( timing().isValidFrsg(frame, segment) ) readPointImpl(frame, segment) else 0
  }
  //</editor-fold>

  /** MUST OVERRIDE: Read a single point from the data, in internal integer scaling.
    */
  def readPointImpl(frame: Int, segment: Int): Int

  //<editor-fold desc="reading a trace">
  /** Read a single trace from the data, in internal integer scaling.
    */
  final def readTrace(segment: Int): Array[Int] = {
    val range = NN.SampleRangeAll().getSampleRangeValid( timing() )
    readTraceDVImpl(range).toArray
  }
  final def readTrace(range: SampleRangeSpecifier): Array[Int] = readTraceDV(range).toArray
  /** Read a single trace (within the span) from the data, in internal integer scaling.
    */
  final def readTraceDV(range: SampleRangeSpecifier): DV[Int] = {
    val (preLength, seg, postLength) = range.getSampleRangeValidPrePost( timing() )
    DV.vertcat( DV.zeros[Int]( preLength ), readTraceDVImpl(seg), DV.zeros[Int]( postLength ) )
  }
  //</editor-fold>

  /** CAN OVERRIDE: Read a single data trace from the data, in internal integer scaling.
    * Should return a defensive clone.
    */
  def readTraceDVImpl(range: SampleRangeValid): DV[Int] = {
    val res = DV.zeros[Int](range.length)
    nounou.util.forJava(range.start, range.last + 1, range.step, (c: Int) => (res(c) = readPointImpl(c, range.segment)))
    res
  }

  override def isCompatible(that: NNElement): Boolean = {
    that match {
      case x: NNDataChannel => {
        timing.isCompatible(x.timing) && scale.isCompatible(x.scale)
      }
      case _ => false
    }
  }

  override def toString() = {
    s"NNDataChannel(${timing().segmentCount} segments, fs=${timing().sampleRate}, $gitHeadShort)"
  }
  override def toStringFull(): String = toString()
//  {
//    var tempout = toString().dropRight(1) + " )/n" //+
//      //"============================================================/n" +
//      //"seg#/tsegmentLength/tsegmentStartTs/n"
//    tempout.dropRight(1)
//  }


}




