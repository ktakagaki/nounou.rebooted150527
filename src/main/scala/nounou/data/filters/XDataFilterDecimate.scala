  package nounou.data.filters

  import nounou.NN._
  import nounou.data.{XData, X}
  import breeze.linalg.{DenseVector => DV, convert}
  import breeze.signal._
  import scala.beans.BeanProperty
  import nounou.data.ranges.{SampleRangeReal, SampleRangeValid}
  import breeze.signal.support.FIRKernel1D

  /**
   * @author ktakagaki
   * @date 2/1314.
   */
  class XDataFilterDecimate( private var _parent: XData ) extends XDataFilterDownsample( _parent ) {

    override def toString() = {
      if(factor == 1) "XDataFilterDecimate: off (factor=1)"
      else "XDataFilterDecimate: factor=" + factor
    }

    var kernel: FIRKernel1D[Long] = null

    override def factor: Int = _factor
    _factor = 1

    override def factor_= ( factor: Int ): Unit = {
      require( factor <= 16,
        logger.error( "Downsample rate {} must be <= 16." , factor.toString )
      )

      if( factor == this.factor ){
        logger.trace( "factor is already {}, not changing. ", factor.toString )
      } else if(factor == 1) setDecimateOff()
      else {
        kernel = designFilterDecimation[ FIRKernel1D[Long] ](factor, multiplier = 1024L)
        this._factor = factor
        changedData()
        logger.info( "set kernel to {}", kernel )
      }
    }

    def setDecimateOff(): Unit = if(kernel == null){
      logger.info( "filter is already off, not changing. ")
    } else {
      logger.info( "Turning filter kernel off." )
      _factor = 1
      kernel = null
    }




    override def readPointImpl(channel: Int, frame: Int, segment: Int): Int =
      if(kernel == null){
        _parent.readPointImpl(channel, frame, segment)
      } else {
        //by calling _parent.readTrace instead of _parent.readTraceImpl, we can deal with cases where the kernel will overhang actual data, since the method will return zeros
        val tempData = _parent.readTrace( channel, SampleRange(frame * factor - kernel.overhangPre, frame * factor + kernel.overhangPost, 1))//, OptSegment(segment) ))
        val tempRet = convolve( DV( tempData.map(_.toLong).toArray ), kernel.kernel, overhang = OptOverhang.None )
        require( tempRet.length == 1, "something is wrong with the convolution!" )
        tempRet(0).toInt
      }

    override def readTraceImpl(channel: Int, range: SampleRangeValid/*Range.Inclusive, segment: Int*/): DV[Int] =
      if(kernel == null){
          _parent.readTraceImpl(channel, range/*, segment*/)
      } else {
          //by calling _parent.readTrace instead of _parent.readTraceImpl, we can deal with cases where the kernel will overhang actual data, since the method will return zeros
          val realRange = new SampleRangeReal(range.start * factor - kernel.overhangPre, range.last * factor + kernel.overhangPost, 1, range.segment )
          val tempData = _parent.readTrace(channel, realRange)
            //RangeFr(range.start * factor - kernel.overhangPre, range.last * factor + kernel.overhangPost, 1, OptSegment(segment) ))
//        println("tempData: " + tempData.length)
//        println("kernel: " + kernel.kernel.length)
//        println("start: " + range.start + " end: " + range.end+ " stepMs: " + range.stepMs+ " inclusive: " + range.isInclusive)
          val tempRes: DV[Long] =
            convolve( convert( tempData, Long ), kernel.kernel,
              range = OptRange.RangeOpt( new Range.Inclusive(0, (range.last - range.start)*factor, range.step*factor) ),
              overhang = OptOverhang.None )
//        println("tempRes: " + tempRes.length)
          convert( (tempRes / kernel.multiplier.toLong ), Int)
      }

//    override def readFrameImpl(frame: Int, segment: Int): Vector[Int] = super[XDataFilter].readFrameImpl(frame * factor, segment)
//    override def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int): Vector[Int] = super[XDataFilter].readFrameImpl(frame * factor, channels, segment)



  }