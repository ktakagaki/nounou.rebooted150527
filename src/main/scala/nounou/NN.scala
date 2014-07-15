package nounou

import breeze.numerics.round
import nounou.data.{XSpike, XData, Frame, XTrodeN, ranges}

/**
 * @author ktakagaki
 * @date 2/17/14.
 */
object NN {

  // <editor-fold defaultstate="collapsed" desc=" RangeFrAll/RangeFr ">

  final def RangeFrAll(): ranges.RangeFrAll = RangeFrAll( 0 )
  @deprecated
  final def RangeFrAll(step: Int): ranges.RangeFrAll = new ranges.RangeFrAll( 0, OptStep(step) )
  @deprecated
  final def RangeFrAll(step: Double): ranges.RangeFrAll = RangeFrAll( round(step).toInt )
  @deprecated
  final def RangeFrAll(step: Int, segment: Int): ranges.RangeFrAll = new ranges.RangeFrAll(segment, OptStep(step))

  @deprecated
  final def RangeFr(start: Int, endMarker: Int, step: Int, segment: Int): ranges.RangeFr = ranges.RangeFr(start, endMarker, step, segment)
  @deprecated
  final def RangeFr(start: Int, endMarker: Int, step: Int): ranges.RangeFr = ranges.RangeFr(start, endMarker, step)
  @deprecated
  final def RangeFr(start: Int, endMarker: Int): ranges.RangeFr = ranges.RangeFr(start, endMarker)
  @deprecated
  final def RangeFr(frame: Int): ranges.RangeFr = ranges.RangeFr(frame, frame, 1)

  final def RangeMs(start: Double, endMarker: Double, step: Double, segment: Int) = ranges.RangeMs(start, endMarker, step, segment)

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" toArray methods ">

  def toArray(xFrame: Frame) = xFrame.toArray()
  def toArray(xFrames: Array[Frame]) = xFrames.map( _.toArray() )
  def toArray(xSpike: XSpike) = XSpike.toArray( xSpike )
  def toArray(xSpikes: Array[XSpike]) = XSpike.toArray( xSpikes )

  // </editor-fold>

  def readSpikes(xData: XData, channels: Array[Int], xFrames: Array[Frame], length: Int, trigger: Int) =
    data.XSpike.readSpikes(xData, channels, xFrames, length, trigger)
  def readSpike(xData: XData, channels: Array[Int], xFrame: Frame, length: Int, trigger: Int) =
    data.XSpike.readSpike(xData, channels, xFrame, length, trigger)

  final def RangeTs(startTs: Long, endTs: Long, step: Long, segment: Int): ranges.RangeTs =
    ranges.RangeTs(startTs, endTs, step, segment)

  final def RangeTsEvent(eventTS: Long, preFrames: Int, postFrames: Int, step: Int): ranges.RangeTsEvent =
    ranges.RangeTsEvent(eventTS,  preFrames, postFrames, step)
  final def RangeTsEvent(eventTSS: Array[Long], preFrames: Int, postFrames: Int, step: Int): Array[ranges.RangeTsEvent] =
    ranges.RangeTsEvent(eventTSS,  preFrames, postFrames, step)

  //final def XTrodes( trodeGroup: Array[Array[Int]] ): XTrodes = data.XTrodes( trodeGroup )
  final def XTrodeN( trodeGroup: Array[Int] ): XTrodeN = new data.XTrodeN( trodeGroup.toVector )

  //final def XSpikes(waveformLength: Int, xTrodes: XTrodes ) = new XSpikes(waveformLength, xTrodes)


  @deprecated("Don't use this anymore, initialize nounous.DataReader()", "v 1")
  final def newReader: DataReader = {
    return new DataReader
  }



}


