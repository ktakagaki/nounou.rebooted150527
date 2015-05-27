package nounou.elements.data.filters

import _root_.nounou.elements.ranges.SampleRangeValid
import nounou.elements.data.NNData
import breeze.signal.support.FIRKernel1D
import breeze.signal._
import breeze.linalg.{DenseVector => DV, min}
import nounou.elements.traits.NNDataTiming

/**
 * @author ktakagaki
 * //@date 2/16/14.
 */
class NNDataFilterDownsample( private val parentVal: NNData, protected var factorVar: Int )
  extends NNDataFilter( parentVal ) {

  setFactor(factorVar)

  def this(parentVal: NNData) = this(parentVal, 10)

  // <editor-fold defaultstate="collapsed" desc=" toString/toStringFull ">

  override def toString() = {
    if(factor == 1) "XDataFilterDownsample: off (factor=1)"
    else "XDataFilterDownsample: factor=" + factor
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" factor-related ">

  protected var timingBuffer: NNDataTiming = parentVal.timing()

  final def factor(): Int = getFactor()
  /** Java-style alias for [[factor]].
    */
  def getFactor(): Int = factorVar
  def setFactor( factor: Int ) = {
    loggerRequire( factor >= 1, "new factor {} cannot be less than 1!", factor.toString )
    if( factor == this.factor ){
      logger.trace( "factor is already {}, not changing. ", factor.toString )
    } else {
      factorVar = factor
      timingBuffer = new NNDataTiming(
        parentVal.timing.sampleRate / factor,
        (for(seg <- 0 until parentVal.timing.segmentCount)
        yield ( (parentVal.timing.segmentLength(seg) - 1).toDouble/factor).round.toInt + 1 ).toArray,
        parentVal.timing.segmentStartTss,
        parentVal.timing.timestampOffset
        )
      //logger.info( "changed factor to {}", factor.toString )
      changedData()
    }
  }

  override def getTiming(): NNDataTiming = timingBuffer

//  //override def sampleRate: Double = _parent.sampleRate / factor
//  override def segmentLengthImpl(segment: Int): Int =
//    if( factor == currentSegLenFactor ) currentSegLenBuffer(segment)
//    else {
//      currentSegLenBuffer =
//        (for(seg <- 0 until segmentCount)
//        yield ( (_parent.segmentLength(seg) - 1).toDouble/factor).round.toInt + 1 ).toArray
//      currentSegLenFactor = factor
//      currentSegLenBuffer(segment)
//    }
//  private var currentSegLenFactor = 0
//  private var currentSegLenBuffer = Array[Int]()

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" readXXX ">

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int =
    parentVal.readPointImpl(channel, frame*factor, segment)

  override def readTraceDVImpl(channel: Int, range: SampleRangeValid): DV[Int] =
    if(factor == 1){
      parentVal.readTraceDVImpl(channel, range)
    } else {
      parentVal.readTraceDVImpl(channel,
                new SampleRangeValid(
                          range.start*factor,
                          min(range.last*factor, parentVal.timing.segmentLength(range.segment)-1),
                          range.step*factor,
                          range.segment)
        )
    }

  // </editor-fold>

  

}





//  override def segmentCount: Int = _parent.segmentCount

//  override def readFrameImpl(frame: Int/*, segment: Int*/): DV[Int] = super[XDataFilter].readFrameImpl(frame * factor)//, segment)
//  override def readFrameImpl(frame: Int, channels: Array[Int]/*, segment: Int*/): DV[Int] = super[XDataFilter].readFrameImpl(frame * factor, channels/*, segment*/)

//  override def channelNames: scala.Vector[String] = _parent.channelNames

//  override def absUnit: String = _parent.absUnit
//  override def absOffset: Double = _parent.absOffset
//  override def absGain: Double = _parent.absGain


// override def segmentStartTSs: Vector[Long] = _parent.segmentStartTSs
//  override def segmentEndTs: Array[Long] = if( factor == currentSegEndTSFactor ) currentSegEndTSBuffer
//  else {
//    currentSegEndTSBuffer = ( for(seg <- 0 until segmentCount) yield _parent.segmentStartTs(seg) + ((this.segmentLength(seg)-1)*timestampsPerFrame).toLong ).toArray
//    currentSegEndTSFactor = factor
//    currentSegEndTSBuffer
//  }
//  private var currentSegEndTSFactor = 0
//  private var currentSegEndTSBuffer = _parent.segmentEndTs

