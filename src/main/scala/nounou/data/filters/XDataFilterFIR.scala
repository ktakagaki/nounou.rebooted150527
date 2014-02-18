package nounou.data.filters

import nounou._
import nounou.data.{XData, X}
import breeze.linalg.{DenseVector, convert}
import breeze.signal.support.FIRKernel1D
import breeze.signal._

/**
 * @author ktakagaki
 * @date 2/4/14.
 */
class XDataFilterFIR( override val upstream: XData ) extends XDataFilter( upstream ) {

  var kernel: FIRKernel1D[Long] = null
  var kernelOmega0: Double = 0d
  var kernelOmega1: Double = 1d
  var multiplier = 256L

  override def toString() = {
    if(kernel == null) "XDataFilterFIR: kernel null (off)"
    else "XDataFilterFIR: kernel " + kernel.toString() + ", multiplier: " + multiplier.toString
  }

  // <editor-fold defaultstate="collapsed" desc=" get/set filter settings ">

  def setFilterOff(): Unit = if(kernel == null){
    logger.trace( "filter is already off, not changing. ")
  } else {
    logger.info( "Turning filter kernel off." )
    kernel = null
    kernelOmega0 = 0
    kernelOmega1 = 1
    changedData()
  }

  def setFilter( omega0: Double, omega1: Double ): Unit = {
    require(omega0>= 0 && omega1 > omega0 && omega1 <= 1,
      logger.error(
        "Frequencies must be 0 <= omega0 < omega1 <= 1. omega0={}, omega1={}. Use setFilterHz if setting in Hz.",
        omega0.toString, omega1.toString)
    )

    if(omega0 == 0d && omega1 == 1d)
      setFilterOff()
    else {
      kernel = designFilterFirwin[Long](1024, DenseVector[Double](omega0, omega1), nyquist = 1d,
        zeroPass = false, scale=true, multiplier = this.multiplier)
      kernelOmega0 = omega0
      kernelOmega1 = omega1

      logger.info( "set kernel to {}", kernel )
      changedData()
    }
  }

  def setFilterHz( f0: Double, f1: Double ): Unit = {
    require(f0 >= 0 && f1 > f0 && f1 <= sampleRate/2, logger.error("setFilterHz: Frequencies must be 0 <= f0 < f1 <= sampleRate/2. f0={}, f1={}", f0.toString, f1.toString) )
    setFilter(f0/(sampleRate/2d), f1/(sampleRate/2d))
  }

  def getFilterHz(): Vector[Double] = Vector[Double]( kernelOmega0*(sampleRate/2d), kernelOmega1*(sampleRate/2d) )

  // </editor-fold>


  // <editor-fold defaultstate="collapsed" desc=" calculate data ">

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

  override def readTraceImpl(channel: Int, ran: Range.Inclusive, segment: Int): Vector[Int] =
    if(kernel == null){
        upstream.readTraceImpl(channel, ran, segment)
    } else {
        //by calling upstream.readTrace instead of upstream.readTraceImpl, we can deal with cases where the kernel will overhang actual data, since the method will return zeros
        val tempData = upstream.readTrace( channel, new FrameRange( ran.start - kernel.overhangPre, ran.last + kernel.overhangPost + 1, 1), segment)
//      println( "1: " + tempData.length )
//      println(OptRange.rangeToRangeOpt(ran))
        val tempRes: DenseVector[Long] = convolve(
                                              convert( new DenseVector( tempData.toArray ), Long), kernel.kernel,
                                              range = OptRange.RangeOpt(new Range.Inclusive(0, ran.last - ran.start, ran.step)),
                                              overhang = OptOverhang.None ) / multiplier
//      println( "2: " +  tempRes.length )
        toInt( tempRes.toArray.toVector )
    }

//  override def readFrameImpl(frame: Int, segment: Int): Vector[Int] = super[XDataFilter].readFrameImpl(frame, segment)
//  override def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int): Vector[Int] = super[XDataFilter].readFrameImpl(frame, channels, segment)

  // </editor-fold>

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