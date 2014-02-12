package nounou.data.filters

import nounou._
import nounou.data.{XData, X}
import breeze.linalg.DenseVector
import breeze.signal.support.FIRKernel1D
import breeze.signal._

/**
 * @author ktakagaki
 * @date 2/4/14.
 */
class XDataFilterFIR( upstream: XData ) extends XDataFilter(upstream) {

  var kernel: FIRKernel1D[Long] = null

  def setFilter( omega0: Double, omega1: Double ): Unit = {
    require(omega0>= 0 && omega1 > omega0 && omega1 <= 1,
      logger.error("setFilter: Frequencies must be 0 <= omega0 < omega1 <= 1. omega0={}, omega1={}. Use setFilterHz if setting in Hz.", omega0.toString, omega1.toString) )
    if(omega0 == 0d && omega1 == 1d) kernel = null
    else kernel = designFilterFirwin[Long](sampleRate.toInt*2, DenseVector[Double](omega0, omega1), nyquist = 1d,
                                          zeroPass = false, scale=true, multiplier = 1024d)
  }

  def setFilterHz( f0: Double, f1: Double ): Unit = {
    require(f0 >= 0 && f1 > f0 && f1 <= sampleRate/2, logger.error("setFilterHz: Frequencies must be 0 <= f0 < f1 <= sampleRate/2. f0={}, f1={}", f0.toString, f1.toString) )
    setFilter(f0/(sampleRate/2d), f1/(sampleRate/2d))
  }




  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int =
    if(kernel == null){
      upstream.readPointImpl(channel, frame, segment)
    } else {
      //by calling upstream.readTrace instead of upstream.readTraceImpl, we can deal with cases where the kernel will overhang actual data, since the method will return zeros
      val tempData = upstream.readTrace( channel, (frame - kernel.overhangPre) to (frame + kernel.overhangPost), segment)
      val tempRet = convolve( DenseVector( tempData.map(_.toLong).toArray ), kernel.kernel, overhang = OptOverhang.None )
      require( tempRet.length == 1, "something is wrong with the convolution!" )
      tempRet(0).toInt
    }

  override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): Vector[Int] =
    if(kernel == null){
      upstream.readTraceImpl(channel, range, segment)
    } else {
      //by calling upstream.readTrace instead of upstream.readTraceImpl, we can deal with cases where the kernel will overhang actual data, since the method will return zeros
      val tempData = upstream.readTrace( channel, (range.start - kernel.overhangPre) to (range.last + kernel.overhangPost), segment)
      convolve( DenseVector( tempData.map(_.toLong).toArray ), kernel.kernel, overhang = OptOverhang.None ).toArray.toVector.map(_.toInt)
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
