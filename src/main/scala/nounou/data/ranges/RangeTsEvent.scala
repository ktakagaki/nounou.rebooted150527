package nounou.data.ranges

import nounou._
import nounou.data.traits.XDataTiming

/**Encapsulates a TS(timestamp, Long)-based frame range, with appropriate values.
  * @author ktakagaki
  * @date 3/19/14.
  */
object RangeTSEvent {

  /** Gives a [[nounou.data.ranges.RangeFrSpecifier]] object which specifies
    * an event range based on timestamps (absolute microseconds) and pre/post frames.
    * @param step must be -1 (default; one data frame step) or a real frame step that amounts to > 0
    *             when converted with the appropriate frame rate
    */
  def apply(eventTS: Long, preFrames: Int, postFrames: Int, step: Int) = new RangeTSEvent(eventTS, preFrames, postFrames, step)

  /** Gives a [[nounou.data.ranges.RangeFrSpecifier]] object which specifies
    * an event range based on timestamps (absolute microseconds) and pre/post frames.
    * Step size will be a default value of 1 data frame.
    */
  def apply(eventTS: Long, preFrames: Int, postFrames: Int) = new RangeTSEvent(eventTS, preFrames, postFrames)

}

class RangeTSEvent(val eventTS: Long, val preFrames: Int, val postFrames: Int, val step: Int) extends RangeFrSpecifier {

  override def toString() = s"RangeTsEvent($eventTS, $preFrames, $postFrames, $step)"

  def this(eventTS: Long, preFrames: Int, postFrames: Int) = this(eventTS, preFrames, postFrames, 1)

  loggerRequire( step>=1, "Int step size must be 1 or greater!")


  // <editor-fold defaultstate="collapsed" desc=" RangeFrSpecifier methods ">

  override def getRealSegment(xDataTiming: XDataTiming): Int = {
    realSegmentBufferRefresh(xDataTiming)
    realEventSegmentBuffer
  }

  override def getRealStepFrames(xDataTiming: XDataTiming): Int = step

  override final def getRealRange(xDataTiming: XDataTiming): Range.Inclusive = {
    realSegmentBufferRefresh(xDataTiming)
    Range.inclusive( realEventFrameBuffer-preFrames, realEventFrameBuffer+postFrames, getRealStepFrames(xDataTiming))
    //Range.inclusive( 0, xDataTiming.segmentLength(getRealSegment(xDataTiming)), getRealStepFrames(xDataTiming))
  }

  override final def getValidRange(xDataTiming: XDataTiming): Range.Inclusive = {
    realSegmentBufferRefresh(xDataTiming)
    val realSegment = RangeFr(realEventFrameBuffer-preFrames, realEventFrameBuffer+postFrames, getRealStepFrames(xDataTiming), OptSegment(realEventSegmentBuffer))
    //val realSegment = RangeFr(0, xFrames.segmentLength(realSegmentBuffer), getRealStepFrames(xFrames), OptSegment(realSegmentBuffer))
    realSegment.getValidRange(xDataTiming)
  }

  // </editor-fold>

  private var xFrameBuffer: XDataTiming = null
  private var realEventSegmentBuffer = -1
  private var realEventFrameBuffer = -1

  private def realSegmentBufferRefresh(xDataTiming: XDataTiming): Unit = {
    if( xFrameBuffer != xDataTiming) {
      val fs = xDataTiming.convertTStoFS(eventTS)
      xFrameBuffer = xDataTiming
      realEventSegmentBuffer = fs._2
      realEventFrameBuffer = fs._1
      if(realEventFrameBuffer  < 0 || realEventFrameBuffer  >= xFrameBuffer.segmentLength(realEventSegmentBuffer) )
        logger.warn("The TS specified event frame {} is out of range, this might be unintended.", fs.toString())
      //println("Real segment buff " + realSegmentBuffer.toString )
    }
  }




}