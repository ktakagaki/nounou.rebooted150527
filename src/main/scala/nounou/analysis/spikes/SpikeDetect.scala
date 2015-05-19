package nounou.analysis.spikes

//import breeze.linalg.{argmax, randomInt, DenseVector}
//import nounou.{OptNull, Opt}
import nounou.util.LoggingExt
//import nounou.data.{XTrodeN, XData}
//import nounou.data.filters.{XDataFilterInvert, XDataFilterBuffer, XDataFilterMedianSubtract}
//import nounou.data.ranges.{SampleRangeSpecifier, FrRange$}

import scala.collection.mutable.ArrayBuffer

/**
* @author ktakagaki
*/
object SpikeDetect extends SpikeDetect {

  def it() = this

}


class SpikeDetect extends LoggingExt {

//
//
//  def thresholdPeakDetect(data: XData, trode: XTrodeN, frameRange: RangeFrSpecifier, thresholds: Array[Int]): Array[Frame] =
//    thresholdPeakDetect(data: XData, trode: XTrodeN, frameRange: RangeFrSpecifier, thresholds: Array[Int], OptNull)
//  def thresholdPeakDetect(data: XData, trode: XTrodeN, frameRange: RangeFrSpecifier, thresholds: Array[Int], opts: Opt*): Array[Frame] = {
//
//    //this is where the return values are accumulated
//    var tempRet = new Array[Int](1)
//
//    var optThresholdPeakDetectWindow = 3200000
//
//    // <editor-fold defaultstate="collapsed" desc=" Handle options ">
//
//    for( opt <- opts ) opt match {
//      case OptThresholdPeakDetectWindow( fr ) => optThresholdPeakDetectWindow = fr
//      case _ => {}
//    }
//
//    if(optThresholdPeakDetectWindow < 32000) throw loggerError("optThresholdPeakDetectWindow must be 32000 or larger!")
//
//    // </editor-fold>
//
//    // <editor-fold defaultstate="collapsed" desc=" check input ">
//
//    if( trode.channelCount != thresholds.length )
//      throw loggerError("thresholdSDMultiples must have same length as trode channel count!")
//    if( thresholds.filter( (p: Int) => ( p <= 0 ) ).length != 0 )
//      throw loggerError("thresholds must all be positive!")
//
//    // </editor-fold>
//
//    val validRange = frameRange.getRangeFrValid(data)
//    loggerRequire( validRange.step == 1, "step size for spike detection must be 1! {} is invalid!", validRange.step.toString )
//    var start = validRange.start
//
//    val rangeFrs = new ArrayBuffer[RangeFr]()
//    if( validRange.length > optThresholdPeakDetectWindow ){
//      //var bufferedData: Array[Array[Int]] = null
//      while (start < validRange.last - optThresholdPeakDetectWindow){
//        rangeFrs += RangeFr( start, frameRange.segment, start + optThresholdPeakDetectWindow, OptSegment(frameRange.segment))
//        start += optThresholdPeakDetectWindow
//      }
//      if(start < validRange.last){
//        rangeFrs += RangeFr( start, validRange.last, OptSegment(frameRange.segment))
//      }
//      //ToDo 2: parallelize here
//      tempRet = rangeFrs.map( thresholdPeakDetectImpl(data, trode.channels, _, thresholds, opts:_*) ).flatten.toArray
//    } else {
//      val bufferedData = (for(ch <- trode.channels) yield data.readTraceA(ch, frameRange)).toArray
//      tempRet = thresholdPeakDetectImplImpl(bufferedData, thresholds, opts:_*).map( _ + start)
//    }
//
//    tempRet.toSet.toArray.map( new Frame(_, frameRange.segment) )
//  }
//
//  def thresholdPeakDetectImpl(xData: XData, channels: Vector[Int], rangeFr: RangeFr, thresholds: Array[Int], opts: Opt*): Array[Int] = {
//    val bufferedData = (for(ch <- channels) yield xData.readTraceA(ch, rangeFr)).toArray
//    thresholdPeakDetectImplImpl(bufferedData, thresholds, opts:_*).map( _ + rangeFr.start)
//  }
//
//  def thresholdPeakDetectImplImpl(data: Array[Array[Int]], thresholds: Array[Int]): Array[Int] =
//    thresholdPeakDetectImplImpl(data: Array[Array[Int]], thresholds: Array[Int], OptNull)
//  def thresholdPeakDetectImplImpl(data: Array[Array[Int]], thresholds: Array[Int], opts: Opt*): Array[Int] = {
//
//    //this is where the return values are accumulated
//    val tempRet2 = new ArrayBuffer[Int]()
//
//    var optBlackoutFr = 64
//    var optPeakHalfWidthMaxFr = 16 //1 ms full width at 32kHz
//    var optPeakHalfWidthMinFr = 1  //0.03125ms full width at 32kHz
//    // The analysis window will be [index - 2 * optPeakHalfWidthMaxFr, index + 2 * optPeakHalfWidthMaxFr]
//    // *2 allows for a bit of conservatism to lessen double spike detection
//
//    // <editor-fold defaultstate="collapsed" desc=" Handle options ">
//
//    for( opt <- opts ) opt match {
//      case OptBlackoutFr( fr ) => optBlackoutFr = fr
//      case OptPeakHalfWidthMaxFr( fr ) => optPeakHalfWidthMaxFr = fr
//      case OptPeakHalfWidthMinFr( fr ) => optPeakHalfWidthMinFr = fr
//      case _ => {}
//    }
//
//    if(optBlackoutFr < 0) throw loggerError("optBlackoutFr must be zero or larger!")
//    if(optPeakHalfWidthMaxFr <= 0) throw loggerError("OptPeakHalfWidthMaxFr must be larger than zero!")
//    if(optPeakHalfWidthMinFr <= 0) throw loggerError("OptPeakHalfWidthMinFr must be larger than zero!")
//    if(optPeakHalfWidthMinFr >= optPeakHalfWidthMaxFr) throw loggerError("OptPeakHalfWidthMinFr must be smaller than OptPeakHalfWidthMaxFr!")
//
//    // </editor-fold>
//
//    // <editor-fold defaultstate="collapsed" desc=" check input ">
//
//    if( data.map(_.length).distinct.length != 1 )
//      throw loggerError("the normData input must be a square array of arrays!")
//    if( data.length != thresholds.length )
//      throw loggerError("normData must have same length as thresholdSDMultiples!")
//    if( thresholds.filter( (p: Int) => ( p <= 0 ) ).length != 0 )
//      throw loggerError("thresholdSDMultiples must all be positive!")
//
//    if( data(0).length <= optPeakHalfWidthMaxFr*2 + 1 )
//      throw loggerError("data not long enough for given optPeakHalfWidthMaxFr: " + optPeakHalfWidthMaxFr.toString)
//
//    // </editor-fold>
//
//    // <editor-fold defaultstate="collapsed" desc=" create normData scaled to normScaling by normalizing input data ">
//
//    val normData = new Array[Array[Int]](data.length)
//    val normScaling = 1000000
//    val thresholdsD = thresholds.map( _.toDouble )
//    for(ch <- 0 until data.length){
//      normData(ch) = new Array[Int](data(ch).length)
//      for(index <- 0 until data(ch).length){
//        normData(ch)(index) = ( data(ch)(index).toDouble / thresholdsD(ch) * normScaling.toDouble ).toInt
//      }
//    }
//
//    // </editor-fold>
//
//    // <editor-fold defaultstate="collapsed" desc=" predefined subroutines ">
//
//    def checkThresholdAcrossChannels(index: Int): Boolean = {
//      var ch = 0
//      var ret = false
//      while( !ret && ch < normData.length ) {
//        if ( normData(ch)(index) > normScaling /*thresholds(ch)*/ ) ret = true
//        else ch += 1
//      }
//      ret
//    }
//    def findLocalRelativeMaxArgAcrossChannels(indexStart: Int, indexEnd: Int): (Int, Int) = {
//      var maxValue = Int.MinValue
//      var maxIndex = Int.MinValue
//
//      var ch = 0
//      while( ch < normData.length ) {
//        var index = indexStart
//        while( index <= indexEnd ) {
//          if( normData(ch)(index) >= maxValue ) {
//            maxValue = normData(ch)(index)
//            maxIndex = index
//          }
//          index += 1
//        }
//        ch += 1
//      }
//      (maxIndex, maxValue)
//    }
//    def localMaxIsSpike(ch: Int, index: Int): Boolean = {
//      val localMaxValue = normData(ch)(index)
//      val localMaxValueHalf = localMaxValue /2
//
//      var indexFromLocalMax = 1
//      var widthStartIndex = 0
//      var widthEndIndex = 0
//      //keep advancing outwards from index point, until subthreshold is found or pwMax reached
//      while ( (widthStartIndex==0 || widthEndIndex == 0) && indexFromLocalMax <= optPeakHalfWidthMaxFr) {
//        //if the trace drops below the cutoff before the peak, the widthStartIndex has been found
//        if (widthStartIndex == 0 && normData(ch)(index - indexFromLocalMax) < localMaxValueHalf){
//          widthStartIndex = index - indexFromLocalMax
//        }
//        //if the trace drops below the cutoff after the peak, the widthEndIndex has been found
//        if (widthEndIndex == 0 && normData(ch)(index + indexFromLocalMax) < localMaxValueHalf){
//          widthEndIndex = index + indexFromLocalMax
//        }
//        indexFromLocalMax += 1
//      }
//
//      if( widthStartIndex==0 || widthEndIndex == 0 ) false
//      else if( widthEndIndex - widthStartIndex < optPeakHalfWidthMinFr ) false
//      else if( widthEndIndex - widthStartIndex > optPeakHalfWidthMaxFr ) false
//      else true
//    }
//
//    // </editor-fold>
//
//    val lastCenterP1 = normData(0).length - optPeakHalfWidthMaxFr * 2 // index should always remain < lastCenterP1
//    var index = optPeakHalfWidthMaxFr * 2
//
//    var contInitLoop = true
//    var initPeakPos = -1
//    // <editor-fold defaultstate="collapsed" desc=" fast forward until first half segment is under the threshold ">
//
//    while( contInitLoop && index < lastCenterP1 ){
//      var i = index - 2 * optPeakHalfWidthMaxFr
//      //cycle through first half window to find latest peak position
//      while( i <= index ){
//        if ( checkThresholdAcrossChannels(i) ) initPeakPos  = i
//        i += 1
//      }
//
//      if( initPeakPos == -1 ) {
//        //if there was no threshold crossing, we're good
//        contInitLoop = false
//      } else {
//        //if there was a threshold crossing, fast forward and continue
//        index += initPeakPos + 1
//        initPeakPos = -1
//      }
//    }
//
//    // </editor-fold>
//    //we are now under threshold at the index position
//
//    //main loop
//    while ( index < lastCenterP1 ) {
//
//      if ( checkThresholdAcrossChannels(index) ) {
//        //if the threshold is newly crossed...
//        var localMaxIndex = Int.MinValue
//        var localMaxValue = Int.MinValue
//
//        //...continue advancing the index until the local maximum position, within 2 spike halfwidths
//        var continueLoopLocalMax = true
//        var localMaxFound = false
//        while ( continueLoopLocalMax && index < lastCenterP1 ) {
//          val temp = findLocalRelativeMaxArgAcrossChannels( index - 2 * optPeakHalfWidthMaxFr, index + 2 * optPeakHalfWidthMaxFr )
//          localMaxIndex = temp._1
//          localMaxValue = temp._2
//
//          //local maximum found
//          if (localMaxIndex == index) {
//            continueLoopLocalMax = false
//            localMaxFound = true
//          }
//          //past local max, zoom forward to next subthreshold
//          else if (localMaxIndex < index) {
//            index += 1
//            var continueInnerZoomLoop = true
//            while( continueInnerZoomLoop && index < lastCenterP1 ) {
//              if (checkThresholdAcrossChannels(index)) index += 1
//              else continueInnerZoomLoop = false
//            }
//            continueLoopLocalMax = false
//            localMaxFound = false
//          }
//          //not local maximum yet
//          else {
//            index = localMaxIndex // go forward (ostensibly) to next max
//            continueLoopLocalMax = true
//            localMaxFound = false
//          }
//        }
//        //index is now set to a local maximum value
//
//        //next, see if the local maximum point satisfies the spike width conditions
//        if( localMaxFound ) {
//          var ch = 0
//          var contSpikeDetLoop = true
//          while (contSpikeDetLoop && ch < normData.length) {
//            if (localMaxIsSpike(ch, index)) {
//              tempRet2 += index
//              contSpikeDetLoop = false
//            } else {
//              ch += 1
//            }
//          }
//          //zoom forward by blackout regardless of whether local max met spike criteria or not
//          index += optBlackoutFr
//        }
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

}
