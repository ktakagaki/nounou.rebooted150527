import nounou.data.Frame
import nounou.util.LoggingExt

/**
 * @author ktakagaki
 * @date 2/4/14.
 */
package object nounou extends LoggingExt {

  // <editor-fold defaultstate="collapsed" desc=" rangeInclusiveToFrameRange ">

  import nounou.data.ranges._
  implicit def rangeInclusiveToFrameRange(range: Range.Inclusive): RangeFr = rangeInclusiveToFrameRange(range, 0)
  implicit def rangeInclusiveToFrameRange(range: Range.Inclusive, segment: Int): RangeFr = {
    require( range.step > 0, "Only positive steps are allowed for indexing in nounou!" )
    require( range.start <= range.end, "In nounous, start <= lastValid is required for frame nounou.data.ranges. start=" + range.start + ", lastValid=" + range.end)

    RangeFr(range.start, range.end, range.step, OptSegment(segment))
  }

  // </editor-fold>

  //General options for nounou

  abstract class Opt extends breeze.util.Opt

//  case class OptStep(step: Int) extends Opt {
//    require(step>0, "optStep must be one or larger!")
//  }
//  val OptStep1 = OptStep(1)
  case class OptSegment(segment: Int) extends Opt {
    require(segment > -1, "optSegment must be -1 (non-specified) or larger!")
  }
  val OptSegmentNone = OptSegment(-1)
  case object OptNull extends Opt

}