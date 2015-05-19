package nounou.elements.data.filters

import _root_.nounou.elements.ranges.SampleRangeValid
import nounou.NN._
import nounou.elements.data.NNData
import breeze.linalg.{DenseVector => DV, max, convert}
import breeze.signal.support.FIRKernel1D
import breeze.signal._
import scala.beans.BeanProperty
//import nounou.data.ranges.FrRange$

/**
 * @author ktakagaki
 * //@date 2/4/14.
 */
class NNDataFilterFIR(private var _parent: NNData ) extends NNDataFilter( _parent ) {

  var kernel: FIRKernel1D[Long] = null
  override def getActive(): Boolean = { super.getActive() && kernel != null }

  var kernelOmega0: Double = 0d
  var kernelOmega1: Double = 1d
  var multiplier = 256L

  override def toString() = {
    if(kernel == null) "XDataFilterFIR: kernel null (off)"
    else "XDataFilterFIR:\n" +
         "   kernel " + kernel.toString()
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

  def setFilter( omega0: Double, omega1: Double): Unit = {
    loggerRequire(omega0>= 0 && omega1 > omega0 && omega1 <= 1,
      "Frequencies must be 0 <= omega0 < omega1 <= 1. omega0={}, omega1={}. Use setFilterHz if setting in Hz.",
      omega0.toString, omega1.toString)

    //ToDo 3: improve this error message, make warning?
    loggerRequire(omega1*taps > 1d,
      "The number of taps {} is probably insufficient for the omega1 value {}",
      taps.toString, omega1.toString)

    if(omega0 == 0d && omega1 == 1d)
      setFilterOff()
    else {
      kernel = designFilterFirwin[Long](taps, DV[Double](omega0, omega1), nyquist = 1d,
        zeroPass = false, scale=true, multiplier = this.multiplier)
      kernelOmega0 = omega0
      kernelOmega1 = omega1

      logger.info( "set kernel to {}", kernel )
      changedData()
    }
  }

  def setFilterHz( f0: Double, f1: Double): Unit = {
    val sampleRate = timing.sampleRate
    require(f0 >= 0 && f1 > f0 && f1 <= sampleRate/2, logger.error("setFilterHz: Frequencies must be 0 <= f0 < f1 <= sampleRate/2. f0={}, f1={}", f0.toString, f1.toString) )
    setFilter(f0/(sampleRate/2d), f1/(sampleRate/2d))
  }

  def getFilterHz(): Array[Double] = {
    val sampleRate = timing.sampleRate
    Array[Double]( kernelOmega0*(sampleRate/2d), kernelOmega1*(sampleRate/2d) )
  }

  @BeanProperty
  var taps = 4096
  // </editor-fold>


  // <editor-fold defaultstate="collapsed" desc=" calculate data ">

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = {
    //by calling _parent.readTrace instead of _parent.readTraceImpl, we can deal with cases where the kernel will overhang actual data, since the method will return zeros
    val tempData = _parent.readTraceDV( channel,
        SampleRange(frame - kernel.overhangPre, frame + kernel.overhangPost, 1, segment))
    val tempRet = convolve( DV( tempData.map(_.toLong).toArray ), kernel.kernel, overhang = OptOverhang.None )
    require( tempRet.length == 1, "something is wrong with the convolution!" )
    tempRet(0).toInt
  }

  override def readTraceDVImpl(channel: Int, range: SampleRangeValid): DV[Int] = {
    //by calling _parent.readTrace instead of _parent.readTraceImpl, we can deal with cases where the kernel will overhang actual data, since the method will return zeros
    val tempData = _parent.readTraceDV( channel,
      SampleRangeReal( range.start - kernel.overhangPre, range.last + kernel.overhangPost, 1, range.segment))
//    println("XDataFilterFIR " + ran.toString())
    val tempRes: DV[Long] = convolve(
         convert( new DV( tempData.toArray ), Long),
         kernel.kernel,
         range = OptRange.RangeOpt(new Range.Inclusive(0, range.last - range.start, range.step)),
        overhang = OptOverhang.None ) / multiplier
    convert(tempRes, Int)
  }

//  override def readFrameImpl(frame: Int, segment: Int): Vector[Int] = super[XDataFilter].readFrameImpl(frame, segment)
//  override def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int): Vector[Int] = super[XDataFilter].readFrameImpl(frame, channels, segment)

  // </editor-fold>


}