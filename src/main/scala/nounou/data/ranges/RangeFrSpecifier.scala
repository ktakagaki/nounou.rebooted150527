package nounou.data.ranges

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

  /** Returns the segment number for the frame range.
    */
  def segment(): Int

  /** Returns the equivalent RangeFr object, to which other operations can be delegated.
    * This is especially relevant for classes such as [[nounou.data.ranges.RangeMs]],
    * where frame ranges must be realized based on the [[nounou.data.traits.XFrames]] sampling rate.
   */
  def getRangeFr(x: XFrames): RangeFr

  /** Returns a [[scala.Range.Inclusive]] which exclusively includes frame indexes which
    * are both within the specified data range and
    * the range provided by the [[nounou.data.traits.XFrames]] object.
   */
  def getValidRange(x: XFrames): Range.Inclusive = getRangeFr(x).getValidRange(x)

}
