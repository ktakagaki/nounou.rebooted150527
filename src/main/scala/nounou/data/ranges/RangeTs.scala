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

class RangeTs(val startTs: Long, val lastTs: Long, val stepTs: Long/*, val optSegment: OptSegment*/) extends RangeFrSpecifier {

//  def segment() = optSegment.segment

//  def this(startTs: Long, endTs: Long, stepTs: Long) = this(startTs, endTs, stepTs, OptSegmentNone)
//  def this(startTs: Long, endTs: Long, optSegment: OptSegment) = this(startTs, endTs, -1L, optSegment)
  def this(startTs: Long, endTs: Long) = this(startTs, endTs, -1L)//, OptSegmentNone)

  def getRangeFr(x: XFrames): RangeFr = {

    val stepReal = if(stepTs == -1L) 1
    else {
      loggerRequire(stepTs > 0, "Timestamp steps must be -1 (1 frame) or >0! (stepTs=" + stepTs + " )")
      ( x.sampleRate * stepTs / 1000000d ).toInt
    }

    val startReal = x.tsToFr(startTs)//sg(startTs) //ToDo 2: expand to send segment info too in RangeFr
    val endReal = x.tsToFr(lastTs)//sg(lastTs)
//    loggerRequire(startReal._2 == endReal._2, "The two specified timestamps belong to different recording segments {}  and {}.", startReal._2.toString, endReal._2.toString)

    RangeFr(startReal, endReal, stepReal)//, OptSegment(segment))
  }

}