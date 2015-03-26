package nounou.elements.data.filters

import _root_.nounou.elements.ranges.SampleRangeValid
import nounou.elements.data.NNData
import breeze.signal.support.FIRKernel1D
import breeze.signal._
import breeze.linalg.{DenseVector => DV, min}
import nounou.elements.traits.NNDataTiming

/**
 * @author ktakagaki
 * @date 2/16/14.
 */
class NNDataFilterDownsample( private var _parent: NNData ) extends NNDataFilter( _parent ) {

  def this(_parent: NNData, factor: Int) = {
    this(_parent)
    setFactor(factor)
  }

  override def toString() = {
    if(factor == 1) "XDataFilterDownsample: off (factor=1)"
    else "XDataFilterDownsample: factor=" + factor
  }

  // <editor-fold defaultstate="collapsed" desc=" factor-related ">

  final def factor(): Int = getFactor()

  /** Java-style alias for [[factor()]].
    */
  def getFactor(): Int = _factor
  def setFactor( factor: Int ) = {
    loggerRequire( factor >= 1, "new factor {} cannot be less than 1!", factor.toString )
    if( factor == this.factor ){
      logger.trace( "factor is already {}, not changing. ", factor.toString )
    } else {
      _factor = factor
      timingBuffer = NNDataTiming.apply(
        _parent.timing.sampleRate / factor,
        (for(seg <- 0 until _parent.timing.segmentCount)
        yield ( (_parent.timing.segmentLength(seg) - 1).toDouble/factor).round.toInt + 1 ).toArray,
        _parent.timing.segmentStartTss
        )
      //logger.info( "changed factor to {}", factor.toString )
      changedData()
    }
  }
  protected var _factor: Int = 1
  protected var timingBuffer: NNDataTiming = _parent.timing()

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
      _parent.readPointImpl(channel, frame*factor, segment)

  override def readTraceDVImpl(channel: Int, range: SampleRangeValid): DV[Int] =
    if(factor == 1){
        _parent.readTraceDVImpl(channel, range)
    } else {
        _parent.readTraceDVImpl(channel,
                new SampleRangeValid(
                          range.start*factor,
                          min(range.last*factor, _parent.timing.segmentLength(range.segment)-1),
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

