//package nounou.analysis.units
//
//import nounou.data._
//import nounou.data.filters._
//import nounou._
//import scala.beans.BeanProperty
//import breeze.linalg._
//import breeze.numerics.abs
//import breeze.stats.{median, mean}
//import scala.collection.mutable.{ArrayBuffer}
//import nounou.nounou.data.ranges._
//import scala.collection.mutable
//import scala.collection.parallel.mutable.ParHashSet
//import breeze.stats.distributions.RandBasis
//
///**
// * @author ktakagaki
// * //@date 3/14/14.
// */
//abstract class SpikeDetector extends LoggingExt {
//
//  // <editor-fold defaultstate="collapsed" desc=" set/getTriggerData ">
//
//  private var triggerData: XData = XDataNull
//  def setTriggerData(xData: XData): Unit = {triggerData=xData}
//  def getTriggerData(): XData = triggerData
//
//  // </editor-fold>
//  // <editor-fold defaultstate="collapsed" desc=" set/getTrodes ">
//
//  private var trodes: XTrodes = XTrodesNull
//  def setTrodes(xTrodes: XTrodes): Unit = {trodes=xTrodes}
//  def getTrodes(): XTrodes = trodes
//
//  // </editor-fold>
//
//
//  final def detectSpikeTs(trode: Int): Array[Long] = detectSpikeTs( trode, RangeFrAll())
//  final def detectSpikeTs(trode: Int, segment: Int): Array[Long] = detectSpikeTs( trode, RangeFrAll(segment))
//  def detectSpikeTs(trode: Int, frameRange: RangeFr): Array[Long]
//
//
//}
//
//trait SpkDetBlackout extends SpikeDetector {
//
//  def getBlackoutFr() = getTriggerData().msToFr( getBlackoutMs() )
//
//  // <editor-fold defaultstate="collapsed" desc=" blackoutMS ">
//
//  var _blackoutMs = 1.5d
//  def blackoutMs_=(blackout: Double): Unit = {
//    loggerRequire( blackout > 0, "Blackout must be > 0, value {} is invalid!", blackout.toString )
//    _blackoutMs = blackout
//  }
//  def blackoutMs = _blackoutMs
//  def setBlackoutMs(blackout: Double) = blackoutMs_=(blackout)
//  def getBlackoutMs() = blackoutMs
//
//  // </editor-fold>
//
//}
//
//// <editor-fold defaultstate="collapsed" desc=" SpkDetPeakWidth ">
//
//object SpkDetPeakWidth extends SpkDetPeakWidth {
//  def it = this
//}
//
//class SpkDetPeakWidth extends SpikeDetector with SpkDetBlackout {
//
//  // <editor-fold defaultstate="collapsed" desc=" thresholdSD ">
//
//  protected var thresholdSD = 2d
//  def setThresholdSD(threshold: Double): Unit = {
//    loggerRequire( threshold > 0, "Threshold SD multiple must be > 0, value {} is invalid!", threshold.toString )
//    thresholdSD = threshold
//  }
//  def getThresholdSD() = thresholdSD
//
//  // </editor-fold>
//
//  def isInverted() = true
//
//  @BeanProperty
//  var peakWidthMin = 0.1 //3.2 frames at 32kHz (spikes will look thinner with higher baseline)
//  @BeanProperty
//  var peakWidthMax = 0.4 //12.8 frames at 32kHz
//  @BeanProperty
//  var medianFilterWindowLength = 2.5 // 80 frames at 32 kHz
//
//  override def detectSpikeTs(trode: Int, frameRange: RangeFr): Array[Long] = {
//    loggerRequire( frameRange.step == 1, "Currently, SpikeDetector classes must be called with a frame range with step = 1. {} is invalid", frameRange.step.toString)
//    loggerRequire( getTriggerData() != XDataNull, "Must set XData first to detect spikes. Use setTriggerData(x: XData).")
//    if( getTrodes == XTrodesNull ) {
//      logger.warn("XTrodes have not been set. Using default layout with 1-trodes per each channel of the data.")
//      setTrodes(new XTrodesIndividual(getTriggerData.channelCount))
//    }
//
//    detectSpikeTsImpl(trode, frameRange).map(p => getTriggerData().frsgToTs(p+frameRange.start, frameRange.segment))
//  }
//
//  @deprecated
//  def traceSD(channel: Int, frameRange: RangeFr): Int ={
//    //set median filter for detection
//    val medianData = new XDataFilterMedianSubtract( getTriggerData() )
//    medianData.setWindowLength( medianData.msToFr( getMedianFilterWindowLength() ) )
//
//    //buffer data for calculations
//    val bufferedData = new XDataFilterBuffer( medianData )
//    val fr = frameRange.getRangeFr(bufferedData).getRangeFrValid(bufferedData)
//    val readLength = 6400
//    if( fr.length < readLength*10 ){
//      //if the data range is short enough, take the median estimate from the whole data range
//      (median( abs(  bufferedData.readTrace(channel, frameRange)  ) ).toDouble / 0.6745 * thresholdSD).intValue
//    } else {
//      //if the data range is long, take random samples for cutoff SD estimate
//      val samp = randomInt( 10, (0, medianData.segmentLength( frameRange.segment )-1-readLength ) ).toArray.sorted.map(
//        (p: Int) => median(abs(medianData.readTrace( channel, RangeFr(p, p + readLength - 1, 1, frameRange.segment)) ))
//      )
//      (median( DenseVector(samp) ).toDouble / 0.6745 * getThresholdSD()).intValue
//    }
//
//  }
//
//  def detectSpikeTsImpl(trode: Int, frameRange: RangeFr): Array[Int] = {
//
//    loggerRequire( frameRange.step == 1, "step size for spike detection must be 1! {} is invalid!", frameRange.step.toString )
//
//    val channels = getTrodes.trodeChannels(trode)
//    val channelIndex = Range(0, channels.length)
//    logger.trace("Thresholding with {} channels in trode #{}", channels.length.toString, trode.toString)
//
//    //set median filter for detection
//    val medianData = new XDataFilterMedianSubtract( getTriggerData() )
//    medianData.setWindowLength( medianData.msToFr( getMedianFilterWindowLength() ) )
//
//    //set inversion if operative
//    val invertedData = if(isInverted){
//      val temp = new XDataFilterInvert( medianData )
//      temp.setInverted( isInverted() )
//      temp
//    } else {
//      medianData
//    }
//
//    //buffer data for calculations
//    val bufferedData = new XDataFilterBuffer( invertedData )
//
//    val fr = frameRange.getRangeFr(bufferedData).getRangeFrValid(bufferedData)
//
//    //calculate thresholds
//    val thresholds: Array[Int] = channels.map( traceSD(_, frameRange) )
//    logger.info("Thresholding respective channels with values: {}", DenseVector(thresholds).toString)
//
//    //val tempRet = new ArrayBuffer[Int]()// with mutable.SynchronizedBuffer[Int]//new ParHashSet[Int]()
//    val pwMin = invertedData.msToFr(getPeakWidthMin())
//    val pwMax = invertedData.msToFr(getPeakWidthMax())
//
//    val tempTraces = channels.map( ch => bufferedData.readTrace(ch, frameRange) ).zipWithIndex
//    tempTraces.par.flatMap( trace => detectSpikeTsImplImpl(trace._1, thresholds(trace._2), pwMin, pwMax)).toSet[Int].toArray.sorted
//  }
//
//
//  def detectSpikeTsImplImpl( trace: DenseVector[Int], threshold: Int, pwMin: Int, pwMax: Int ): Array[Int] = {
//
//    //this is where the return values are accumulated
//    val tempRet2 = new ArrayBuffer[Int]()
//    //    var (tempMax, tempMaxPos) = ((tr: DenseVector[Int]) => (max(tr), maxIndex(tr)))( trace(index - pwMax to index + pwMax)  )
//
//    var index = 2 * pwMax
//    //make sure that the start index is under the threshold, if the trace starts over the threshold
//    var continueLoopThreshold = (trace(0) >= threshold)
//    while (continueLoopThreshold && index < trace.length) {
//      //keep looping until first trace value which is smaller than the threshold
//      if (trace(index) < threshold) continueLoopThreshold = false
//      index += 1
//    }
//    //we are now under threshold at the index position
//
//    //main loop
//    while (index < trace.length - 2 * pwMax) {
//
//      if (trace(index) >= threshold) {
//        //if the threshold is newly crossed...
//
//        //...continue advancing the index until the local maximum position, within 2 spike halfwidths
//        var continueLoopLocalMax = true
//        while (continueLoopLocalMax && index < trace.length - 2 * pwMax) {
//          val maxI = argmax(trace(index to index + pwMax * 2))//maxIndex(trace(index to index + pwMax * 2))
//          //local maximum found
//          if (maxI < pwMax * 2) continueLoopLocalMax = false
//          index += maxI
//        }
//
//        //next, see if the local maximum point satisfies the spike width conditions
//        if( index < trace.length - pwMax ) {
//          val tempCutoff = (trace(index) /*+ threshold*/) / 2d //cutoff is the half point between peak and zero //2SD
//          var indexFromLocalMax = 1
//          var widthStartIndex = 0
//          var widthEndIndex = 0
//          //keep advancing outwards from index point, until subthreshold is found or pwMax reached
//          while (widthStartIndex * widthEndIndex == 0 && indexFromLocalMax < pwMax) {
//            //if the trace drops below the cutoff before the peak, the widthStartIndex has been found
//            if (trace(index - indexFromLocalMax) < tempCutoff) widthStartIndex = index - indexFromLocalMax
//            //if the trace drops below the cutoff after the peak, the widthEndIndex has been found
//            if (trace(index + indexFromLocalMax) < tempCutoff) widthEndIndex = index + indexFromLocalMax
//            indexFromLocalMax += 1
//          }
//
//          val width =
//            if (widthStartIndex * widthEndIndex == 0) Integer.MAX_VALUE
//            else widthEndIndex - widthStartIndex
//
//          //if the "spike" meets the width conditions, add index to tempRet2
//          if (width <= pwMax && width >= pwMin &&
//              max(trace(index - 2*pwMax to widthStartIndex))  < tempCutoff && //outside of the spikes, the window should be low
//              max(trace(widthEndIndex to index + 2*pwMax)  )  < tempCutoff    //outside of the spikes, the window should be low
//          ) tempRet2 += index
//        }
//
//        //zoom forward by pwMax
//        index += pwMax //pwMin
//        if( index < trace.length ) {
//          continueLoopThreshold = (trace(index) >= threshold)
//          //keep zooming forward until lower than threshold
//          while (continueLoopThreshold && index < trace.length) {
//            //keep advancing until first trace value which is smaller than the threshold again
//            if (trace(index) < threshold) continueLoopThreshold = false
//            index += 1
//          }
//          //we are now under threshold at the index position
//        } // else { the while loop will fall through due to index value
//
//      } else {
//        //if the threshold is not crossed, then continue the loop
//        index += 1
//      }
//
//    }
//
//    tempRet2.toArray
//  }
//
//
//}
//
//// </editor-fold>