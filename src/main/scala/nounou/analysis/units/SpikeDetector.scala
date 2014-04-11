package nounou.analysis.units

import nounou.data._
import nounou.data.filters.{XDataFilterNull, XDataFilterFIR, XDataFilter}
import nounou._
import scala.beans.BeanProperty
import breeze.linalg.{max, DenseVector}
import breeze.numerics.abs
import breeze.stats.median
import scala.collection.mutable.{ArrayBuffer}
import nounou.ranges._

/**
 * @author ktakagaki
 * @date 3/14/14.
 */
abstract class SpikeDetector extends LoggingExt {

  // <editor-fold defaultstate="collapsed" desc=" set/getTriggerData ">

  private var triggerData: XData = XDataNull
  def setTriggerData(xData: XData): Unit = {triggerData=xData}
  def getTriggerData(): XData = triggerData

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" set/getTriggerData ">

  private var waveformData: XData = XDataNull
  def setWaveformData(xData: XData): Unit = {waveformData=xData}
  def getWaveformData(): XData = waveformData

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" set/getTrodes ">

  private var trodes: XTrodes = XTrodesNull
  def setTrodes(xTrodes: XTrodes): Unit = {trodes=xTrodes}
  def getTrodes(): XTrodes = trodes

  // </editor-fold>


  final def detectSpikeTs(trode: Int): Array[Long] = detectSpikeTs( trode, RangeFrAll())
  final def detectSpikeTs(trode: Int, segment: Int): Array[Long] = detectSpikeTs( trode, RangeFrAll(segment))
  def detectSpikeTs(trode: Int, frameRange: RangeFr): Array[Long]

  //ToDo ability to handle traces/trace arrays directly

}

trait SpkDetBlackout extends SpikeDetector {

  def getBlackoutFr() = getTriggerData().msToFr( getBlackoutMs() )

  // <editor-fold defaultstate="collapsed" desc=" blackoutMS ">

  var _blackoutMs = 1.5d
  def blackoutMs_=(blackout: Double): Unit = {
    loggerRequire( blackout > 0, "Blackout must be > 0, value {} is invalid!", blackout.toString )
    _blackoutMs = blackout
  }
  def blackoutMs = _blackoutMs
  def setBlackoutMs(blackout: Double) = blackoutMs_=(blackout)
  def getBlackoutMs() = blackoutMs

  // </editor-fold>

}

object SpkDetQuiroga extends SpkDetQuiroga
class SpkDetQuiroga extends SpkDetBlackout {

  // <editor-fold defaultstate="collapsed" desc=" thresholdSD ">

  protected var _thresholdSD = 3d
  def thresholdSD_=(threshold: Double): Unit = {
    loggerRequire( threshold > 0, "Threshold SD multiple must be > 0, value {} is invalid!", threshold.toString )
    _thresholdSD = threshold
  }
  def thresholdSD = _thresholdSD
  def setThresholdSD(absThresholdSD: Double) = _thresholdSD=(thresholdSD)
  def getThresholdSD() = thresholdSD

  // </editor-fold>

  override def detectSpikeTs(trode: Int, frameRange: RangeFr): Array[Long] = {
    loggerRequire( frameRange.step == 1, "Currently, SpikeDetector classes must be called with a frame range with step = 1. {} is invalid", frameRange.step.toString)
    loggerRequire( getTriggerData() != XDataNull, "Must set XData first to detect spikes. Use setTriggerData(x: XData).")
    if( getTrodes == XTrodesNull ) {
      logger.warn("XTrodes have not been set. Using default layout with 1-trodes per each channel of the data.")
      setTrodes(new XTrodesIndividual(getTriggerData.channelCount))
    }

    val channels = getTrodes.trodeChannels(trode)//trodeCount.flatMap( p => xTrodes.trodeGroup(p) )
    val tempDataAbs = channels.map( p => abs(getTriggerData.readTrace(p, frameRange)).toArray )
    logger.trace("Thresholding with {} channels in trode #{}", channels.length.toString, trode.toString)

    val thresholds = tempDataAbs.map( p => ( median( DenseVector(p) ) / 0.6745 * thresholdSD).toInt )
    val blackoutSamples = getTriggerData.msToFr( blackoutMs )

    detectSpikeTsImpl(tempDataAbs, thresholds, blackoutSamples).map(p => getTriggerData().frsgToTs(p+frameRange.start, frameRange.segment))
  }

  def detectSpikeTsImpl( tempDataAbs: Array[Array[Int]], thresholds: Array[Int]): Array[Int] = {

    loggerRequire( tempDataAbs.length == thresholds.length,
        "Must specify the same number of thresholds as data traces: {} != {} ",
        tempDataAbs.length.toString, thresholds.length.toString)

    logger.info("Thresholding respective channels with values: {}", DenseVector(thresholds).toString)

    val tempRet = new ArrayBuffer[Int]()
    var index = 0
    val channels = Range(0, tempDataAbs.length)
    var triggered = !channels.forall( p => tempDataAbs(p)(0) < thresholds(p) )
    val blackout = getBlackoutFr()

    while(index < tempDataAbs(0).length){
      if( !triggered ){
        if( !channels.forall(p => tempDataAbs(p)(index) < thresholds(p) ) ){
          tempRet.append(index)
          triggered = true
          index += blackout
        } else{
          index += 1
        }
      } else {
        if( channels.forall(p => tempDataAbs(p)(index) < thresholds(p) ) ) triggered = false
        index += 1
      }
    }

    tempRet.toArray
  }

}

object SpkDetPeak extends SpkDetPeak
class SpkDetPeak extends SpkDetQuiroga {

  override protected var _thresholdSD = 2d

  // <editor-fold defaultstate="collapsed" desc=" get/setPeakWindow ">

  var _peakWindow = 10
  def peakWindow_=(frames: Int): Unit = {
    loggerRequire( frames > 0, "Peak windows must be > 0, value {} is invalid!", frames.toString )
    _peakWindow = frames
  }
  def peakWindow = _peakWindow
  def setPeakWindow(frames: Int) = peakWindow_=(frames)
  def getPeakWindow() = peakWindow

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" get/setSlopeWindow ">

  var _slopeWindow = 3
  def slopeWindow_=(frames: Int): Unit = {
    loggerRequire( frames > 0, "Peak windows must be > 0, value {} is invalid!", frames.toString )
    _slopeWindow = frames
  }
  def slopeWindow = _slopeWindow
  def setSlopeWindow(frames: Int) = slopeWindow_=(frames)
  def getSlopeWindow() = peakWindow

  // </editor-fold>


  override def detectSpikeTsImpl( tempDataAbs: Array[Array[Int]], thresholds: Array[Int]): Array[Int] = {

    loggerRequire( tempDataAbs.length == thresholds.length,
      "Must specify the same number of thresholds as data traces: {} != {} ",
      tempDataAbs.length.toString, thresholds.length.toString)

    logger.info("Thresholding respective channels with values: {}", DenseVector(thresholds).toString)

    val tempRet = new ArrayBuffer[Int]()
    var index = 0
    val channels = Range(0, tempDataAbs.length)
    var triggered = 0
    val blackout = getBlackoutFr()

    while(index < tempDataAbs(0).length){
      if( triggered > 0 ){
        if( !channels.forall(p => tempDataAbs(p)(index) < thresholds(p) ) ){
          tempRet.append(index)
          triggered = true
          index += blackout
        } else{
          index += 1
        }
      } else {
        if( channels.forall(p => tempDataAbs(p)(index) < thresholds(p) ) ) triggered = false
        index += 1
      }
    }

    tempRet.toArray
  }


}