  package nounou.data.filters

  import nounou._
  import nounou.data.{XData, X}
  import breeze.linalg.{convert, DenseVector}
  import breeze.signal.support.FIRKernel1D
  import breeze.signal._

  /**
   * @author ktakagaki
   * @date 2/1314.
   */
  class XDataFilterDecimate(override val upstream: XData ) extends XDataFilter( upstream ) {

    var kernel: FIRKernel1D[Long] = null
    var factor: Int = 1

    // <editor-fold defaultstate="collapsed" desc=" filter settings ">

    def setDecimateOff(): Unit = if(kernel == null){
      logger.info( "filter is already off, not changing. ")
    } else {
      logger.info( "Turning filter kernel off." )
      factor = 1
      kernel = null
    }

    def setDecimate( factor: Int ): Unit = {
      require( factor < 16,
        logger.error( "Downsample rate {} must be < 16." , factor.toString )
      )

      if(factor == 1) setDecimateOff()
      else {
        kernel = designFilterDecimation[ FIRKernel1D[Long] ](factor, multiplier = 1024L)
        this.factor = factor
        logger.info( "set kernel to {}", kernel )
      }
    }


    // </editor-fold>


    override def readPointImpl(channel: Int, frame: Int, segment: Int): Int =
      if(kernel == null){
        upstream.readPointImpl(channel, frame, segment)
      } else {
        //by calling upstream.readTrace instead of upstream.readTraceImpl, we can deal with cases where the kernel will overhang actual data, since the method will return zeros
        val tempData = upstream.readTrace( channel, (frame * factor - kernel.overhangPre) to (frame * factor + kernel.overhangPost), segment)
        val tempRet = convolve( DenseVector( tempData.map(_.toLong).toArray ), kernel.kernel, overhang = OptOverhang.None )
        require( tempRet.length == 1, "something is wrong with the convolution!" )
        tempRet(0).toInt
      }

    override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): Vector[Int] =
      if(kernel == null){
        upstream.readTraceImpl(channel, range, segment)
      } else {
        //by calling upstream.readTrace instead of upstream.readTraceImpl, we can deal with cases where the kernel will overhang actual data, since the method will return zeros
        val tempData = upstream.readTrace( channel, (range.start * factor - kernel.overhangPre) to (range.last * factor + kernel.overhangPost), segment)
        val tempRes: DenseVector[Long] =
          convolve( convert( DenseVector( tempData.toArray ), Long), kernel.kernel,
                    range = OptRange.rangeToRangeOpt( range.start * factor to range.end * factor ),
                    overhang = OptOverhang.None )
        convert( tempRes :/ kernel.multiplier, Int).toVector
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
      currentSegEndTSBuffer = ( for(seg <- 0 until segmentCount) yield segmentStartTSs(seg) + ((segmentLengths(seg)-1)*tsPerFrame).toLong ).toVector
      currentSegEndTSFactor = factor
      currentSegEndTSBuffer
    }
    private var currentSegEndTSFactor = 1
    private var currentSegEndTSBuffer = upstream.segmentEndTSs

    override def segmentLengths: Vector[Int] = if( factor == currentSegLenFactor ) currentSegLenBuffer
    else {
      currentSegLenBuffer = ( for(seg <- 0 until segmentCount) yield ( segmentLengths(seg) - 1 )/factor + 1 ).toVector
      currentSegLenFactor = factor
      currentSegLenBuffer
    }
    private var currentSegLenFactor = 1
    private var currentSegLenBuffer = upstream.segmentLengths

    //  override def segmentCount: Int = upstream.segmentCount

  }