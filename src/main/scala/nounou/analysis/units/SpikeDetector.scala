package nounou.analysis.units

import nounou.data.{XTrodes, XDataNull, XSpikes, XData}
import nounou.data.filters.{XDataFilterNull, XDataFilterFIR, XDataFilter}
import nounou.{LoggingExt, OptSpikeDetectorFlush, FrameRange}
import scala.beans.BeanProperty
import breeze.linalg.DenseVector
import breeze.numerics.abs
import breeze.stats.median
import scala.collection.mutable.{ArrayBuffer}

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
    loggerRequire( frameRange.step == 1, "Currently, SpikeDetector classes must be called with a frame range with step = 1. {} is invalid", frameRange.step.toString)

    val channels = trodes.flatMap( p => xSpikes.trodeLayout.trodeGroup(p) )
    val tempData = channels.map( p => xData.readTrace(p, frameRange, segment).toArray )
    val thresholds = tempData.map( p => (median( abs(DenseVector(p)) ) / 0.6745 * absThresholdSD).toInt )
    val blackoutSamples = xData.msToFrame( blackoutMs )
    val timestamps = thresholder(tempData, thresholds, blackoutSamples).map(p => xData.frameSegmentToTS(p+frameRange.start, segment))


  }

  //ToDo 4: make into general breeze function
  def thresholder( vect: Array[Array[Int]], thresholds: Array[Int], blackout: Int): Array[Int] = {
    val tempRet = new ArrayBuffer[Int]()
    var index = 0
    var channels = Range(0, vect.length)
    var triggered = !channels.forall( p => vect(p)(0) < thresholds(0) )
    while(index < vect.length){
      if( !triggered ){
        if( !channels.forall(p => vect(p)(index) < thresholds(p) ) ){
          tempRet.append(index)
          triggered = true
          index += blackout
        } else{
          index += 1
        }
      } else{
        if( channels.forall(p => vect(p)(index) < thresholds(p) ) ) triggered = false
        index += 1
      }
    }

    tempRet.toArray
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