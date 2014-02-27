package nounou.data.filters

import nounou._
import nounou.data.{X, XData}

/** A passthrough object, which is overriden and inherited with various XDataFilterTr traits to create a filter block.
  */
class XDataFilter( val upstream: XData ) extends XData {

  upstream.setChild(this)

  override def channelNames: scala.Vector[String] = upstream.channelNames

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = upstream.readPointImpl(channel, frame, segment)
  override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): Vector[Int] = upstream.readTraceImpl(channel, range, segment)
  override def readFrameImpl(frame: Int, segment: Int): Vector[Int] = upstream.readFrameImpl(frame, segment)
  override def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int): Vector[Int] = upstream.readFrameImpl(frame, channels, segment)

  override def absUnit: String = upstream.absUnit
  override def absOffset: Double = upstream.absOffset
  override def absGain: Double = upstream.absGain

  override def sampleRate: Double = upstream.sampleRate
  override def segmentEndTSs: scala.Vector[Long] = upstream.segmentEndTSs
  override def segmentStartTSs: scala.Vector[Long] = upstream.segmentStartTSs
  override def segmentLengths: scala.Vector[Int] = upstream.segmentLengths
  override def segmentCount: Int = upstream.segmentCount

  override def isCompatible(target: X) = false
  override def :::(target: X): XData = {
    throw new IllegalArgumentException("cannot append an XDataFilter or child!")
  }

}
