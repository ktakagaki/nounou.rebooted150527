package nounou.data.filters

import nounou._
import nounou.data.{XData, X}
import breeze.signal.support.FIRKernel1D
import breeze.linalg.DenseVector

/**
 * @author ktakagaki
 * @date 2/4/14.
 */
class XDataFilterFIR( upstream: XData ) extends XDataFilter(upstream) {

  var kernel: FIRKernel1D[Int] = null




  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int =
    if(kernel == null){
      upstream.readPointImpl(channel, frame, segment)
    } else {
      val kernelLength = kernel.length
//      upstream.readTraceImpl(channel, )
//      convolve( )
      1
    }

  override def readTraceImpl(channel: Int, range: FrameRange, segment: Int): Vector[Int] =
    if(kernel == null){
      upstream.readTraceImpl(channel, range, segment)
    } else {
      null
    }

  override def readFrameImpl(frame: Int, segment: Int): Vector[Int] = super[XDataFilter].readFrameImpl(frame, segment)
  override def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int): Vector[Int] = super[XDataFilter].readFrameImpl(frame, channels, segment)

//  override def channelNames: scala.Vector[String] = upstream.channelNames

//  override def absUnit: String = upstream.absUnit
//  override def absOffset: Double = upstream.absOffset
//  override def absGain: Double = upstream.absGain

//  override def sampleRate: Double = upstream.sampleRate
//  override def segmentEndTSs: scala.Vector[Long] = upstream.segmentEndTSs
//  override def segmentStartTSs: scala.Vector[Long] = upstream.segmentStartTSs
//  override def segmentLengths: scala.Vector[Int] = upstream.segmentLengths
//  override def segmentCount: Int = upstream.segmentCount

}
