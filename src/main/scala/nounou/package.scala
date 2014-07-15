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

    new RangeFr(Frame(range.start, segment), Frame(range.end, segment), OptStep(range.step))
  }

  // </editor-fold>

  //General options for nounou

  abstract class Opt extends breeze.util.Opt

  case class OptStep(step: Int) extends Opt {
    require(step>0, "optStep must be one or larger!")
  }
  case object OptNull extends Opt

}