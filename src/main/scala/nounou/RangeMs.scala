package nounou

import nounou.data.traits.XFrames

/**For specifying data extraction range in Ms
 * @author ktakagaki
 * @date 2/17/14.
 */
  object RangeMs {

    class All(step: Double) extends RangeMs(0, 0, step, true)

  }

  class RangeMs(val start: Double, val end: Double, val step: Double, val isAll: Boolean = false) extends RangeFrSpecifier {

    def getFrameRange(x: XFrames): RangeFr = {

      val stepReal = (step * x.sampleInterval * 1000d).toInt
      require(stepReal>0, "This amounts to a negative or zero timestep! (stepMs=" + step + " ms)")

      if(isAll){
        RangeFr.All( stepReal )
      } else {
        val startReal = x.msToFrame(start)
        val endReal = x.msToFrame(end)

        new RangeFr(startReal, endReal, stepReal)
      }
    }

  }
