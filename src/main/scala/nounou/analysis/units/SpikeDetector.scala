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

object SpkDetQuiroga extends SpkDetQuiroga{
  def it = this
}
class SpkDetQuiroga extends SpkDetBlackout {

  // <editor-fold defaultstate="collapsed" desc=" thresholdSD ">

  protected var _thresholdSD = 3d
  def thresholdSD_=(threshold: Double): Unit = {
    loggerRequire( threshold > 0, "Threshold SD multiple must be > 0, value {} is invalid!", threshold.toString )
    _thresholdSD = threshold
  }
  def thresholdSD = _thresholdSD
  def setThresholdSD(thresholdSD: Double): Unit = { _thresholdSD=thresholdSD }
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
    val tempData = channels.map( p => getTriggerData.readTrace(p, frameRange).toArray )
    logger.trace("Thresholding with {} channels in trode #{}", channels.length.toString, trode.toString)

    val thresholds = tempData.map( p => ( median( abs(DenseVector(p)) ) / 0.6745 * thresholdSD).toInt )

    detectSpikeTsImpl(tempData, thresholds).map(p => getTriggerData().frsgToTs(p+frameRange.start, frameRange.segment))
  }

  def detectSpikeTsImpl( tempData: Array[Array[Int]], thresholds: Array[Int]): Array[Int] = {

    loggerRequire( tempData.length == thresholds.length,
        "Must specify the same number of thresholds as data traces: {} != {} ",
      tempData.length.toString, thresholds.length.toString)

    logger.info("Thresholding respective channels with values: {}", DenseVector(thresholds).toString)

    val tempDataAbs = tempData.map( p=> abs(DenseVector(p)).toArray )

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

object SpkDetPeak extends SpkDetPeak{
  def it = this
}
class SpkDetPeak extends SpkDetQuiroga {

  _thresholdSD = 2d

  def getSpikeDirection() = -1

  // <editor-fold defaultstate="collapsed" desc=" get/setPeakWindow ">

  var _peakWindow = 16 //0.5 ms at 32kHz
  def peakWindow_=(frames: Int): Unit = {
    loggerRequire( frames > 0, "Peak windows must be > 0, value {} is invalid!", frames.toString )
    _peakWindow = frames
  }
  def peakWindow = _peakWindow
  def setPeakWindow(frames: Int): Unit = { peakWindow = frames }
  def getPeakWindow() = peakWindow

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" get/setSlopeWindow ">

  var _slopeWindow = 3  //integrate over 3 segments, 4 points
  def slopeWindow_=(frames: Int): Unit = {
    loggerRequire( frames > 0, "Peak windows must be > 0, value {} is invalid!", frames.toString )
    _slopeWindow = frames
  }
  def slopeWindow = _slopeWindow
  def setSlopeWindow(frames: Int): Unit = { slopeWindow = frames }
  def getSlopeWindow() = peakWindow

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" get/setBaselineWindow ">

  var _baselineWindow = 16 // baseline over 0.5 ms at 32 kHz
  def baselineWindow_=(frames: Int): Unit = {
    loggerRequire( frames > 0, "Peak windows must be > 0, value {} is invalid!", frames.toString )
    _baselineWindow = frames
  }
  def baselineWindow = _baselineWindow
  def setBaselineWindow(frames: Int): Unit = { baselineWindow = frames }
  def getBaselineWindow() = baselineWindow

  // </editor-fold>


  override def detectSpikeTsImpl( tempData: Array[Array[Int]], thresholds: Array[Int]): Array[Int] = {

    loggerRequire( tempData.length == thresholds.length,
      "Must specify the same number of thresholds as data traces: {} != {} ",
      tempData.length.toString, thresholds.length.toString)

    logger.info("Thresholding respective channels with values: {}", DenseVector(thresholds).toString)
    
    val tempDataAdj: Array[Array[Int]] = if(getSpikeDirection() == 1){
                        tempData
                      } else if (getSpikeDirection() == -1 ) {
                        tempData.map(p => (DenseVector(p) * -1).toArray)
                      } else {
                        throw loggerError("spike direction must be set to +/- 1")
                      }

    val tempRet = new ArrayBuffer[Int]()
    //val channels = Range(0, tempDataAbs.length)
    val triggered = Array.tabulate(tempDataAdj.length)(p => -1)
    val windowMaxIndex = Array.tabulate(tempDataAdj.length)(p => -1)
    val windowMax = Array.tabulate(tempDataAdj.length)(p => Integer.MIN_VALUE)
    val sw = getSlopeWindow()
    val pw = getPeakWindow()

    var index = sw
    var ch = 0
    while(index < tempDataAdj(0).length){
      ch = 0
      while(ch < tempDataAdj.length){
        if(triggered(ch)<0) {
          if( tempDataAdj(ch)( index ) - tempDataAdj(ch)( index - sw ) >= thresholds(ch)) {
            //if the channel isn't triggered yet, but now meets trigger criteria
            triggered(ch) = index// - sw
            windowMax(ch) = tempDataAdj(ch)(index)
            windowMaxIndex(ch) = index
          }
        } else {
        //if the trigger has previously been triggered
          if( index > triggered(ch) + pw ){
          //If the peak return window has already passed, (and the value has not sunken below the original value; implied)
            triggered(ch) = -1
            windowMax(ch) = Integer.MIN_VALUE
            windowMaxIndex(ch) = -1
          } else if (tempDataAdj(ch)(index) <= tempDataAdj(ch)(triggered(ch))) {
          //The value has just sunken below the triggered value => register as detected spike and reset triggered, etc.
            tempRet += windowMaxIndex(ch)
            triggered(ch) = -1
            windowMax(ch) = Integer.MIN_VALUE
            windowMaxIndex(ch) = -1
          } else if ( tempDataAdj(ch)(index) > windowMax(ch) ){
          //(the value is still above original), but the current value is bigger than the previous max
            windowMax(ch) = tempDataAdj(ch)(index)
            windowMaxIndex(ch) = index
          }
        }
        ch += 1
      }
      index += 1
    }

    tempRet.toArray
  }


}