package nounou.data.filters

import nounou._
import nounou.data.ranges.RangeFr
import nounou.data.{XDataNull, XLayout, X, XData}
import breeze.linalg.{DenseVector => DV}

/** A passthrough object, which is overriden and inherited with various XDataFilterTr traits to create a filter block.
  */
class XDataFilter( val upstream: XData ) extends XData {

  upstream.setChild(this)

  // <editor-fold defaultstate="collapsed" desc=" setActive/getActive ">

  private var _active = true
  final def setActive(active: Boolean) = if(_active != active){
    _active = active
    changedData()
  }
  final def getActive = _active

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" adjust reading functions for active state ">

  override final def readPoint(channel: Int, frame: Int, segment: Int): Int = if(_active){
    super.readPoint(channel, frame, segment)
  }else{
    upstream.readPoint(channel, frame, segment)
  }

  override final def readTrace(channel: Int, range: RangeFr): DV[Int] = if(_active){
    super.readTrace(channel, range)
  }else{
    upstream.readTrace(channel, range)
  }
    // </editor-fold>


//  override def channelNames: scala.Vector[String] = upstream.channelNames
  override def channelCount = upstream.channelCount

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = upstream.readPointImpl(channel, frame, segment)
  override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): DV[Int] = upstream.readTraceImpl(channel, range, segment)
  override def readFrameImpl(frame: Int, segment: Int): DV[Int] = upstream.readFrameImpl(frame, segment)
  override def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int): DV[Int] = upstream.readFrameImpl(frame, channels, segment)

  override def absUnit: String = upstream.absUnit
  override def absOffset: Double = upstream.absOffset
  override def absGain: Double = upstream.absGain
  override def scaleMax = upstream.scaleMax
  override def scaleMin = upstream.scaleMin

  override def sampleRate: Double = upstream.sampleRate
  override def segmentEndTs: scala.Vector[Long] = upstream.segmentEndTs
  override def segmentStartTs: scala.Vector[Long] = upstream.segmentStartTs
  override def segmentLength: scala.Vector[Int] = upstream.segmentLength
  override def segmentCount: Int = upstream.segmentCount

  override def layout: XLayout = upstream.layout()

  override def isCompatible(target: X) = false
  override def :::(target: X): XData = {
    throw new IllegalArgumentException("cannot append an XDataFilter or child!")
  }

}


object XDataFilterNull extends XDataFilter( XDataNull )