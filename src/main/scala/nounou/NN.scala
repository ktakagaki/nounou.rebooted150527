package nounou

import scala.reflect.ClassTag
import breeze.math.Complex
import nounou.data.traits.XFrames
import breeze.numerics.round
import nounou.ranges.{RangeFrAll, RangeFr}
import nounou.ranges

/**
 * @author ktakagaki
 * @date 2/17/14.
 */
object NN {

  final val RangeFrAll: RangeFr = RangeFrAll()
  final def RangeFrAll(step: Int) = RangeFrAll(step)
  final def RangeFrAll(step: Double) = ranges.RangeFrAll( round(step).toInt )
  final def RangeFrAll(step: Int, segment: Int) = ranges.RangeFrAll(step, segment)

  final def RangeFr(start: Int, endMarker: Int, step: Int, segment: Int): RangeFr = RangeFr(start, endMarker, step, segment)
  final def RangeFr(start: Int, endMarker: Int, step: Int): RangeFr = ranges.RangeFr(start, endMarker, step)
  final def RangeFr(start: Int, endMarker: Int): RangeFr = ranges.RangeFr(start, endMarker)

  final def RangeFr(frame: Int): RangeFr = ranges.RangeFr(frame, frame, 1)

  final def RangeFr(start: Double, endMarker: Double): RangeFr = ranges.RangeFr(round(start).toInt, round(endMarker).toInt)
  final def RangeFr(start: Double, endMarker: Double, step: Double): RangeFr = ranges.RangeFr(round(start).toInt, round(endMarker).toInt, round(step).toInt)


  final def RangeTS(startTS: Long, endTS: Long, step: Long, segment: Int, isAll: Boolean) = new RangeTS(startTS, endTS, step, segment, isAll)


  @deprecated("Don't use this anymore, initialize nounous.DataReader()", "v 1")
  final def newReader: DataReader = {
    return new DataReader
  }

//  final def msRange(startMs: Double, endMs: Double, stepMs: Double, sampleRate:Double): RangeFr =
//      RangeFr.msRange(startMs, endMs, stepMs, sampleRate)



}


