package nounou

import scala.reflect.ClassTag
import breeze.math.Complex
import nounou.data.traits.XFrames
import breeze.numerics.round

/**
 * @author ktakagaki
 * @date 2/17/14.
 */
  object NN {

    final val rangeFrAll: RangeFr = RangeFr.All()
    final def rangeFrAll(step: Int) = RangeFr.All(step)
    final def rangeFrAll(step: Double) = RangeFr.All( round(step).toInt )

    final def rangeFr(start: Int, endMarker: Int, step: Int): RangeFr = {
      return new RangeFr(start, endMarker, step, false)
    }

    final def rangeFr(start: Int, endMarker: Int): RangeFr = {
      return new RangeFr(start, endMarker, 1, false)
    }

    final def rangeFr(frame: Int): RangeFr = {
      return new RangeFr(frame, frame, 1, false)
    }
    final def rangeFr(start: Double): RangeFr = rangeFr(round(start).toInt)
    final def rangeFr(start: Double, endMarker: Double): RangeFr = rangeFr(round(start).toInt, round(endMarker).toInt)
    final def rangeFr(start: Double, endMarker: Double, step: Double): RangeFr = rangeFr(round(start).toInt, round(endMarker).toInt, round(step).toInt)


    @deprecated("Don't use this anymore, initialize nounous.DataReader()", "v 1")
    final def newReader: DataReader = {
      return new DataReader
    }

    final def msRange(startMs: Double, endMs: Double, stepMs: Double, sampleRate:Double): RangeFr =
        RangeFr.msRange(startMs, endMs, stepMs, sampleRate)



}


