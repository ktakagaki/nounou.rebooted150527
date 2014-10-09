package nounou.data.ranges

import nounou._
import nounou.data.traits.XDataTiming

/**Encapsulates a TS(timestamp, Long)-based frame range, with appropriate values.
 * @author ktakagaki
 * @date 3/19/14.
 */
object RangeTs {

  /** Gives a [[nounou.data.ranges.RangeFrSpecifier]] object which specifies
    * start, end, and step based on timestamps (absolute microseconds).
    * @param step must be -1 (default; one data frame step) or a real frame step that amounts to > 0
    *             when converted with the appropriate frame rate
    */
  def apply(startTs: Long, lastTs: Long, step: Long) = new RangeTs(startTs, lastTs, step)

  /** Gives a [[nounou.data.ranges.RangeFrSpecifier]] object which specifies
    * start, end, and step based on timestamps (absolute microseconds).
    * Step size will be a default value of 1 data frame.
    */
  def apply(startTs: Long, lastTs: Long) = new RangeTs(startTs, lastTs)

}

class RangeTs(val startTs: Long, val lastTs: Long, val stepTs: Long) extends RangeFrSpecifier {

  def this(startTs: Long, endTs: Long) = this(startTs, endTs, -1L)

  // <editor-fold defaultstate="collapsed" desc=" RangeFrSpecifier methods ">
    
  override def getRealSegment(xDataTiming: XDataTiming): Int = {
    realSegmentBufferRefresh(xDataTiming)
    realSegmentBuffer
  }

  private var realStepFramesBuffer = -1
  override def getRealStepFrames(xDataTiming: XDataTiming): Int = {
    if(realStepFramesBuffer == -1) {
      realStepFramesBuffer =
        if (stepTs == -1L) 1
        else {
          val stepReal = (stepTs.toDouble * xDataTiming.sampleRate / 1000000d).toInt
          loggerRequire(stepReal > 0, "This amounts to a negative time step! (stepTs=" + stepTs + " micro s => " + stepReal + " frames)")
          stepReal
        }
    }
    realStepFramesBuffer
  }
  override final def getRealRange(xDataTiming: XDataTiming): Range.Inclusive = {
    realSegmentBufferRefresh(xDataTiming)
    Range.inclusive( realStartFrameBuffer, realLastFrameBuffer, getRealStepFrames(xDataTiming))
    //Range.inclusive( 0, xDataTiming.segmentLength(getRealSegment(xDataTiming)), getRealStepFrames(xDataTiming))
  }

  override final def getValidRange(xDataTiming: XDataTiming): Range.Inclusive = {
    realSegmentBufferRefresh(xDataTiming)
    val realSegment = RangeFr(realStartFrameBuffer, realLastFrameBuffer, getRealStepFrames(xDataTiming), OptSegment(realSegmentBuffer))
    //val realSegment = RangeFr(0, xFrames.segmentLength(realSegmentBuffer), getRealStepFrames(xFrames), OptSegment(realSegmentBuffer))
    realSegment.getValidRange(xDataTiming)
  }
  
  // </editor-fold>

  private var realSegmentXFrameBuffer: XDataTiming = null
  private var realSegmentBuffer = -1
  private var realStartFrameBuffer = -1
  private var realLastFrameBuffer = -1

  private def realSegmentBufferRefresh(xDataTiming: XDataTiming): Unit = {
    if( realSegmentXFrameBuffer != xDataTiming) {
      val fs1 = xDataTiming.convertTStoFS(startTs)
      val fs2 = xDataTiming.convertTStoFS(lastTs)
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






//class All(step: Double) extends RangeMs(0, 0, step, true)

//  /** Gives a [[nounou.data.ranges.RangeFrSpecifier]] object which specifies
//    * start, end, and step based on timestamps (absolute microseconds).
//    * @param step must be -1 (default; one data frame step) or a real frame step that amounts to > 0
//    *             when converted with the appropriate frame rate
//    */
//  def apply(startTs: Long, lastTs: Long, step: Long, optSegment: OptSegment) = new RangeTs(startTs, lastTs, step, optSegment)


//  /** Gives a [[nounou.data.ranges.RangeFrSpecifier]] object which specifies
//    * start, end, and step based on timestamps (absolute microseconds).
//    * Step size will be a default value of 1 data frame.
//    */
//  def apply(startTs: Long, lastTs: Long, optSegment: OptSegment) = new RangeTs(startTs, lastTs, optSegment)



//  private def getRangeFr(xFrames: XFrames): RangeFr = {
//    realSegmentBufferRefresh(xFrames)
//    RangeFr(realStartFrameBuffer, realLastFrameBuffer, getRealStepFrames(xFrames), OptSegment(realSegmentBuffer))
//  }

//  override def getSegment(): Int = -1
//  override def getOptSegment(): OptSegment = OptSegment(-1)
