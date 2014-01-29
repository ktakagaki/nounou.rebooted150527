package nounou.data.xdata

import nounou.data.{XConcatenatable, X, Span}

/**
 * Created by Kenta on 12/15/13.
 */
 class XDataChannelArray(val array: Vector[XDataChannel]) extends XDataImmutable with XConcatenatable{

  //require channel compatibility on initialization?

  def apply(channel: Int) = array(channel)

  override lazy val segmentLengths = array(0).segmentLengths
  override lazy val segmentStartTSs = array(0).segmentStartTSs
  override lazy val sampleRate = array(0).sampleRate

  // (from XChannelsImmutable)
  override lazy val channelNames = array.map(_.channelName)

  // (from XAbsoluteImmutable)
  override lazy val absGain = array(0).absGain
  override lazy val absOffset = array(0).absOffset
  override lazy val absUnit = array(0).absUnit


  override def readPointImpl(channel: Int, frame: Int, segment: Int) = array(channel).readPointImpl(frame, segment)
  override def readTraceImpl(channel: Int, span:Span, segment: Int) = array(channel).readTraceImpl(span, segment)

  // <editor-fold desc="XConcatenatable">

   override def :::(that: X): XDataChannelArray = {
    that match {
      case t: XDataChannelArray => {
        if(this.isCompatible(t)){
          val oriThis = this
          new XDataChannelArray(oriThis.array ++ t.array)
        } else {
          throw new IllegalArgumentException("the two XDataChannelArray types are not compatible, and cannot be concatenated.")
        }
      }
      case t: XDataChannel => {
        if(this(0).isCompatible(t)){
          new XDataChannelArray( this.array :+ t)
        } else {
          throw new IllegalArgumentException("the XDataChannelArray type and XDataChannel type are not compatible, and cannot be concatenated.")
        }
      }
      case _ => throw new IllegalArgumentException("the two X types are not compatible, and cannot be concatenated.")
    }
  }

  override def isCompatible(that: X): Boolean = {
    that match {
      case x: XDataChannel => this(0).isCompatible(x)
      case x: XDataChannelArray => this(0).isCompatible(x(0))
      case _ => false
    }
  }

  // </editor-fold>

}
