package nounou

//import breeze.linalg.DenseVector

import _root_.nounou.data.ranges.{SampleRangeValid, SampleRangeReal, SampleRangeTS, SampleRange}
import breeze.linalg.DenseVector
import nounou.data.{/*XSpike,*/ XTrodeN}


/**A static class which encapsulates convenience functions for using nounou, with
 * an emphasis on use from Mathematica/MatLab/Java
 * @author ktakagaki
 * @date 2/17/14.
 */
object NN {

  override final def toString(): String = "Welcome to nounou, a Scala/Java adapter for neurophysiological data."

  // <editor-fold defaultstate="collapsed" desc=" options ">

  def OptNull() = nounou.OptNull

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" frame ranges ">

  final def SampleRange(start: Int, last: Int, step: Int, segment: Int) = new SampleRange(start, last, step, segment)
  final def SampleRangeReal(start: Int, last: Int, step: Int, segment: Int) = new SampleRangeReal(start, last, step, segment)
  final def SampleRangeValid(start: Int, last: Int, step: Int, segment: Int) = new SampleRangeValid(start, last, step, segment)
//The following are deprecated due to the ambiguity between step and segment variables
//  final def SampleRange(start: Int, last: Int, step: Int)               = new SampleRange(start, last, step, -1)
//  final def SampleRange(start: Int, last: Int, segment: Int)            = new SampleRange(start, last, -1,   segment)
//  final def SampleRange(start: Int, last: Int)                          = new SampleRange(start,    last,     -1,       -1)
  final def SampleRange( range: (Int, Int) )                            = new SampleRange(range._1, range._2, -1,       -1)
  final def SampleRange( range: (Int, Int), segment: Int)               = new SampleRange(range._1, range._2, -1,       segment)
  final def SampleRange( range: (Int, Int, Int) )                       = new SampleRange(range._1, range._2, range._3, -1)
  final def SampleRange( range: (Int, Int, Int), segment: Int )         = new SampleRange(range._1, range._2, range._3, segment)
  final def SampleRange( range: Array[Int], segment: Int ): SampleRange = {
    val len = range.length
    loggerRequire( len == 2 || len == 3, s"The first variable of SampleRange must be a 2- or 3-element array, not $range")
    if( len == 3 ) SampleRange(range(0), range(1), range(2), segment)
    else SampleRange(range(0), range(1), -1, segment)
  }
  final def SampleRange( range: Array[Int]): SampleRange = SampleRange( range, -1 )

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" RangeTs ">

  final def SampleRangeTs(startTs: Long, endTS: Long, stepTS: Long): SampleRangeTS =
    new SampleRangeTS(startTs, endTS, stepTS)
  final def FrameRangeTs(startTs: Long, endTS: Long): SampleRangeTS =
    new SampleRangeTS(startTs, endTS, -1L)

//  final def RangeTs(stamps: Array[Long], preTS: Long, postTS: Long): Array[ranges.RangeTs] =
//    stamps.map( (s: Long) => ranges.RangeTs(s-preTS, s+postTS) )

  // </editor-fold>

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" toArray methods ">

  def toArray(denseVector: DenseVector[Long]) = breeze.util.JavaArrayOps.dvToArray(denseVector)
//  def toArray(xSpike: XSpike) = XSpike.toArray( xSpike )
//  def toArray(xSpikes: Array[XSpike]) = XSpike.toArray( xSpikes )

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
