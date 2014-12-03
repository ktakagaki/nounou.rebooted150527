package nounou.analysis

import nounou.Opt

/**
 * @author ktakagaki
 * @date 07/14/2014.
 */
package object units {

  abstract class OptAnalysisUnits extends Opt

  /**Blackout time in frames*/
  case class OptBlackoutFr(frames: Int) extends OptAnalysisUnits
  case class OptPeakHalfWidthMaxFr(frames: Int) extends OptAnalysisUnits
  case class OptPeakHalfWidthMinFr(frames: Int) extends OptAnalysisUnits
  case class OptThresholdPeakDetectWindow(frames: Int) extends OptAnalysisUnits
//  case class OptTraceSDReadLengthFr(frames: Int) extends OptAnalysisUnits

}
