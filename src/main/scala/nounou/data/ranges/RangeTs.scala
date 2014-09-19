package nounou.data.ranges

import nounou._
import nounou.data.traits.XFrames

/**Encapsulates a TS(timestamp, Long)-based frame range, with appropriate values.
 * @author ktakagaki
 * @date 3/19/14.
 */
object RangeTs {

  //class All(step: Double) extends RangeMs(0, 0, step, true)

//  /** Gives a [[nounou.data.ranges.RangeFrSpecifier]] object which specifies
//    * start, end, and step based on timestamps (absolute microseconds).
//    * @param step must be -1 (default; one data frame step) or a real frame step that amounts to > 0
//    *             when converted with the appropriate frame rate
//    */
//  def apply(startTs: Long, lastTs: Long, step: Long, optSegment: OptSegment) = new RangeTs(startTs, lastTs, step, optSegment)

  /** Gives a [[nounou.data.ranges.RangeFrSpecifier]] object which specifies
    * start, end, and step based on timestamps (absolute microseconds).
    * @param step must be -1 (default; one data frame step) or a real frame step that amounts to > 0
    *             when converted with the appropriate frame rate
    */
  def apply(startTs: Long, lastTs: Long, step: Long) = new RangeTs(startTs, lastTs, step)

//  /** Gives a [[nounou.data.ranges.RangeFrSpecifier]] object which specifies
//    * start, end, and step based on timestamps (absolute microseconds).
//    * Step size will be a default value of 1 data frame.
//    */
//  def apply(startTs: Long, lastTs: Long, optSegment: OptSegment) = new RangeTs(startTs, lastTs, optSegment)

  /** Gives a [[nounou.data.ranges.RangeFrSpecifier]] object which specifies
    * start, end, and step based on timestamps (absolute microseconds).
    * Step size will be a default value of 1 data frame.
    */
  def apply(startTs: Long, lastTs: Long) = new RangeTs(startTs, lastTs)

}

class RangeTs(val startTs: Long, val lastTs: Long, val stepTs: Long) extends RangeFrSpecifier {

  override def getSegment(): Int = -1
  override def getOptSegment(): OptSegment = OptSegment(-1)

  def this(startTs: Long, endTs: Long) = this(startTs, endTs, -1L)

  private var realSegmentXFrameBuffer: XFrames = null
  private var realSegmentBuffer = -1
  private var realStartFrameBuffer = -1
  private var realLastFrameBuffer = -1

  private def realSegmentBufferRefresh(xFrames: XFrames): Unit = {
    if( realSegmentXFrameBuffer != xFrames) {
      val fs1 = xFrames.convertTS2FS(startTs)
      val fs2 = xFrames.convertTS2FS(lastTs)
      loggerRequire(fs1._2 == fs2._2, "The two specified timestamps belong to different recording segments " +
        fs1._2.toString + " and " + fs2._2.toString)
      realSegmentXFrameBuffer = xFrames
      realSegmentBuffer = fs1._2
      realStartFrameBuffer = fs1._1
      realLastFrameBuffer  = fs2._1
      println("Real segment buff " + realSegmentBuffer.toString )
    }
  }

  override def getRealSegment(xFrames: XFrames): Int = {
    realSegmentBufferRefresh(xFrames)
    realSegmentBuffer
  }
  override def getRealStepFrames(totalLength: Int): Int = {
    val stepReal = if(stepTs == -1L) 1 else (stepTs.toDouble * totalLength.toDouble / 1000000d).toInt
    loggerRequire(stepReal > 0, "This amounts to a negative time step! (stepTs=" + stepTs + " micro s => " + stepReal + " frames)")
    stepReal
  }

  def getRangeFr(xFrames: XFrames): RangeFr = {
    realSegmentBufferRefresh(xFrames: XFrames)
    RangeFr(realStartFrameBuffer, realLastFrameBuffer, getRealStepFrames(xFrames), OptSegment(realSegmentBuffer))
  }

}