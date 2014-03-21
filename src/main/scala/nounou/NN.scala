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

  final val RangeFrAll: RangeFr = nounou.RangeFrAll()
  final def RangeFrAll(step: Int) = nounou.RangeFrAll(step)
  final def RangeFrAll(step: Double) = nounou.RangeFrAll( round(step).toInt )
  final def RangeFrAll(step: Int, segment: Int) = nounou.RangeFrAll(step, segment)

  final def RangeFr(start: Int, endMarker: Int, step: Int, segment: Int): RangeFr = nounou.RangeFr(start, endMarker, step, segment)
  final def RangeFr(start: Int, endMarker: Int, step: Int): RangeFr = nounou.RangeFr(start, endMarker, step)
  final def RangeFr(start: Int, endMarker: Int): RangeFr = nounou.RangeFr(start, endMarker)

  final def RangeFr(frame: Int): RangeFr = nounou.RangeFr(frame, frame, 1)

  final def RangeFr(start: Double, endMarker: Double): RangeFr = nounou.RangeFr(round(start).toInt, round(endMarker).toInt)
  final def RangeFr(start: Double, endMarker: Double, step: Double): RangeFr = nounou.RangeFr(round(start).toInt, round(endMarker).toInt, round(step).toInt)


  final def RangeTS(startTS: Long, endTS: Long, step: Long, segment: Int, isAll: Boolean) = new RangeTS(startTS, endTS, step, segment, isAll)


  @deprecated("Don't use this anymore, initialize nounous.DataReader()", "v 1")
  final def newReader: DataReader = {
    return new DataReader
  }

//  final def msRange(startMs: Double, endMs: Double, stepMs: Double, sampleRate:Double): RangeFr =
//      RangeFr.msRange(startMs, endMs, stepMs, sampleRate)



}


