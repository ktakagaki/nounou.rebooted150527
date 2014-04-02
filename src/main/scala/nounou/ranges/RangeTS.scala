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
  def apply(startTS: Long, endTS: Long, step: Long, segment: Int) = new RangeTs(startTS, endTS, step, segment, false)

  /** Most common constructor, with default segment = 0.
    * @param step must amount to a real frame step that is > 0, when converted with the appropriate frame rate
    */
  def apply(startTS: Long, endTS: Long, step: Long) = new RangeTs(startTS, endTS, step, 0, false)

}

class RangeTs(val startTS: Long, val endTS: Long, val step: Long, val segment: Int, val isAll: Boolean) extends RangeFrSpecifier {

//  def this(startTS: Long, endTS: Long, step: Long, segment: Int) = this(startTS, endTS, step, segment, false)
//  def this(startTS: Long, endTS: Long, step: Long) = this(startTS, endTS, step, 0, false)

  def this(segment: Int, isAll: Boolean) = this(0L, 0L, 0L, segment, true)
  def this(isAll: Boolean) = this(0, true)

  def getFrameRange(x: XFrames): RangeFr = {

    if(isAll){
      RangeFrAll( /*stepReal,*/ step = 1, segment = segment )
    } else {

      val stepReal = (step * x.sampleInterval/* * 1000d*/).toInt
      loggerRequire(stepReal>0, "This amounts to a negative or zero timestep! (stepMs=" + step + " ms)")

      val startReal = x.tsToFrsg(startTS) //ToDo 2: expand to send segment info too in RangeFr
      val endReal = x.tsToFrsg(endTS)
      loggerRequire(startReal._2 == endReal._2, "The two specified timestamps belong to different recording segments {}  and {}.", startReal._2.toString, endReal._2.toString)

      new RangeFr(startReal._1, endReal._1, stepReal, segment)
    }
  }

}


object RangeTsEvent {

  /** Most common constructor for a frame specification based on timestamps and pre/post data points.
    * @param preFrames number of data points to take prior to given timestamp. Data points will be taken in steps of `step`
    * @param postFrames number of data points to take following given timestamp. Data points will be taken in steps of `step`
    * @param segment this parameter specifies the recording segment, active in certain file formats
    */
  def apply(eventTS: Long, preFrames: Int, postFrames: Int, step: Int, segment: Int) =
    new RangeTsEvent(eventTS,  preFrames, postFrames, step, segment)

  /** Constructor for a frame specification based on timestamps and pre/post data points, with default segment = 0.
    * @param preFrames number of data points to take prior to given timestamp. Data points will be taken in steps of `step`
    * @param postFrames number of data points to take following given timestamp. Data points will be taken in steps of `step`
    */
  def apply(eventTS: Long, preFrames: Int, postFrames: Int, step: Int) =
    new RangeTsEvent(eventTS,  preFrames, postFrames, step, 0)

}


class RangeTsEvent(val eventTS: Long, val preFrames: Int, val postFrames: Int, val step: Int, val segment: Int) extends RangeFrSpecifier {

  //def this(eventTS: Int, preFrames: Int, postFrames: Int, step: Int) = this(eventTS, preFrames, postFrames, step, 0)

  def getFrameRange(x: XFrames): RangeFr = {
    val eventFrame = x.tsToFrsg(eventTS)
    RangeFr(eventFrame._1 - preFrames*step, eventFrame._1 + postFrames*step, step, segment)
  }

}

