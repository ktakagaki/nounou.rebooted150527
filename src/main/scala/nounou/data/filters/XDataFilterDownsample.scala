package nounou.data.filters

import nounou.data.XData
import breeze.signal.support.FIRKernel1D
import breeze.signal._
import breeze.linalg.{DenseVector => DV, min}

/**
 * @author ktakagaki
 * @date 2/16/14.
 */
class XDataFilterDownsample( private var _parent: XData ) extends XDataFilter( _parent ) {

  def this(_parent: XData, factor: Int) = {
    this(_parent)
    factor_=(factor)
  }

  override def toString() = {
    if(factor == 1) "XDataFilterDownsample: off (factor=1)"
    else "XDataFilterDownsample: factor=" + factor
  }

  // <editor-fold defaultstate="collapsed" desc=" factor-related ">

  def factor(): Int = _factor

  /** Java-style alias for [[factor()]].
    */
  def getFactor(): Int = factor
  def factor_=( factor: Int ) = {
    loggerRequire( factor >= 1, "new factor {} cannot be less than 1!", factor.toString )
    if( factor == this.factor ){
      logger.trace( "factor is already {}, not changing. ", factor.toString )
    } else {
      _factor = factor
      logger.info( "changed factor to {}", factor.toString )
      changedData()
    }
  }
  /** Java-style alias for [[factor_=()]] aka [[[[factor_$eq()]]]].
    */
  def setFactor( factor: Int ): Unit = factor_=( factor )
  protected var  _factor: Int = 0

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" readXXX ">

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int =
      _parent.readPointImpl(channel, frame/factor, segment)

  override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): DV[Int] =
    if(factor == 1){
        _parent.readTraceImpl(channel, range, segment)
    } else {
        _parent.readTraceImpl(channel,
                new Range.Inclusive(range.start*factor,
                                    min(range.end*factor, _parent.segmentLength(segment)-1),
                                    range.step*factor),
                segment)
    }

  // </editor-fold>

  override def sampleRate: Double = _parent.sampleRate / factor

  override def segmentLength: Array[Int] = if( factor == currentSegLenFactor ) currentSegLenBuffer
  else {
    currentSegLenBuffer = ( for(seg <- 0 until segmentCount) yield ( _parent.segmentLength(seg) - 1 )/factor + 1 ).toArray
    currentSegLenFactor = factor
    currentSegLenBuffer
  }
  private var currentSegLenFactor = 0
  private var currentSegLenBuffer = _parent.segmentLength

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

