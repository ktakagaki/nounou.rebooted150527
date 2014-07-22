package nounou.data.ranges

import nounou.data.traits.XFrames
import nounou.util.LoggingExt

/** This trait can be passed to objects to allow specification of data frames.
  * It allows one to specify frame ranges such as "all frames"
  * ([[nounou.data.ranges.RangeFrAll]]) and millisecond- or timestamp(Long)-
  * dependent frame ranges, which can only be resolved to real data frame
  * ranges using sampling information given in the actual data.
  * ([[nounou.data.traits.XFrames]]).
  */
trait RangeFrSpecifier extends LoggingExt {

  /** Returns the segment number for the frames
    */
  def segment(): Int

  def getRangeFr(x: XFrames): RangeFr

  def getValidRange(x: XFrames): Range.Inclusive = getRangeFr(x).getValidRange(x)

}
