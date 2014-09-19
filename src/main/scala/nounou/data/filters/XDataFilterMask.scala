package nounou.data.filters

import nounou.data.XData
import nounou.data.XMask
import breeze.linalg.{DenseVector => DV}

/**
 * @author ktakagaki
 * @date 2/19/14.
 */
class XDataFilterMask(private var _parent: XData, private var initialMask: XMask = new XMask()) extends XDataFilter(_parent) {

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
    if( mask.isMaskedFrame(frame, segment, _parent) ){
      0
    }else{
      _parent.readPointImpl(channel, frame, segment)
    }
  }

  override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): DV[Int] = {
    if( mask.isMaskedFrame(range, segment, _parent) ){
      //ToDo 2: make zero array
      super.readTraceImpl(channel, range, segment)
    } else {
      _parent.readTraceImpl(channel, range, segment)
    }
  }

  }
