//import nounou.data.Frame

import nounou.data.traits.XDataTiming
import nounou.util.LoggingExt

/**
 * @author ktakagaki
 * @date 2/4/14.
 */
package object nounou extends LoggingExt {

  // <editor-fold defaultstate="collapsed" desc=" rangeInclusiveToFrameRange ">

  import nounou.data.ranges._

//  //This implicit should not be triggered within the package (see next comment)... check by commenting and compiling
//  //Use of this implicit implies OptSegmentAutomatic, i.e., a single-segment data set
//  implicit def implRangeInclusiveToFR(range: Range.Inclusive): RangeFr = {//rangeInclusiveToFrameRange(range, 0)
//    //implicit def rangeInclusiveToFrameRange(range: Range.Inclusive, segment: Int): RangeFr = {
//    loggerRequire( range.step > 0, "Only positive steps are allowed for indexing in nounou!" )
//    loggerRequire( range.start <= range.end,
//      "In nounous, start <= lastValid is required for frame nounou.data.ranges. start=" + range.start + ", lastValid=" + range.end)
//    RangeFr(range.start, range.end, range.step, OptSegmentAutomatic)
//  }

  // </editor-fold>

  //General options for nounou

  abstract class Opt extends breeze.util.Opt
  case object OptNull extends Opt

//  case class OptStep(step: Int) extends Opt {
//    require(step>0, "optStep must be one or larger!")
//  }
//  val OptStep1 = OptStep(1)

  case class OptSegment(segment: Int) extends Opt {
    loggerRequire(segment >= -1, "OptSegment value must be -1 (not specified) or larger!")

    override def toString() = s"OptSegment($segment)"

  }
  val OptSegmentAutomatic = OptSegment(-1)


}