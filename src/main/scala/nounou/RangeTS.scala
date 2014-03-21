package nounou

import nounou.data.traits.XFrames

/**
 * @author ktakagaki
 * @date 3/19/14.
 */
object RangeTS {

  class All(step: Double) extends RangeMs(0, 0, step, true)

}

class RangeTS(val startTS: Long, val endTS: Long, val step: Long, val segment: Int, val isAll: Boolean) extends RangeFrSpecifier {

  def this(startTS: Long, endTS: Long, step: Long, segment: Int) = this(startTS, endTS, step, segment, false)
  def this(segment: Int, isAll: Boolean) = this(0L, 0L, 0L, segment, true)
  def this(isAll: Boolean) = this(0, true)
  def this(startTS: Long, endTS: Long, step: Long) = this(startTS, endTS, step, 0, false)

  def getFrameRange(x: XFrames): RangeFr = {

    val stepReal = (step * x.sampleInterval/* * 1000d*/).toInt
    loggerRequire(stepReal>0, "This amounts to a negative or zero timestep! (stepMs=" + step + " ms)")

    if(isAll){
      RangeFrAll( stepReal )
    } else {
      val startReal = x.tsToFrameSegment(startTS) //ToDo 2: expand to send segment info too in RangeFr
      val endReal = x.tsToFrameSegment(endTS)
      loggerRequire(startReal._2 == endReal._2, "The two specified timestamps belong to different recording segments {}  and {}.", startReal._2.toString, endReal._2.toString)

      new RangeFr(startReal._1, endReal._1, stepReal)
    }
  }

}

class RangeTSEvent(val eventTS: Long, val preFrames: Int, val postFrames: Int, val steps: Int, val segment: Int) extends RangeFrSpecifier {

  def this(eventTS: Int, preFrames: Int, postFrames: Int, step: Int) = this(eventTS, preFrames, postFrames, step, 0)

  def getFrameRange(x: XFrames): RangeFr = {
    val eventFrame = x.tsToFrameSegment(eventTS, true) //ToDo 2: eliminate negative if OOB for FrameSegment, alsways true
    new RangeFr(eventFrame._1 - preFrames, eventFrame._1 + postFrames, steps)
  }

}

