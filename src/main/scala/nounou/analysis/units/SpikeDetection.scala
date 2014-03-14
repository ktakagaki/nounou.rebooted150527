package nounou.analysis.units

import nounou.data.{XDataNull, XSpikes, XData}
import nounou.data.filters.{XDataFilterFIR, XDataFilter}

/**
 * @author ktakagaki
 * @date 3/14/14.
 */
abstract class SpikeDetection {

  def apply(xDataOri: XData, xTrode: XTrodeLayout, trodes: Vector[Int]): XSpikes

}

object SDQuiroga extends SpikeDetection {

  var lowpassHz = 300d
  var highpassHz = 3000d
  var thresholdSD = 3
  var firFilter: XDataFilterFIR = new XDataFilterFIR(XDataNull)

  override def toString() = "Quiroga spike detection algorithm with pre-filter of: \n" +
    firFilter.toString + "\n " +
    "and threshold set at +/-" + thresholdSD + " deviations of the median estimate for standard deviation."

}