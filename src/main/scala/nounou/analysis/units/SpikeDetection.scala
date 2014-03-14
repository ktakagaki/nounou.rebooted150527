package nounou.analysis.units

import nounou.data.{XTrodes, XDataNull, XSpikes, XData}
import nounou.data.filters.{XDataFilterFIR, XDataFilter}
import nounou.FrameRange
import scala.beans.BeanProperty
import breeze.linalg.DenseVector

/**
 * @author ktakagaki
 * @date 3/14/14.
 */
abstract class SpikeDetector(val xDataOri: XData, val xTrodes: XTrodes) {

  final def apply(trode: Int) = apply( Array[Int](trode) )

  final def apply(trodes: Array[Int]) = apply( trodes, FrameRange.all() )

  final def apply(trodes: Array[Int], frameRange: FrameRange) = apply( trodes, frameRange, 0 )

  def apply(trodes: Array[Int], frameRange: FrameRange, segment: Int): Vector[XSpikes]

}

abstract class SDQuiroga(override val xDataOri: XData, override val xTrodes: XTrodes) extends SpikeDetector(xDataOri, xTrodes) {

  @BeanProperty
  var thresholdSD = 3

  protected def setPreFilter(frameRange: FrameRange, segment: Int): Unit
  protected var filter: XDataFilter

  override def toString() = "Quiroga spike detection algorithm with pre-filter of: \n" +
    filter.toString + "\n " +
    "and threshold set at +/-" + thresholdSD + " deviations of the median estimate for standard deviation."

}

class SDQuirogaFIR(override val xDataOri: XData, override val xTrodes: XTrodes) extends SDQuiroga(xDataOri, xTrodes) {

  @BeanProperty
  var lowpassHz = 300d
  @BeanProperty
  var highpassHz = 3000d
  @BeanProperty
  var taps = 1024 // 32kHz/1024 = 31.25Hz

  protected var filter: XDataFilterFIR = new XDataFilterFIR(XDataNull)

  override def setPreFilter(frameRange: FrameRange, segment: Int): Unit = {
    if( filter.getFilterHz() != Vector( lowpassHz, highpassHz) ) {
      filter = new XDataFilterFIR( xDataOri )
      filter.setFilterHz(lowpassHz, highpassHz, taps)
    }
  }

  def apply(trodes: Array[Int], frameRange: FrameRange, segment: Int) = {

  }

}