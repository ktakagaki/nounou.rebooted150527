package nounou

import breeze.linalg.DenseVector
import nounou.data.{XSpike, XTrodeN, ranges}

/**A static class which encapsulates convenience functions from nounou for
  * use in Mathematica/MatLab/Java
 * @author ktakagaki
 * @date 2/17/14.
 */
object NN {

  override final def toString(): String = "Welcome to nounou, a Scala/Java adapter for neurophysiological data."

  // <editor-fold defaultstate="collapsed" desc=" options ">

  def OptNull() = nounou.OptNull
  def OptSegment(segment: Int) = nounou.OptSegment(segment)
  def OptSegment() = nounou.OptSegmentAutomatic

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" frame ranges ">

  // <editor-fold defaultstate="collapsed" desc=" RangeFrAll ">

  final def RangeFrAll(step: Int, optSegment: OptSegment): ranges.RangeFrAll = ranges.RangeFrAll( step, optSegment )
// deprecated due to potential confusion with this(segment: Int)
//  final def RangeFrAll(step: Int): ranges.RangeFrAll = ranges.RangeFrAll( step, -1 )
  final def RangeFrAll(optSegment: OptSegment): ranges.RangeFrAll = ranges.RangeFrAll(1, optSegment)
  final def RangeFrAll(): ranges.RangeFrAll = ranges.RangeFrAll()

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" RangeFr ">

  final def RangeFr(start: Int, last: Int, step: Int, optSegment: OptSegment) = ranges.RangeFr(start, last, step, optSegment)
  final def RangeFr(start: Int, last: Int, step: Int)                         = ranges.RangeFr(start, last, step)
  final def RangeFr(start: Int, last: Int, optSegment: OptSegment)            = ranges.RangeFr(start, last, optSegment)
  final def RangeFr(start: Int, last: Int)                                    = ranges.RangeFr(start, last)
  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" RangeTs ">

  final def RangeTs(startTs: Long, endTS: Long, stepTS: Long): ranges.RangeTS =
    ranges.RangeTS(startTs, endTS, stepTS)

  final def RangeTs(startTS: Long, endTS: Long): ranges.RangeTS =
    ranges.RangeTS(startTS, endTS)

//  final def RangeTs(stamps: Array[Long], preTS: Long, postTS: Long): Array[ranges.RangeTs] =
//    stamps.map( (s: Long) => ranges.RangeTs(s-preTS, s+postTS) )

  // </editor-fold>

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" toArray methods ">

  def toArray(denseVector: DenseVector[Long]) = breeze.util.JavaArrayOps.dvToArray(denseVector)
  def toArray(xSpike: XSpike) = XSpike.toArray( xSpike )
  def toArray(xSpikes: Array[XSpike]) = XSpike.toArray( xSpikes )

  // </editor-fold>

//  def readSpikes(xData: XData, channels: Array[Int], xFrames: Array[Frame], length: Int, trigger: Int) =
//    data.XSpike.readSpikes(xData, channels, xFrames, length, trigger)
//  def readSpike(xData: XData, channels: Array[Int], xFrame: Frame, length: Int, trigger: Int) =
//    data.XSpike.readSpike(xData, channels, xFrame, length, trigger)


  //final def XTrodes( trodeGroup: Array[Array[Int]] ): XTrodes = data.XTrodes( trodeGroup )
  final def XTrodeN( trodeGroup: Array[Int] ): XTrodeN = new data.XTrodeN( trodeGroup.toVector )


}



//final def XSpikes(waveformLength: Int, xTrodes: XTrodes ) = new XSpikes(waveformLength, xTrodes)
//  final def newNNData: NNData = new NNData


//  // <editor-fold defaultstate="collapsed" desc=" RangeTsEvent ">
//
//  def RangeTsEvent(eventTs: Long, preFrames: Int, postFrames: Int) =
//    ranges.RangeTsEvent(eventTs, preFrames, postFrames)
//
//  def RangeTsEvent(eventTs: Array[Long], preFrames: Int, postFrames: Int) =
//    ranges.RangeTsEvent(eventTs, preFrames, postFrames)
//
//  // </editor-fold>
// <editor-fold defaultstate="collapsed" desc=" RangeMs ">

////  final def RangeMs(startMs: Double, lastMs: Double, stepMs: Double, optSegment: OptSegment) =
////    ranges.RangeMs(startMs, lastMs, stepMs, optSegment)
//  final def RangeMs(startMs: Double, lastMs: Double, stepMs: Double) =
//    ranges.RangeMs(startMs, lastMs, stepMs)
////  final def RangeMs(startMs: Double, lastMs: Double, optSegment: OptSegment) =
////    ranges.RangeMs(startMs, lastMs, optSegment)
//  final def RangeMs(startMs: Double, lastMs: Double)=
//    ranges.RangeMs(startMs, lastMs)

// </editor-fold>
// <editor-fold defaultstate="collapsed" desc=" RangeMsEvent ">

//  final def RangeMsEvent(eventMs: Double, preMs: Double, postMs: Double, stepMs: Double, optSegment: OptSegment) =
//    ranges.RangeMsEvent(eventMs, preMs, postMs, stepMs, optSegment)
//  final def RangeMsEvent(eventMs: Double, preMs: Double, postMs: Double, optSegment: OptSegment) =
//    ranges.RangeMsEvent(eventMs, preMs, postMs, optSegment)
//  final def RangeMsEvent(eventMs: Double, preMs: Double, postMs: Double, stepMs: Double) =
//    ranges.RangeMsEvent(eventMs, preMs, postMs, stepMs)
//  final def RangeMsEvent(eventMs: Double, preMs: Double, postMs: Double) =
//    ranges.RangeMsEvent(eventMs, preMs, postMs)
////  final def RangeMsEvent(eventMs: Array[Double], preMs: Double, postMs: Double, optSegment: OptSegment) =
////    ranges.RangeMsEvent(eventMs, preMs, postMs, optSegment)
//  final def RangeMsEvent(eventMs: Array[Double], preMs: Double, postMs: Double) =
//    ranges.RangeMsEvent(eventMs, preMs, postMs)
////  final def RangeMsEvent(eventMs: Array[Double], preMs: Double, postMs: Double, stepMs: Double, optSegment: OptSegment) =
////    ranges.RangeMsEvent(eventMs, preMs, postMs, stepMs, optSegment)
//  final def RangeMsEvent(eventMs: Array[Double], preMs: Double, postMs: Double, stepMs: Double) =
//    ranges.RangeMsEvent(eventMs, preMs, postMs, stepMs)

// </editor-fold>
