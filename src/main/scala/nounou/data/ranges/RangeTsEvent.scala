package nounou.data.ranges

import nounou._
import nounou.data.traits.XFrames

object RangeTsEvent {

  /** Constructor for a frame specification based on timestamps and pre/post data points, with default segment = 0.
    * @param preFrames number of data points to take prior to given timestamp. Data points will be taken in steps of `step`
    * @param postFrames number of data points to take following given timestamp. Data points will be taken in steps of `step`
    */
  def apply(eventTs: Long, preFrames: Int, postFrames: Int) = new RangeTsEvent(eventTs, preFrames, postFrames)
  def apply(eventTs: Array[Long], preFrames: Int, postFrames: Int) = eventTs.map(new RangeTsEvent(_, preFrames, postFrames))

}


class RangeTsEvent(val eventTS: Long, val preFrames: Int, val postFrames: Int/*, val optSegment: OptSegment*/) extends RangeFrSpecifier {

//  def segment() = optSegment.segment

//  def this(eventTS: Long, preFrames: Int, postFrames: Int) = this(eventTS, preFrames, postFrames, OptSegmentNone)

  val step = 1

  def getRangeFr(x: XFrames): RangeFr = {
    val eventFrame = x.tsToFr(eventTS)//x.tsToFrsg(eventTS)
    RangeFr(eventFrame - preFrames*step, eventFrame + postFrames*step, step/*, OptSegment(eventFrame._2)*/)
  }

}

