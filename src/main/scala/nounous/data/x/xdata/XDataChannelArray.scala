package nounous.data.xdata

import nounous.data.{Span}
import nounous.data.traits.XConcatenatable
import nounous.data.x.X

/**
 * Created by Kenta on 12/15/13.
 */
 abstract class XDataChannelArray extends XDataImmutable with XConcatenatable{

  /** MUST OVERRIDE:
    *
    */
  val array: Vector[XDataChannel]

  override lazy val segments = array(0).segments
  override lazy val length = array(0).length
  override lazy val startTimestamp = array(0).startTimestamp
  override lazy val sampleRate = array(0).sampleRate

  // (from XChannelsImmutable)
  override lazy val channelName = array.map(_.channelName)

  // (from XAbsoluteImmutable)
  override lazy val absGain = array(0).absGain
  override lazy val absOffset = array(0).absOffset
  override lazy val absUnit = array(0).absUnit


  override def readPointImpl(segment: Int, channel: Int, frame: Int) = array(channel).readPointImpl(segment, frame)
  override def readTraceImpl(segment: Int, channel: Int) = array(channel).readTraceImpl(segment)
  override def readTraceImpl(segment: Int, channel: Int, span:Span) = array(channel).readTraceImpl(segment, span)

  override def :::(that: X): XDataChannelArray = {
    that match {
      case t: XDataChannelArray => {
        if(this.isCompatible(that)){
          val oriThis = this
          new XDataChannelArray {
            override val array = oriThis.array ++ t.array
          }
        } else {
          throw new IllegalArgumentException("the two XDataChannelArray types are not compatible, and cannot be concatenated.")
        }
      }
      case _ => throw new IllegalArgumentException("the two X types are not compatible, and cannot be concatenated.")
    }
  }

}
