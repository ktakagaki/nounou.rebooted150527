package nounou

import scala.reflect.ClassTag
import breeze.math.Complex
import nounou.data.traits.XFrames

/**
 * @author ktakagaki
 * @date 2/17/14.
 */
  object NN {

    final val frameRangeAll: FrameRange = FrameRange.All()
    final def frameRangeAll(step: Int) = FrameRange.All(step)
    final def frameRange(start: Int, endMarker: Int, step: Int): FrameRange = {
      return new FrameRange(start, endMarker, step, false)
    }

    final def frameRange(start: Int, endMarker: Int): FrameRange = {
      return new FrameRange(start, endMarker, 1, false)
    }

    final def frameRange(frame: Int): FrameRange = {
      return new FrameRange(frame, frame, 1, false)
    }

    final def scalaRange(start: Int, endMarker: Int, step: Int): Range = {
      return new Range.Inclusive(start, endMarker, step)
    }

    final def scalaRange(start: Int, endMarker: Int): Range = {
      return new Range.Inclusive(start, endMarker, 1)
    }

    final def newReader: DataReader = {
      return new DataReader
    }

    final def vectorToArray[T: ClassTag](vector: Vector[T]): Array[T] = vector.toArray
//    final def array(vector: Vector[Long]): Array[Long] = vector.toArray
//    final def ad(vector: Vector[Double]): Array[Double] = vector.toArray
//    final def ac(vector: Vector[Complex]): Array[Complex] = vector.toArray

    final def msRange(msStart: Double, msEnd: Double, step: Double, sampleRate:Double): FrameRange = {
      val startFr = (msStart/1000d * sampleRate).toInt
      val endFr = (msEnd/1000d * sampleRate).toInt
      val stepReal = (step/1000d * sampleRate).toInt
      new FrameRange(startFr, endFr, stepReal)
    }



}


