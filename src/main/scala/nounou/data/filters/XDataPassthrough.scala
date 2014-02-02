package nounou.data.filters

import nounou.data.{X, Span, XData}
import scala.collection.immutable.Vector

/** A passthrough object, which is inherited with various XDataFilter traits to create a filter block.
  */
class XDataPassThrough( var upstream: XData ) extends XData {

  override def channelNames: scala.Vector[String] = upstream.channelNames

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = upstream.readPointImpl(channel, frame, segment)
  override def readTraceImpl(channel: Int, span:Span, segment: Int): Vector[Int] = upstream.readTraceImpl(channel, span, segment)
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
    throw new IllegalArgumentException("cannot append an XDataPassThrough!")
  }

}
