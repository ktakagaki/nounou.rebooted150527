package nounou.elements.data.filters

import nounou.elements.data.NNData

import breeze.numerics.isOdd
import breeze.stats.median
import breeze.linalg.{DenseVector => DV}
import breeze.signal.{filterMedian, OptOverhang}
import nounou.elements.ranges.{SampleRangeValid, SampleRange}

/**This filter applies a median subtraction, which is a non-linear form of high-pass which is
  * less biased by extreme transients like spiking.
 * @author ktakagaki
 * //@date 3/17/14.
 */
class NNDataFilterMedianSubtract( private var parenVar: NNData ) extends NNDataFilter( parenVar ) {

  private var _windowLength = 1
  private val upstreamBuff: NNData = new NNDataFilterBuffer(parenVar)

  var windowLengthHalf = 0
  def setWindowLength( value: Int ): Unit = {
    loggerRequire( value > 0, "Parameter windowLength must be bigger than 0, invalid value: {}", value.toString)
    loggerRequire( isOdd(value), "Parameter windowLength must be odd to account correctly for overhangs, invalid value: {}", value.toString)
    _windowLength = value
    windowLengthHalf = (_windowLength-1)/2
  }
  def getWindowLength(): Int = _windowLength
  def windowLength_=( value: Int ) = setWindowLength( value )
  def windowLength() = _windowLength


  // <editor-fold defaultstate="collapsed" desc=" calculate data ">

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int =
    if(windowLength == 1){
      upstreamBuff.readPointImpl(channel, frame, segment)
    } else {
      //by calling upstream.readTrace instead of upstream.readTraceImpl, we can deal with cases where the kernel will overhang actual data, since the method will return zeros
      val tempData = upstreamBuff.readTraceDV(
                          channel,
                          new SampleRange(frame - windowLengthHalf, frame + windowLengthHalf, 1, segment) )
      median(tempData).toInt
    }

  override def readTraceDVImpl(channel: Int, ran: SampleRangeValid): DV[Int] =
    if(windowLength == 1){
      upstreamBuff.readTraceDVImpl(channel, ran)
    } else {
      //by calling upstream.readTrace instead of upstream.readTraceImpl, we can deal with cases where the kernel will overhang actual data, since the method will return zeros
      val tempData = upstreamBuff.readTraceDV(
        channel,
        new SampleRange( ran.start - windowLengthHalf, ran.last + windowLengthHalf, 1, ran.segment) )
      tempData(windowLengthHalf to -windowLengthHalf-1) - filterMedian(tempData, windowLength, OptOverhang.None)
    }

  //  override def readFrameImpl(frame: Int, segment: Int): Vector[Int] = super[XDataFilter].readFrameImpl(frame, segment)
  //  override def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int): Vector[Int] = super[XDataFilter].readFrameImpl(frame, channels, segment)

  // </editor-fold>

}
