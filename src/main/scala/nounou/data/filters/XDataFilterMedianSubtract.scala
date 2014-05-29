package nounou.data.filters

import scala.beans.BeanProperty
import nounou.data.XData
import breeze.numerics.isOdd
import breeze.stats.median
import breeze.linalg.{DenseVector => DV}
import breeze.signal.{filterMedian, OptOverhang}
import nounou.ranges.RangeFr

/**
 * @author ktakagaki
 * @date 3/17/14.
 */
class XDataFilterMedianSubtract( override val upstream: XData ) extends XDataFilter( upstream ) {

  private var _windowLength = 1
  private val upstreamBuff: XData = new XDataFilterBuffer(upstream)

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
      val tempData = upstreamBuff.readTrace( channel, RangeFr(frame - windowLengthHalf, frame + windowLengthHalf, 1, segment) )
      median(tempData).toInt
    }

  override def readTraceImpl(channel: Int, ran: Range.Inclusive, segment: Int): DV[Int] =
    if(windowLength == 1){
      upstreamBuff.readTraceImpl(channel, ran, segment)
    } else {
      //by calling upstream.readTrace instead of upstream.readTraceImpl, we can deal with cases where the kernel will overhang actual data, since the method will return zeros
      val tempData = upstreamBuff.readTrace( channel, RangeFr( ran.start - windowLengthHalf, ran.last + windowLengthHalf, 1, segment) )
//      logger.info("windowLength {} tempData.length {} filterMedian length {} ", windowLength.toString, tempData.length.toString, filterMedian(tempData, windowLength, OptOverhang.None).length.toString)
      tempData(windowLengthHalf to -windowLengthHalf-1) - filterMedian(tempData, windowLength, OptOverhang.None)
    }

  //  override def readFrameImpl(frame: Int, segment: Int): Vector[Int] = super[XDataFilter].readFrameImpl(frame, segment)
  //  override def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int): Vector[Int] = super[XDataFilter].readFrameImpl(frame, channels, segment)

  // </editor-fold>

}
