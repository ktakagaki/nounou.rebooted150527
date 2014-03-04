package nounou

import scala.reflect.ClassTag
import breeze.math.Complex
import nounou.data.traits.XFrames

/**
 * @author ktakagaki
 * @date 2/17/14.
 */
  object NN {

    final val frameRangeAll: FrameRange = FrameRange.all()
    final def frameRangeAll(step: Int) = FrameRange.all(step)
    final def frameRange(start: Int, endMarker: Int, step: Int): FrameRange = {
      return new FrameRange(start, endMarker, step, false)
    }

    final def frameRange(start: Int, endMarker: Int): FrameRange = {
      return new FrameRange(start, endMarker, 1, false)
    }

    final def frameRange(frame: Int): FrameRange = {
      return new FrameRange(frame, frame, 1, false)
    }

//    final def scalaRange(start: Int, endMarker: Int, stepMs: Int): Range = {
//      return new Range.Inclusive(start, endMarker, stepMs)
//    }
//
//    final def scalaRange(start: Int, endMarker: Int): Range = {
//      return new Range.Inclusive(start, endMarker, 1)
//    }

    @deprecated("Don't use this anymore, initialize nounous.DataReader()", "v 1")
    final def newReader: DataReader = {
      return new DataReader
    }

    final def vectorToArray[T: ClassTag](vector: Vector[T]): Array[T] = vector.toArray
//    final def array(vector: Vector[Long]): Array[Long] = vector.toArray
//    final def ad(vector: Vector[Double]): Array[Double] = vector.toArray
//    final def ac(vector: Vector[Complex]): Array[Complex] = vector.toArray

    final def msRange(startMs: Double, endMs: Double, stepMs: Double, sampleRate:Double): FrameRange =
        FrameRange.msRange(startMs, endMs, stepMs, sampleRate)



}


