package nounou.analysis.units

import nounou.data._
import nounou.data.filters._
import nounou._
import scala.beans.BeanProperty
import breeze.linalg._
import breeze.numerics.abs
import breeze.stats.{median, mean}
import scala.collection.mutable.{ArrayBuffer}
import nounou.ranges._
import scala.collection.mutable
import scala.collection.parallel.mutable.ParHashSet
import breeze.stats.distributions.RandBasis

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
//  // <editor-fold defaultstate="collapsed" desc=" set/getWaveformData ">
//
//  private var waveformData: XData = XDataNull
//  def setWaveformData(xData: XData): Unit = {waveformData=xData}
//  def getWaveformData(): XData = waveformData
//
//  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" set/getTrodes ">

  private var trodes: XTrodes = XTrodesNull
  def setTrodes(xTrodes: XTrodes): Unit = {trodes=xTrodes}
  def getTrodes(): XTrodes = trodes

  // </editor-fold>


  final def detectSpikeTs(trode: Int): Array[Long] = detectSpikeTs( trode, RangeFrAll())
  final def detectSpikeTs(trode: Int, segment: Int): Array[Long] = detectSpikeTs( trode, RangeFrAll(segment))
  def detectSpikeTs(trode: Int, frameRange: RangeFr): Array[Long]


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

// <editor-fold defaultstate="collapsed" desc=" SpkDetQuiroga ">

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

    detectSpikeTsImpl(trode, frameRange).map(p => getTriggerData().frsgToTs(p+frameRange.start, frameRange.segment))
  }

  def detectSpikeTsImpl(trode: Int, frameRange: RangeFr): Array[Int] = {

    val channels = getTrodes.trodeChannels(trode)//trodeCount.flatMap( p => xTrodes.trodeGroup(p) )
    val tempData: Array[Array[Int]] = channels.map( p => getTriggerData.readTrace(p, frameRange).toArray )
    logger.trace("Thresholding with {} channels in trode #{}", channels.length.toString, trode.toString)

    val thresholds: Array[Int] = tempData.map( p => ( median( abs(DenseVector(p)) ) / 0.6745 * thresholdSD).toInt )
//    loggerRequire( tempData.length == thresholds.length,
//        "Must specify the same number of thresholds as data traces: {} != {} ",
//      tempData.length.toString, thresholds.length.toString)
    logger.info("Thresholding respective channels with values: {}", DenseVector(thresholds).toString)

    val tempDataAbs = tempData.map( p=> abs(DenseVector(p)).toArray )

    val tempRet = new ArrayBuffer[Int]()
    var index = 0
    val channelRange = Range(0, tempDataAbs.length)
    var triggered = !channelRange.forall( p => tempDataAbs(p)(0) < thresholds(p) )
    val blackout = getBlackoutFr()

    while(index < tempDataAbs(0).length){
      if( !triggered ){
        if( !channelRange.forall(p => tempDataAbs(p)(index) < thresholds(p) ) ){
          tempRet.append(index)
          triggered = true
          index += blackout
        } else{
          index += 1
        }
      } else {
        if( channelRange.forall(p => tempDataAbs(p)(index) < thresholds(p) ) ) triggered = false
        index += 1
      }
    }

    tempRet.toArray
  }

}

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc=" SpkDetPeak ">


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


  override def detectSpikeTsImpl(trode: Int, frameRange: RangeFr): Array[Int] = {

    val channels = getTrodes.trodeChannels(trode)//trodeCount.flatMap( p => xTrodes.trodeGroup(p) )

    logger.trace("Thresholding with {} channels in trode #{}", channels.length.toString, trode.toString)

    val tempDataAdj: Array[DenseVector[Int]] = if(getSpikeDirection() == 1){
                        channels.map( p => getTriggerData.readTrace(p, frameRange) )
                      } else if (getSpikeDirection() == -1 ) {
                        channels.map( p => getTriggerData.readTrace(p, frameRange) * -1 )
                      } else {
                        throw loggerError("spike direction must be set to +/- 1")
                      }

    val thresholds: Array[Int] =
      if( tempDataAdj(0).length < 32000){
        tempDataAdj.map( p => ( median( abs(p) ) / 0.6745 * thresholdSD).toInt )
      } else {
        val samps = tempDataAdj.map( dv => DenseVector.rand(32000).map( p => dv( (p * tempDataAdj(0).length).toInt ) ) )
        samps.map( p => ( median( abs(p) ) / 0.6745 * thresholdSD).toInt )
      }
    logger.info("Thresholding respective channels with values: {}", DenseVector(thresholds).toString)

    val tempRet = new ArrayBuffer[Int]()//new ParHashSet[Int]()
    val pw = peakWindow

    var index = pw
    //val parch = Range(0, tempDataAdj.length).par
    while(index < tempDataAdj(0).length - 2*pw){
//    for(index <- Range(pw, tempDataAdj(0).length - 2* pw).par ){
    //      parch.map( ch => {
//        val temppeak = tempDataAdj(ch)(index)
//        if (
//          tempDataAdj(ch).slice(index - pw, index + 2 * pw + 1).forallValues((p: Int) => temppeak >= p) &&
//            temppeak - min(tempDataAdj(ch).slice(index - pw, index)) > thresholds(ch) &&
//            temppeak - min(tempDataAdj(ch).slice(index + 1, index + 2 * pw + 1)) > thresholds(ch)
//        ) {
//          tempRet.+:(index)
//        }
//      })
//      }
//      index += 1
      var ch = 0
      while(ch < tempDataAdj.length){
        val temppeak = tempDataAdj(ch)(index)
        if(
          tempDataAdj(ch).slice(index - pw, index + 2*pw + 1).forallValues( (p: Int) => temppeak >= p) &&
          temppeak - min( tempDataAdj(ch).slice(index - pw, index) ) > thresholds(ch) &&
          temppeak - min( tempDataAdj(ch).slice(index + 1, index + 2*pw + 1) ) > thresholds(ch)
        ){
          tempRet += index
        }
        ch += 1
      }
      index += 1
    }
    tempRet.toArray//.sorted
  }


}

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc=" SpkDetPeakWidth ">

object SpkDetPeakWidth extends SpkDetPeakWidth {
  def it = this
}

class SpkDetPeakWidth extends SpkDetQuiroga {

  _thresholdSD = 2d

  def getInverted() = -1

  @BeanProperty
  var peakWidthMin = 0.1 //3.2 frames at 32kHz (spikes will look thinner with higher baseline)
  @BeanProperty
  var peakWidthMax = 0.4 //12.8 frames at 32kHz
  @BeanProperty
  var medianFilterWindowLength = 2.5 // 80 frames at 32 kHz

  override def detectSpikeTsImpl(trode: Int, frameRange: RangeFr): Array[Int] = {

    loggerRequire( frameRange.step == 1, "step size for spike detection must be 1! {} is invalid!", frameRange.step.toString )

    val channels = getTrodes.trodeChannels(trode)
    val channelIndex = Range(0, channels.length)
    logger.trace("Thresholding with {} channels in trode #{}", channels.length.toString, trode.toString)

    //set median filter for detection
    val medianData = new XDataFilterMedianSubtract( getTriggerData() )
    medianData.setWindowLength( medianData.msToFr( getMedianFilterWindowLength() ) )
    val invertedData = new XDataFilterInvert( medianData )
    invertedData.setInverted( getInverted() )
    val bufferedData = new XDataFilterBuffer( invertedData )

    val fr = frameRange.getFrameRange(medianData).getValidRange(medianData)

    //calculate thresholds
    val thresholds: Array[Int] =
      if( fr.length < 10000 ){
      //if the data range is short enough, take the median estimate from the whole data range
        channels.map( ch => ( median( abs(  invertedData.readTrace(ch, frameRange)  ) ) / 0.6745 * thresholdSD).toInt )
      } else {
        //if the data range is long, take random samples for cutoff SD estimate
        val samps = channels.map( ch => randomInt( 1000, (0, invertedData.segmentLength( frameRange.segment )-1 ) ).toArray.map( p => invertedData.readPoint( ch, p ) ) )
        samps.map( p => ( median( abs( DenseVector(p) ) ) / 0.6745 * getThresholdSD() ).toInt )
      }
    logger.info("Thresholding respective channels with values: {}", DenseVector(thresholds).toString)

    //val tempRet = new ArrayBuffer[Int]()// with mutable.SynchronizedBuffer[Int]//new ParHashSet[Int]()
    val pwMin = invertedData.msToFr(getPeakWidthMin())
    val pwMax = invertedData.msToFr(getPeakWidthMax())

    //var index = pwMax
    //val parch = channelIndex
    //val parch = channelIndex.par
    //val tempData = channelIndex.map( ch => )
    channelIndex.flatMap( ch => detectSpikeTsImplImpl(bufferedData.readTrace(ch, frameRange), thresholds(ch), pwMin, pwMax) ).toSet[Int].toArray.sorted
  }


  def detectSpikeTsImplImpl( trace: DenseVector[Int], threshold: Int, pwMin: Int, pwMax: Int ): Array[Int] = {
    var index = pwMax
    val tempRet2 = new ArrayBuffer[Int]()
    while(index < trace.length - pwMax) {
      val tempPeakVal = trace(index)
      val tempCutoff = (tempPeakVal + threshold)/2d   //cutoff is the half point between peak and 2SD

      var break = false
      if( tempCutoff > threshold ) {

        var preIndex = index
        var postIndex = index
        break = false
        while (!break && preIndex >= index - pwMax) {
          val pointVal = trace(preIndex)
          if (pointVal > tempPeakVal) {
            //if there is a data point that increases above the peak, the peak is not a peak
            preIndex = Int.MinValue
            break = true
          } else if (pointVal <= tempCutoff) {
            //if the data point goes under the cutoff, we have our trigger point for the width
            break = true
          } else {
            //otherwise, just step backward in time
            preIndex -= 1
          }
        }

        if (preIndex != Int.MinValue) {
          //loop after the peak only if the part before the peak passes muster
          break = false
          while (!break && postIndex <= index + pwMax) {
            val pointVal = trace(postIndex)
            if (pointVal > tempPeakVal) {
              //if there is a data point that increases above the peak, the peak is not a peak
              postIndex = Int.MaxValue
              break = true
            } else if (pointVal <= tempCutoff) {
              //if the data point goes under the cutoff, we have our trigger point for the width
              break = true
            } else {
              //otherwise, just step forward in time
              postIndex += 1
            }
          }

        }

        //if the spike width is within the prespecified range, append the peak index to the return list
        val spikeWidth = postIndex-preIndex
        if( pwMin <= spikeWidth && spikeWidth <= pwMax ) tempRet2 += index
      }
      index += 1
    }
    tempRet2.toArray
  }


}

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc=" SpkDetSlope ">

object SpkDetSlope extends SpkDetSlope{
  def it = this
}

class SpkDetSlope extends SpkDetPeak {

  _thresholdSD = 2d

  // <editor-fold defaultstate="collapsed" desc=" get/setSlopeWindow ">

  var _slopeWindow = 3  //integrate over 3 segments, 4 points
  def slopeWindow_=(frames: Int): Unit = {
    loggerRequire( frames > 0, "Peak windows must be > 0, value {} is invalid!", frames.toString )
    _slopeWindow = frames
  }
  def slopeWindow = _slopeWindow
  def setSlopeWindow(frames: Int): Unit = { slopeWindow = frames }
  def getSlopeWindow() = slopeWindow

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" get/setBaselineWindow ">

  var _baselineWindow = 16 // baseline over 0.5 ms at 32 kHz
  def baselineWindow_=(frames: Int): Unit = {
    loggerRequire( frames > 0, "Baseline windows must be > 0, value {} is invalid!", frames.toString )
    _baselineWindow = frames
  }
  def baselineWindow = _baselineWindow
  def setBaselineWindow(frames: Int): Unit = { baselineWindow = frames }
  def getBaselineWindow() = baselineWindow

  // </editor-fold>


  override def detectSpikeTsImpl(trode: Int, frameRange: RangeFr): Array[Int] = {

    val channels = getTrodes.trodeChannels(trode)//trodeCount.flatMap( p => xTrodes.trodeGroup(p) )
    val tempData: Array[Array[Int]] = channels.map( p => getTriggerData.readTrace(p, frameRange).toArray )
    logger.trace("Thresholding with {} channels in trode #{}", channels.length.toString, trode.toString)

    val thresholds: Array[Int] = tempData.map( p => ( median( abs(DenseVector(p)) ) / 0.6745 * thresholdSD).toInt )
    //    loggerRequire( tempData.length == thresholds.length,
    //        "Must specify the same number of thresholds as data traces: {} != {} ",
    //      tempData.length.toString, thresholds.length.toString)
    logger.info("Thresholding respective channels with values: {}", DenseVector(thresholds).toString)

    val tempDataAdj: Array[DenseVector[Int]] = if(getSpikeDirection() == 1){
      tempData.map(p => (DenseVector(p)))
    } else if (getSpikeDirection() == -1 ) {
      tempData.map(p => (DenseVector(p) * -1))
    } else {
      throw loggerError("spike direction must be set to +/- 1")
    }

    val tempRet = new ArrayBuffer[Int]()
    //val channels = Range(0, tempDataAbs.length)
    val triggered = Array.tabulate(tempDataAdj.length)(p => -1)
    val windowMaxIndex = Array.tabulate(tempDataAdj.length)(p => -1)
    val windowMax = Array.tabulate(tempDataAdj.length)(p => Integer.MIN_VALUE)
    val sw = slopeWindow
    val pw = peakWindow
    val bw = baselineWindow
    val baselineQueue: Array[mutable.Queue[Int]] = Array.tabulate(tempDataAdj.length)(p => mutable.Queue[Int]() )

    var index = scala.math.max( sw, bw )
    for(channel <- 0 until tempDataAdj.length) { baselineQueue(channel) ++= tempDataAdj(channel)( index - bw to index -1 ).toArray }
    val baselineSum: Array[Long] = baselineQueue.map( p => sum( convert( new DenseVector( p.toArray ), Long ) ) )
    var ch = 0
    while(index < tempDataAdj(0).length){
      ch = 0
      while(ch < tempDataAdj.length){
        if(triggered(ch)<0) {
          if( tempDataAdj(ch)( index ) -  baselineSum(ch) / bw >= thresholds(ch) ) {
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
        baselineSum(ch) -= baselineQueue(ch).dequeue()
        baselineQueue(ch) += tempDataAdj(ch)(index)
        baselineSum(ch) += tempDataAdj(ch)(index)
        ch += 1
      }
      index += 1

    }

    tempRet.toArray
  }


}

// </editor-fold>