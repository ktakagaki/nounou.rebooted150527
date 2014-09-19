package nounou

import nounou.data.{XSpike, XTrodeN, ranges}

/**A static class which encapsulates convenience functions from nounou for
  * use in Mathematica/MatLab/Java
 * @author ktakagaki
 * @date 2/17/14.
 */
object NN {

  final def hello(): String = "Welcome to nounou, a Scala/Java adapter for neurophysiological data."

  // <editor-fold defaultstate="collapsed" desc=" options ">

  def OptNull() = nounou.OptNull
//  def OptSegment(segment: Int) = nounou.OptSegment(segment)
//  def OptSegmentNone() = nounou.OptSegmentNone

  // </editor-fold>


  // <editor-fold defaultstate="collapsed" desc=" RangeFrAll ">

//  final def RangeFrAll(step: Int, optSegment: OptSegment): ranges.RangeFrAll = ranges.RangeFrAll( step, optSegment )
  final def RangeFrAll(step: Int): ranges.RangeFrAll = ranges.RangeFrAll( step )
//  final def RangeFrAll(optSegment: OptSegment): ranges.RangeFrAll = ranges.RangeFrAll( optSegment )
  final def RangeFrAll(): ranges.RangeFrAll = ranges.RangeFrAll()


//  @deprecated
//  final def RangeFrAll(step: Int): ranges.RangeFrAll = new ranges.RangeFrAll( 0, OptStep(step) )
//  @deprecated
//  final def RangeFrAll(step: Double): ranges.RangeFrAll = RangeFrAll( round(step).toInt )
//  @deprecated
//  final def RangeFrAll(step: Int, segment: Int): ranges.RangeFrAll = new ranges.RangeFrAll(segment, OptStep(step))

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" RangeFr ">

//  final def RangeFr(start: Int, last: Int, step: Int, optSegment: OptSegment) = ranges.RangeFr(start, last, step, optSegment)
  final def RangeFr(start: Int, last: Int, step: Int) = ranges.RangeFr(start, last, step)
//  final def RangeFr(start: Int, last: Int, optSegment: OptSegment) = ranges.RangeFr(start, last, optSegment)
  final def RangeFr(start: Int, last: Int) = ranges.RangeFr(start, last)
  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" RangeTs ">

  final def RangeTs(startTs: Long, endTs: Long, stepTs: Long): ranges.RangeTs =
    ranges.RangeTs(startTs, endTs, stepTs)
//  final def RangeTs(startTs: Long, endTs: Long, stepTs: Long, optSegment: OptSegment): ranges.RangeTs =
//    ranges.RangeTs(startTs, endTs, stepTs, optSegment)
//  final def RangeTs(startTs: Long, endTs: Long, optSegment: OptSegment): ranges.RangeTs =
//    ranges.RangeTs(startTs, endTs, optSegment)
  final def RangeTs(startTs: Long, endTs: Long): ranges.RangeTs =
    ranges.RangeTs(startTs, endTs)

  // </editor-fold>
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



  // <editor-fold defaultstate="collapsed" desc=" toArray methods ">

//  def toArray(xFrame: Frame) = xFrame.toArray()
//  def toArray(xFrames: Array[Frame]) = xFrames.map( _.toArray() )
  def toArray(xSpike: XSpike) = XSpike.toArray( xSpike )
  def toArray(xSpikes: Array[XSpike]) = XSpike.toArray( xSpikes )

  // </editor-fold>

//  def readSpikes(xData: XData, channels: Array[Int], xFrames: Array[Frame], length: Int, trigger: Int) =
//    data.XSpike.readSpikes(xData, channels, xFrames, length, trigger)
//  def readSpike(xData: XData, channels: Array[Int], xFrame: Frame, length: Int, trigger: Int) =
//    data.XSpike.readSpike(xData, channels, xFrame, length, trigger)


  //final def XTrodes( trodeGroup: Array[Array[Int]] ): XTrodes = data.XTrodes( trodeGroup )
  final def XTrodeN( trodeGroup: Array[Int] ): XTrodeN = new data.XTrodeN( trodeGroup.toVector )

  //final def XSpikes(waveformLength: Int, xTrodes: XTrodes ) = new XSpikes(waveformLength, xTrodes)


  final def newNNData: NNData = new NNData



}


