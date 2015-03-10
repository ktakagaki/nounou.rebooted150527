package nounou.elements.ranges

import nounou.elements.data.traits.NNDataTiming
import nounou.util.LoggingExt

/** This trait specifies a range of data samples to extract, for instance, when reading data traces.
  * It allows specification of ranges such as "all samples"
  * ([[nounou.elements.ranges.SampleRangeAll]]) and millisecond- or timestamp(Long)-
  * dependent sample ranges. These latter specifications can only be resolved to real data frame
  * ranges using sampling information given in the actual data
  * ([[NNDataTiming]]).
  */
trait SampleRangeSpecifier extends LoggingExt {

  /** Returns the real segment number for the frame range, taking into account -1 for automatic determination.
    */
  def getRealSegment(xDataTiming: NNDataTiming): Int

  /** Returns the real step for the sample range in units of single data samples,
    * taking into account -1 for automatic determination.
    */
  def getRealStep(xDataTiming: NNDataTiming): Int
  
  /** Returns the concrete real sample range with start (can be negative, starting before the data),
    * end (can be beyond end of assumed data as specified in xDataTiming),
    * steps (must be positive int), and segment (present within assumed data).
    */
  def getSampleRangeReal(xDataTiming: NNDataTiming): SampleRangeReal
  /** Returns the concrete valid sample range with start/end (within assumed data),
    * steps (must be positive int), and segment (present within assumed data).
    * In contrast to [[getSampleRangeReal()]], the resulting sample range here cuts off overhangs.
    */
  def getSampleRangeValid(xDataTiming: NNDataTiming): SampleRangeValid
  /** Returns [[getSampleRangeValid]], along with pre- and post- padding sample counts
    * for when the original sample range exceeds/overhangs the available data.
    */
  def getSampleRangeValidPrePost(xDataTiming: NNDataTiming): (Int, SampleRangeValid, Int)

  // </editor-fold>

}











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
//  def getRangeFrValid(x: XFrames): Range.Inclusive = getRangeFr(x).getRangeFrValid(x)



//  def getSegment(): Int
//  def getOptSegment(): OptSegment
//  def getRealSegment(xFrames: XFrames) = getOptSegment.getRealSegment(xFrames)
//  final def getRealStep(xFrames: XFrames): Int = getRealStep(xFrames.segmentLength(getRealSegment(xFrames)))
