package nounou.data.ranges

import nounou.OptSegment
import nounou.data.traits.XFrames
import nounou.util.LoggingExt

/** This trait specifies a range of data frames to extract, for instance, when reading traces.</br>
  * It allows specification of ranges such as "all frames"
  * ([[nounou.data.ranges.RangeFrAll]]) and millisecond- or timestamp(Long)-
  * dependent frame ranges. These specifications can only be resolved to real data frame
  * ranges using sampling information given in the actual data
  * ([[nounou.data.traits.XFrames]]).
  */
trait RangeFrSpecifier extends LoggingExt {

  // <editor-fold defaultstate="collapsed" desc=" range accessors ">

//  def getSegment(): Int
//  def getOptSegment(): OptSegment
//  def getRealSegment(xFrames: XFrames) = getOptSegment.getRealSegment(xFrames)
//  final def getRealStepFrames(xFrames: XFrames): Int = getRealStepFrames(xFrames.segmentLength(getRealSegment(xFrames)))
  /** Returns the real segment number for the frame range, taking into account -1 for automatic determination.
    */
  def getRealSegment(xFrames: XFrames): Int
  /** Returns the real frame steps for the frame range, taking into account -1 for automatic determination.
    */
  def getRealStepFrames(xFrames: XFrames): Int
  /** Returns the real frame range with steps for the frame range, taking into account -1 for automatic determination
    * and specifications such as RangeFrAll.
    */
  def getRealRange(xFrames: XFrames): Range.Inclusive
  /** Returns the valid, frame range with present data for the frame range, taking into account -1 for automatic determination
    * and specifications such as RangeFrAll.
    */
  def getValidRange(xFrames: XFrames): Range.Inclusive

//  /** Returns the equivalent RangeFr object, to which other operations can be delegated.
//    * This is especially relevant for classes such as [[nounou.data.ranges.RangeMs]],
//    * where frame ranges must be realized based on the [[nounou.data.traits.XFrames]] sampling rate.
//   */
//  def getRangeFr(x: XFrames): RangeFr

//  def getRangeSegment(xFrames: XFrames): OptSegment = {
//    segment match {
//      case -1 => {
//        loggerRequire(xFrames.segmentCount==1, "RangeFrAll was specified without a segment. Only single-segment data can be specified in this way.")
//        OptSegment(0)
//      }
//      case _ =>
//        optSegment
//    }
//  }

//  /** Returns a [[scala.Range.Inclusive]] which exclusively includes frame indexes which
//    * are both within the specified data range and
//    * the range provided by the [[nounou.data.traits.XFrames]] object.
//   */
//  def getValidRange(x: XFrames): Range.Inclusive = getRangeFr(x).getValidRange(x)

  // </editor-fold>

}
