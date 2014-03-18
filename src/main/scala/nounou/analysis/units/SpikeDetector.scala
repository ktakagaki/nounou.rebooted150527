package nounou.analysis.units

import nounou.data.{XTrodes, XDataNull, XSpikes, XData}
import nounou.data.filters.{XDataFilterNull, XDataFilterFIR, XDataFilter}
import nounou.{LoggingExt, OptSpikeDetectorFlush, FrameRange}
import scala.beans.BeanProperty
import breeze.linalg.DenseVector
import breeze.numerics.abs
import breeze.stats.median

/**
 * @author ktakagaki
 * @date 3/14/14.
 */
abstract class SpikeDetector extends LoggingExt {

  var _blackoutMs = 1.5d
  def blackoutMs_=(blackout: Double): Unit = {
    loggerRequire( blackout > 0, "Blackout must be > 0, value {} is invalid!", blackout.toString )
    _blackoutMs = blackout
  }
  def blackoutMs = _blackoutMs
  def setBlackoutMs(blackout: Double) = blackoutMs_=(blackout)
  def getBlackoutMs() = blackoutMs

  final def apply(xData: XData, xSpikes: XSpikes, trode: Int, optSpikeDetectorFlush: OptSpikeDetectorFlush): Unit =
        apply( xData, xSpikes, Array[Int](trode), optSpikeDetectorFlush )

  final def apply(xData: XData, xSpikes: XSpikes, trodes: Array[Int], optSpikeDetectorFlush: OptSpikeDetectorFlush): Unit =
    apply( xData, xSpikes, trodes, FrameRange.all(), optSpikeDetectorFlush )

  def apply(xData: XData, xSpikes: XSpikes, trodes: Array[Int], frameRange: FrameRange, optSpikeDetectorFlush: OptSpikeDetectorFlush): Unit =
    apply( xData, xSpikes, trodes, FrameRange.all(), 0, optSpikeDetectorFlush )

  def apply(xData: XData, xSpikes: XSpikes, trodes: Array[Int], frameRange: FrameRange, segment: Int, optSpikeDetectorFlush: OptSpikeDetectorFlush): Unit

  //ToDo ability to handle traces/trace arrays directly

}

abstract class SpikeDetectorQuiroga extends SpikeDetector {

  @BeanProperty
  var absThresholdSD: Double = 3d

  override def apply(xData: XData, xSpikes: XSpikes, trodes: Array[Int], frameRange: FrameRange, segment: Int, optSpikeDetectorFlush: OptSpikeDetectorFlush) = {
    val channels = trodes.flatMap( p => xSpikes.trodeLayout.trodeGroup(p) )
    val tempData = channels.map( p => xData.readTrace(p, frameRange, segment))
    val thresholds = tempData.map( p => median( abs(p) ) / 0.6745 * absThresholdSD )


  }

//  protected def setPreFilter(frameRange: FrameRange, segment: Int): Unit
//
//  @BeanProperty
//  protected var filter: XDataFilter = XDataFilterNull

  override def toString() = "Quiroga spike detection algorithm with threshold set at +/-" +
    absThresholdSD + " deviations of the median estimate for standard deviation."

}

//object SpikeDetectorQuirogaFIR extends SpikeDetectorQuiroga {
//
//  @BeanProperty
//  var lowpassHz = 300d
//  @BeanProperty
//  var highpassHz = 3000d
//  @BeanProperty
//  var taps = 1024 // 32kHz/1024 = 31.25Hz
//
//  @BeanProperty
//  override var filter: XDataFilterFIR = new XDataFilterFIR(XDataNull)
//
//  override def setPreFilter(xData: XData): Unit = {
//    if( filter.getFilterHz() != Vector( lowpassHz, highpassHz) || filter.taps != taps ) {
//      filter = new XDataFilterFIR( xDataOri )
//      filter.setTaps( taps )
//      filter.setFilterHz(lowpassHz, highpassHz)
//    }
//  }
//
//  override def apply(xData: XData, xSpikes: XSpikes, trodes: Array[Int], frameRange: FrameRange, optSpikeDetectorFlush: OptSpikeDetectorFlush) = {
//
//  }
//
//}