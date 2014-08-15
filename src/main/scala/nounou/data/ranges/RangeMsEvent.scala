package nounou.data.ranges

import nounou._
import nounou.data.traits.XFrames

object RangeMsEvent {

  /** Constructor for a frame specification based on timestamps and pre/post data points, with default segment = 0.
    */
//  def apply(eventMs: Double, preMs: Double, postMs: Double, stepMs: Double, optSegment: OptSegment) =
//    new RangeMsEvent(eventMs, preMs, postMs, stepMs, optSegment)
//  def apply(eventMs: Double, preMs: Double, postMs: Double, optSegment: OptSegment) =
//    new RangeMsEvent(eventMs, preMs, postMs, optSegment)
  def apply(eventMs: Double, preMs: Double, stepMs: Double, postMs: Double) = new RangeMsEvent(eventMs, preMs, postMs, stepMs)
  def apply(eventMs: Double, preMs: Double, postMs: Double) = new RangeMsEvent(eventMs, preMs, postMs)
//  def apply(eventMs: Array[Double], preMs: Double, postMs: Double, stepMs: Double, optSegment: OptSegment) =
//    eventMs.map( new RangeMsEvent(_, preMs, postMs, stepMs, optSegment))
  def apply(eventMs: Array[Double], preMs: Double, postMs: Double, stepMs: Double) =
    eventMs.map( new RangeMsEvent(_, preMs, postMs, stepMs))
//  def apply(eventMs: Array[Double], preMs: Double, postMs: Double, optSegment: OptSegment) =
//    eventMs.map( new RangeMsEvent(_, preMs, postMs, optSegment))
  def apply(eventMs: Array[Double], preMs: Double, postMs: Double) = eventMs.map( new RangeMsEvent(_, preMs, postMs))

}


class RangeMsEvent(val eventMs: Double, val preMs: Double, val postMs: Double, val stepMs: Double/*, val optSegment: OptSegment*/) extends RangeFrSpecifier {

//  def segment() = optSegment.segment

//  def this(eventMs: Double, preMs: Double, postMs: Double, stepMs: Double) = this(eventMs, preMs, postMs, stepMs, OptSegmentNone)
//  def this(eventMs: Double, preMs: Double, postMs: Double, optSegment: OptSegment) = this(eventMs, preMs, postMs, -1d, optSegment)
  def this(eventMs: Double, preMs: Double, postMs: Double) = this(eventMs, preMs, postMs, -1d)//, OptSegmentNone)

  def getRangeFr(x: XFrames): RangeFr = {
    RangeMs(eventMs-preMs, eventMs+postMs, stepMs/*, optSegment*/).getRangeFr(x)
  }

}
