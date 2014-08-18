package nounou.data

import nounou._
import nounou.data.traits.{XFramesImmutable, XConcatenatable}

/**Immutable data object to encapsulate arrays of [[XDataChannel]] objects
  *
 * Created by Kenta on 12/15/13.
 */
 class XDataChannelArray(val array: Vector[XDataChannel], override val layout: XLayout = XLayoutNull)
  extends XData with XConcatenatable with XFramesImmutable {
  //ToDo 2: Clarify what is exactly immutable, and enforce

  //enforce channel compatibility
  loggerRequire( array != null && array.length > 0, "input Vector must be non-negative, non-empty" )
  loggerRequire( array.length == 1 || array.tail.forall(array(0).isCompatible(_)), "input Vector must have compatible components")

  def this( dataChannel: XDataChannel, layout: XLayout ) = this( Vector[XDataChannel]( dataChannel ), layout )
  def this( dataChannel: XDataChannel ) = this( Vector[XDataChannel]( dataChannel ) )

  def apply(channel: Int) = array(channel)

  override lazy val segmentLength = array(0).segmentLength
  override lazy val segmentStartTs = array(0).segmentStartTs
  override lazy val sampleRate = array(0).sampleRate

  // (from XChannelsImmutable)
  override lazy val channelNames = array.map(_.channelName)

  // (from XAbsoluteImmutable)
  override lazy val absGain = array(0).absGain
  override lazy val absOffset = array(0).absOffset
  override lazy val absUnit = array(0).absUnit
  override lazy val scaleMax = array(0).scaleMax
  override lazy val scaleMin = array(0).scaleMin


  override def readPointImpl(channel: Int, frame: Int) = array(channel).readPointImpl(frame)//, segment)
  override def readTraceImpl(channel: Int, range: Range.Inclusive) = array(channel).readTraceImpl(range)//, segment)

  def loadDataChannel(dataChannel: XDataChannel): XDataChannelArray = {
    if(array(0).isCompatible(dataChannel)){
      new XDataChannelArray( array :+ dataChannel )
    } else {
      sys.error("New data channel "+dataChannel+" is incompatible with the prior channels. Ignoring loadDataChannel()!")
      this
    }
  }

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
        //ToDo 3: is this advisable?
      case x: XDataChannel => this(0).isCompatible(x)
      case x: XDataChannelArray => this(0).isCompatible(x(0))
      case _ => false
    }
  }

  // </editor-fold>

}
