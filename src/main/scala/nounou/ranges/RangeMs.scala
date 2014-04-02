package nounou

import nounou.data.traits.XFrames
import nounou.RangeFrSpecifier
import nounou.ranges.{RangeFrSpecifier, RangeFr}

/**For specifying data extraction range in Ms.
 * @author ktakagaki
 * @date 2/17/14.
 */
object RangeMs {
  def apply(start: Double, end: Double, step: Double, segment: Int) = new RangeMs(start, end, step, 0)
  def apply(start: Double, end: Double, step: Double) = new RangeMs(start, end, step)
  def apply(start: Double, end: Double) = new RangeMs(start, end)

 // class All(step: Double) extends RangeMs(0, 0, step, true)
}

object RangeMsAnchor{
  def apply(anchorMs: Double, preMs: Double, postMs: Double, step: Double, segment: Int) = new RangeMs(anchorMs-preMs, anchorMs+postMs, step, segment)
  def apply(anchorMs: Double, preMs: Double, postMs: Double, step: Double) = new RangeMs(anchorMs-preMs, anchorMs+postMs, step)
}

class RangeMs(val start: Double, val end: Double, val step: Double, val segment: Int) extends RangeFrSpecifier {
  def this(start: Double, end: Double, step: Double) = this(start, end, step, 0)
  def this(start: Double, end: Double) = this(start, end, 0d, 0)

  def getFrameRange(x: XFrames): RangeFr = {
    val stepReal = if(step == 0d) 1
      else (step * x.sampleInterval * 1000d).toInt

  loggerRequire(stepReal>0, "This amounts to a negative timestep! (stepMs=" + step + " ms)")

//    if(isAll){
//      RangeFrAll( stepReal )
//    } else {
      val startReal = x.msToFrame(start)
      val endReal = x.msToFrame(end)

      new RangeFr(startReal, endReal, stepReal, segment)
//    }
  }

}
