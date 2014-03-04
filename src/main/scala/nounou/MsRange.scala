package nounou

import nounou.data.traits.XFrames

/**
 * @author ktakagaki
 * @date 2/17/14.
 */
  object MsRange {

    class All(step: Double) extends MsRange(0, 0, step, true)

  }

  class MsRange(val start: Double, val end: Double, val step: Double, val isAll: Boolean = false) /*extends Range(start, last, stepMs)*/{

    def getFrameRange(x: XFrames): FrameRange = {

      val stepReal = (step * x.sampleInterval * 1000d).toInt
      require(stepReal>0, "This amounts to a negative or zero timestep! (stepMs=" + step + " ms)")

      if(isAll){
        FrameRange.all( stepReal )
      } else {
        val startReal = x.msToFrame(start)
        val endReal = x.msToFrame(end)

        new FrameRange(startReal, endReal, stepReal)
      }
    }

  }
