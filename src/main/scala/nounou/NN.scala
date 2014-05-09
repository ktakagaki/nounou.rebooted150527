package nounou

import breeze.numerics.round
import nounou.data.{XTrodesPreloaded, XSpikes, XTrodes}

/**
 * @author ktakagaki
 * @date 2/17/14.
 */
object NN {

  // <editor-fold defaultstate="collapsed" desc=" RangeFrAll/RangeFr ">

  final def RangeFrAll(): ranges.RangeFr = ranges.RangeFrAll()
  final def RangeFrAll(step: Int): ranges.RangeFr = ranges.RangeFrAll(step)
  final def RangeFrAll(step: Double): ranges.RangeFr = ranges.RangeFrAll( round(step).toInt )
  final def RangeFrAll(step: Int, segment: Int): ranges.RangeFr = ranges.RangeFrAll(step, segment)

  final def RangeFr(start: Int, endMarker: Int, step: Int, segment: Int): ranges.RangeFr = ranges.RangeFr(start, endMarker, step, segment)
  final def RangeFr(start: Int, endMarker: Int, step: Int): ranges.RangeFr = ranges.RangeFr(start, endMarker, step)
  final def RangeFr(start: Int, endMarker: Int): ranges.RangeFr = ranges.RangeFr(start, endMarker)

  final def RangeFr(frame: Int): ranges.RangeFr = ranges.RangeFr(frame, frame, 1)
//
//  final def RangeFr(start: Double, endMarker: Double): ranges.RangeFr = ranges.RangeFr(round(start).toInt, round(endMarker).toInt)
//  final def RangeFr(start: Double, endMarker: Double, step: Double): ranges.RangeFr = ranges.RangeFr(round(start).toInt, round(endMarker).toInt, round(step).toInt)

  final def RangeMs(start: Double, endMarker: Double, step: Double, segment: Int) = ranges.RangeMs(start, endMarker, step, segment)

  // </editor-fold>


  final def RangeTs(startTs: Long, endTs: Long, step: Long, segment: Int): ranges.RangeTs =
    ranges.RangeTs(startTs, endTs, step, segment)

  final def RangeTsEvent(eventTS: Long, preFrames: Int, postFrames: Int, step: Int): ranges.RangeTsEvent =
    ranges.RangeTsEvent(eventTS,  preFrames, postFrames, step)
  final def RangeTsEvent(eventTSS: Array[Long], preFrames: Int, postFrames: Int, step: Int): Array[ranges.RangeTsEvent] =
    ranges.RangeTsEvent(eventTSS,  preFrames, postFrames, step)

  final def XTrodes( trodeGroup: Array[Array[Int]] ): XTrodes = data.XTrodes( trodeGroup )

  final def XSpikes(waveformLength: Int, xTrodes: XTrodes ) = new XSpikes(waveformLength, xTrodes)


  @deprecated("Don't use this anymore, initialize nounous.DataReader()", "v 1")
  final def newReader: DataReader = {
    return new DataReader
  }



}


