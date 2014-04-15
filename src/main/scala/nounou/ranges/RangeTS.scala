package nounou.ranges

import nounou.data.traits.XFrames

/**Encapsulates a TS(timestamp, Long)-based frame range, with appropriate values.
 * @author ktakagaki
 * @date 3/19/14.
 */
object RangeTs {

  //class All(step: Double) extends RangeMs(0, 0, step, true)

  /** Most common constructor.
    * @param step must amount to a real frame step that is > 0, when converted with the appropriate frame rate
    * @param segment this parameter specifies the recording segment, active in certain file formats
    */
  def apply(startTs: Long, endTs: Long, step: Long, segment: Int) = new RangeTs(startTs, endTs, step, segment)

  /** Most common constructor, with default segment = 0.
    * @param step must amount to a real frame step that is > 0, when converted with the appropriate frame rate
    */
  def apply(startTs: Long, endTs: Long, step: Long) = new RangeTs(startTs, endTs, step)

}

class RangeTs(val startTs: Long, val endTs: Long, val step: Long, val segment: Int) extends RangeFrSpecifier {

  def this(startTs: Long, endTs: Long, step: Long) = this(startTs, endTs, step, 0)

  def getFrameRange(x: XFrames): RangeFr = {

      val stepReal = (step * x.sampleInterval/* * 1000d*/).toInt
      loggerRequire(stepReal>0, "This amounts to a negative or zero timestep! (stepMs=" + step + " ms)")

      val startReal = x.tsToFrsg(startTs) //ToDo 2: expand to send segment info too in RangeFr
      val endReal = x.tsToFrsg(endTs)
      loggerRequire(startReal._2 == endReal._2, "The two specified timestamps belong to different recording segments {}  and {}.", startReal._2.toString, endReal._2.toString)

      new RangeFr(startReal._1, endReal._1, stepReal, segment)
  }

}


object RangeTsEvent {

//  /** Constructor for a frame specification based on timestamps and pre/post data points.
//    * @param preFrames number of data points to take prior to given timestamp. Data points will be taken in steps of `step`
//    * @param postFrames number of data points to take following given timestamp. Data points will be taken in steps of `step`
//    */
//  def apply(eventTs: Long, preFrames: Int, postFrames: Int, step: Int, segment: Int) =
//    new RangeTsEvent(eventTs,  preFrames, postFrames, step, segment)

  /** Constructor for a frame specification based on timestamps and pre/post data points, with default segment = 0.
    * @param preFrames number of data points to take prior to given timestamp. Data points will be taken in steps of `step`
    * @param postFrames number of data points to take following given timestamp. Data points will be taken in steps of `step`
    */
  def apply(eventTs: Long, preFrames: Int, postFrames: Int, step: Int) = new RangeTsEvent(eventTs, preFrames, postFrames, step)
  def apply(eventTs: Long, preFrames: Int, postFrames: Int) = new RangeTsEvent(eventTs, preFrames, postFrames)
  def apply(eventTss: Array[Long], preFrames: Int, postFrames: Int, step: Int) = eventTss.map(new RangeTsEvent(_, preFrames, postFrames, step))
  def apply(eventTss: Array[Long], preFrames: Int, postFrames: Int) = eventTss.map(new RangeTsEvent(_, preFrames, postFrames))

}


class RangeTsEvent(val eventTS: Long, val preFrames: Int, val postFrames: Int, val step: Int) extends RangeFrSpecifier {

  def this(eventTS: Long, preFrames: Int, postFrames: Int) = this(eventTS, preFrames, postFrames, 1)

  def getFrameRange(x: XFrames): RangeFr = {
    val eventFrame = x.tsToFrsg(eventTS)
    RangeFr(eventFrame._1 - preFrames*step, eventFrame._1 + postFrames*step, step, eventFrame._2)
  }

}

