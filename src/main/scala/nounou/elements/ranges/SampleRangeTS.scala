package nounou.elements.ranges

import nounou._
import nounou.elements.traits.NNDataTiming

/**Encapsulates a TS(timestamp, Long)-based frame range, with appropriate values.
 * @author ktakagaki
 * //@date 3/19/14.
 */
class SampleRangeTS(val startTS: Long, val lastTS: Long, val stepTS: Long) extends SampleRangeSpecifier {

  loggerRequire( startTS <= lastTS, s"SampleRangeTS requires startTS <= lastTS. startTS=$startTS, lastTS=$lastTS")
  loggerRequire( stepTS >= 1 || stepTS == -1, s"step must be -1 (automatic) or positive. Invalid value: $stepTS")

  override def toString() = s"SampleRangeTS($startTS, $lastTS, $stepTS)"

  // <editor-fold defaultstate="collapsed" desc=" RangeFrSpecifier methods ">
    
  override def getRealSegment(xDataTiming: NNDataTiming): Int = {
    realSegmentBufferRefresh(xDataTiming)
    realSegmentBuffer
  }

  private var realStepFramesBuffer = -1
  override def getRealStep(xDataTiming: NNDataTiming): Int = {
    if(realStepFramesBuffer == -1) {
      realStepFramesBuffer =
        if (stepTS == -1L) 1
        else {
          val stepReal = (stepTS.toDouble * xDataTiming.sampleRate / 1000000d).toInt
          loggerRequire(stepReal > 0, "This amounts to a negative time step! (stepTs=" + stepTS + " micro s => " + stepReal + " frames)")
          stepReal
        }
    }
    realStepFramesBuffer
  }
  override final def getSampleRangeReal(xDataTiming: NNDataTiming): SampleRangeValid = {
    getSampleRangeValid(xDataTiming)
    //realSegmentBufferRefresh(xDataTiming)
    //Range.inclusive( realStartFrameBuffer, realLastFrameBuffer, getRealStep(xDataTiming))
    //Range.inclusive( 0, xDataTiming.segmentLength(getRealSegment(xDataTiming)), getRealStep(xDataTiming))
  }

  override final def getSampleRangeValid(xDataTiming: NNDataTiming): SampleRangeValid = {
    realSegmentBufferRefresh(xDataTiming)
    new SampleRangeValid( realStartFrameBuffer, realLastFrameBuffer, getRealStep(xDataTiming), realSegmentBuffer )
//    realSegmentBufferRefresh(xDataTiming)
//    val realSegment = new SampleRangeReal(realStartFrameBuffer, realLastFrameBuffer, getRealStep(xDataTiming), realSegmentBuffer)
//    realSegment.getSampleRangeValid(xDataTiming)
  }
  
  // </editor-fold>

  private var realSegmentXFrameBuffer: NNDataTiming = null
  private var realSegmentBuffer = -1
  private var realStartFrameBuffer = -1
  private var realLastFrameBuffer = -1

  private def realSegmentBufferRefresh(xDataTiming: NNDataTiming): Unit = {
    if( realSegmentXFrameBuffer != xDataTiming) {
      val fs1 = xDataTiming.convertTsToFrsg(startTS)
      val fs2 = xDataTiming.convertTsToFrsg(lastTS)
      loggerRequire(fs1._2 == fs2._2, "The two specified timestamps belong to different recording segments " +
        fs1._2.toString + " and " + fs2._2.toString)
      realSegmentXFrameBuffer = xDataTiming
      realSegmentBuffer = fs1._2

      val tempLen = xDataTiming.segmentLength( realSegmentBuffer )
      if(fs1._1 < 0 || fs2._1 > tempLen )
        logger.warn("The TS specified frames [{}, {}] are out of range, this might be unintended.", fs1.toString(), fs2.toString())
      realStartFrameBuffer = fs1._1
      realLastFrameBuffer  = fs2._1
      //println("Real segment buff " + realSegmentBuffer.toString )
    }
  }
}