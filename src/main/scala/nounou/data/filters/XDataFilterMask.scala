package nounou.data.filters

import nounou.data.XData
import nounou.data.XMask
import breeze.linalg.{DenseVector => DV}

/**
 * @author ktakagaki
 * @date 2/19/14.
 */
class XDataFilterMask(override val upstream: XData, private var initialMask: XMask = new XMask()) extends XDataFilter(upstream) {

  override def toString() = "XDataFilterMask: " + mask.toString()
  private var _mask: XMask = initialMask

  def mask(): XMask = _mask
  def mask_=(mask: XMask): Unit = {
    _mask = mask
    changedData()
  }
  def setMask(mask: XMask) = {
    mask_=(mask)
  }
  def getMask() = mask


  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = {
    if( mask.isMaskedFrame(frame, segment, upstream) ){
      0
    }else{
      upstream.readPointImpl(channel, frame, segment)
    }
  }

  override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): DV[Int] = {
    if( mask.isMaskedFrame(range, segment, upstream) ){
      super.readTraceImpl(channel, range, segment)
    } else {
      upstream.readTraceImpl(channel, range, segment)
    }
  }

  }
