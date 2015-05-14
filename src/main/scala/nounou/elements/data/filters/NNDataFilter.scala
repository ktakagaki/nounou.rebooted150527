package nounou.elements.data.filters

import nounou._
import nounou.elements.NNElement
import nounou.elements.data.NNData
import nounou.elements.ranges.{SampleRangeSpecifier, SampleRangeValid}
import nounou.elements.layouts.NNDataLayout
import breeze.linalg.{DenseVector => DV}
import nounou.elements.traits.{NNDataScale, NNDataTiming}

/** A passthrough object, which is overriden and inherited with various XDataFilterTr traits to create a filter block.
  */
class NNDataFilter( /**Explanation*/
                    private var parenVar: NNData ) extends NNData {

  setParent(parenVar)

  // <editor-fold defaultstate="collapsed" desc=" set/getParent ">

  def setParent(parent: NNData): Unit = {
    parenVar.clearChild(this)
    parenVar = parent
    parenVar.setChild(this)
    changedData()
    changedTiming()
    changedLayout()
  }

  def getParent(): NNData = parenVar

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

  override final def readPoint(channel: Int, frame: Int, segment: Int): Int = if(_active){
    super.readPoint(channel, frame, segment)
  }else{
    parenVar.readPoint(channel, frame, segment)
  }

  override final def readTraceDV(channel: Int, range: SampleRangeSpecifier): DV[Int] =
    if(_active){
      super.readTraceDV(channel, range)
    }else{
      parenVar.readTraceDV(channel, range)
    }
    // </editor-fold>

//  override def channelNames: scala.Vector[String] = _parent.channelNames
  override def getChannelCount = parenVar.channelCount

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = parenVar.readPointImpl(channel, frame, segment: Int)
  override def readTraceDVImpl(channel: Int, range: SampleRangeValid): DV[Int] = parenVar.readTraceDVImpl(channel, range)
//  override def readFrameImpl(frame: Int): DV[Int] = _parent.readFrameImpl(frame)
//  override def readFrameImpl(frame: Int, channels: Array[Int]): DV[Int] = _parent.readFrameImpl(frame, channels)

  override def getTiming() = parenVar.timing()
  override def getScale() = parenVar.scale()
  override def setTiming( timing: NNDataTiming ) =
    throw loggerError(s"Cannot set timing for data filter ${this.getClass.getCanonicalName}.")
  override def setScale( scale: NNDataScale ) =
    throw loggerError(s"Cannot set scale for data filter ${this.getClass.getCanonicalName}.")
//  override def absUnit: String = _parent.absUnit
//  override def absOffset: Double = _parent.absOffset
//  override def absGain: Double = _parent.absGain
//  override def scaleMax = _parent.scaleMax
//  override def scaleMin = _parent.scaleMin

//  override def sampleRate: Double = _parent.sampleRate
//  override def segmentEndTs: Array[Long] = _parent.segmentEndTs
//  override def segmentStartTs: Array[Long] = _parent.segmentStartTs
//  override def segmentLengthImpl(segment: Int): Int = _parent.segmentLengthImpl( segment )
//  override def segmentCount: Int = _parent.segmentCount

  /*override def layout: NNLayout = _parent.layout()*/

  override def isCompatible(target: NNElement) = false

}