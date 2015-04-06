package nounou.elements.data

import _root_.nounou.elements.ranges.SampleRangeValid
import breeze.linalg.{DenseVector, min}
import nounou._
import nounou.elements.NNElement
import nounou.elements.traits.NNDataTiming
import nounou.elements.layouts.NNDataLayout

/**Immutable data object to encapsulate arrays of [[NNDataChannel]] objects
  *
 * Created by Kenta on 12/15/13.
 */
 class NNDataChannelArray(val array: Seq[NNDataChannel]) extends NNData {

  //enforce channel compatibility
  loggerRequire( array != null && array.length > 0, "input Vector must be non-negative, non-empty" )
  loggerRequire( array.length == 1 || array(0).isCompatible( array.tail ), "input Array must have compatible components")

  setTiming( array(0).timing() )
  setScale( array(0).scale() )

  def this( array: Array[NNDataChannel] ) = this( array.toVector )
//  def this( array: Array[NNDataChannel], layout: NNLayout ) = this( array.toVector, layout )

  def apply(channel: Int) = array(channel)

//  override def segmentLengthImpl(segment: Int) = {
//    array(0).segmentLengthImpl(segment)
//    //val tempsl =
//      array(0).segmentLengths
//      new Array[Int]( array(0).segmentCount )
//    //println( "XDCA tempsl " + tempsl.toVector.toString )
//    for(s <- 0 until tempsl.length) tempsl(s) = min( DenseVector(array.map(_.segmentLength(s)).toArray) )
//    //println( "XDCA tempsl post " + tempsl.toVector.toString )
//    tempsl
//  }
//  override lazy val segmentStartTs = array(0).segmentStartTs
//  override lazy val sampleRate = array(0).sampleRate

//  // (from XChannelsImmutable)
//  override lazy val channelNames = array.map(_.channelName).toVector

//  // (from XAbsoluteImmutable)
//  override lazy val absGain = array(0).absGain
//  override lazy val absOffset = array(0).absOffset
//  override lazy val absUnit = array(0).absUnit
//  override lazy val scaleMax = array(0).scaleMax
//  override lazy val scaleMin = array(0).scaleMin


  override def readPointImpl(channel: Int, frame: Int, segment: Int) =
    array(channel).readPointImpl(frame, segment)
  override def readTraceDVImpl(channel: Int, range: SampleRangeValid) =
    array(channel).readTraceDVImpl(range)

  def loadDataChannel(dataChannel: NNDataChannel): NNDataChannelArray = {
    if(array(0).isCompatible(dataChannel)){
      new NNDataChannelArray( array :+ dataChannel )
    } else {
      sys.error("New data channel "+dataChannel+" is incompatible with the prior channels. Ignoring loadDataChannel()!")
      this
    }
  }

  // <editor-fold desc="XConcatenatable">

//   override def :::(that: NNElement): NNDataChannelArray = {
//    that match {
//      case t: NNDataChannelArray => {
//        if(this.isCompatible(t)){
//          val oriThis = this
//          new NNDataChannelArray(oriThis.array ++ t.array)
//        } else {
//          throw new IllegalArgumentException("the two XDataChannelArray types are not compatible, and cannot be concatenated.")
//        }
//      }
//      case t: NNDataChannel => {
//        if(this(0).isCompatible(t)){
//          new NNDataChannelArray( this.array :+ t)
//        } else {
//          throw new IllegalArgumentException("the XDataChannelArray type and XDataChannel type are not compatible, and cannot be concatenated.")
//        }
//      }
//      case _ => throw new IllegalArgumentException("the two X types are not compatible, and cannot be concatenated.")
//    }
//  }

  override def isCompatible(that: NNElement): Boolean = {
    that match {
        //ToDo 3: is this advisable?
      case x: NNDataChannel => this(0).isCompatible(x)
      case x: NNDataChannelArray => this(0).isCompatible(x(0))
      case _ => false
    }
  }

  // </editor-fold>
//  /** Number of segments in data.
//    */
//  override def segmentCount: Int = array(0).segmentCount
//
//  /** OVERRIDE: End timestamp for each segment. Implement by overriding _endTimestamp
//    */
//  override def segmentEndTs: Array[Long] = array(0).segmentEndTs

  override def getChannelCount: Int = array.length

}
