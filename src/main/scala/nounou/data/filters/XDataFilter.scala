package nounou.data.filters

import nounou._
import nounou.data.ranges.RangeFr
import nounou.data.{XDataNull, XLayout, X, XData}
import breeze.linalg.{DenseVector => DV}

/** A passthrough object, which is overriden and inherited with various XDataFilterTr traits to create a filter block.
  */
class XDataFilter( private var _parent: XData ) extends XData {

  setParent(_parent)

  // <editor-fold defaultstate="collapsed" desc=" set/getParent ">

  def setParent(parent: XData): Unit = {
    _parent.clearChild(this)
    _parent = parent
    _parent.setChild(this)
    changedData()
    changedTiming()
    changedLayout()
  }

  def getParent(): XData = _parent

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" setActive/getActive ">

  private var _active = true
  final def setActive(active: Boolean) = if(_active != active){
    _active = active
    changedData()
  }
  def getActive() = _active

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" adjust reading functions for active state ">

  override final def readPoint(channel: Int, frame: Int/*, segment: Int*/): Int = if(_active){
    super.readPoint(channel, frame)//, segment)
  }else{
    _parent.readPoint(channel, frame)//, segment)
  }

  override final def readTrace(channel: Int, range: RangeFr): DV[Int] = if(_active){
    super.readTrace(channel, range)
  }else{
    _parent.readTrace(channel, range)
  }
    // </editor-fold>


//  override def channelNames: scala.Vector[String] = _parent.channelNames
  override def channelCount = _parent.channelCount

  override def readPointImpl(channel: Int, frame: Int): Int = _parent.readPointImpl(channel, frame)
  override def readTraceImpl(channel: Int, range: Range.Inclusive): DV[Int] = _parent.readTraceImpl(channel, range)
  override def readFrameImpl(frame: Int): DV[Int] = _parent.readFrameImpl(frame)
  override def readFrameImpl(frame: Int, channels: Array[Int]): DV[Int] = _parent.readFrameImpl(frame, channels)

  override def absUnit: String = _parent.absUnit
  override def absOffset: Double = _parent.absOffset
  override def absGain: Double = _parent.absGain
  override def scaleMax = _parent.scaleMax
  override def scaleMin = _parent.scaleMin

  override def sampleRate: Double = _parent.sampleRate
  override def segmentEndTs: Array[Long] = _parent.segmentEndTs
  override def segmentStartTs: Array[Long] = _parent.segmentStartTs
  override def segmentLength: Array[Int] = _parent.segmentLength
  override def segmentCount: Int = _parent.segmentCount

  override def layout: XLayout = _parent.layout()

  override def isCompatible(target: X) = false
  override def :::(target: X): XData = {
    throw loggerError("cannot append a XDataFilter object!")
  }

}