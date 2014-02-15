package nounou.data.filters

import nounou.data.XData
import breeze.signal.support.FIRKernel1D
import breeze.signal._
import breeze.linalg.DenseVector

/**
 * @author ktakagaki
 * @date 2/16/14.
 */
class XDataFilterDownsample( override val upstream: XData ) extends XDataFilter( upstream ) {

  def factor: Int = _factor
  def factor_=( factor: Int ) = {
    if( factor == this.factor ){
      logger.info( "factor is already {}}, not changing. ", factor.toString )
    } else {
      _factor = factor
      logger.info( "changed factor to {}", factor.toString )
      changedData()
    }
  protected var  _factor: Int = 1

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int =
    if(factor == 1){
      upstream.readPointImpl(channel, frame, segment)
    } else {
      upstream.readPointImpl(channel, frame*factor, segment)
    }

  override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): Vector[Int] =
    if(factor == 1){
      upstream.readTraceImpl(channel, range, segment)
    } else {
      upstream.readTraceImpl(channel, new Range.Inclusive(range.start * factor, range.end * factor, factor), segment)
    }

  override def readFrameImpl(frame: Int, segment: Int): Vector[Int] = super[XDataFilter].readFrameImpl(frame * factor, segment)
  override def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int): Vector[Int] = super[XDataFilter].readFrameImpl(frame * factor, channels, segment)

  //  override def channelNames: scala.Vector[String] = upstream.channelNames

  //  override def absUnit: String = upstream.absUnit
  //  override def absOffset: Double = upstream.absOffset
  //  override def absGain: Double = upstream.absGain

  override def sampleRate: Double = upstream.sampleRate / factor

  // override def segmentStartTSs: Vector[Long] = upstream.segmentStartTSs
  override def segmentEndTSs: Vector[Long] = if( factor == currentSegEndTSFactor ) currentSegEndTSBuffer
  else {
    currentSegEndTSBuffer = ( for(seg <- 0 until segmentCount) yield upstream.segmentStartTSs(seg) + ((upstream.segmentLengths(seg)-1)*tsPerFrame).toLong ).toVector
    currentSegEndTSFactor = factor
    currentSegEndTSBuffer
  }
  private var currentSegEndTSFactor = 1
  private var currentSegEndTSBuffer = upstream.segmentEndTSs

  override def segmentLengths: Vector[Int] = if( factor == currentSegLenFactor ) currentSegLenBuffer
  else {
    currentSegLenBuffer = ( for(seg <- 0 until segmentCount) yield ( upstream.segmentLengths(seg) - 1 )/factor + 1 ).toVector
    currentSegLenFactor = factor
    currentSegLenBuffer
  }
  private var currentSegLenFactor = 1
  private var currentSegLenBuffer = upstream.segmentLengths

  //  override def segmentCount: Int = upstream.segmentCount

}