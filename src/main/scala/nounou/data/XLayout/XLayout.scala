package nounou.data

import javafx.scene.shape.Rectangle
import _root_.nounou.data.X
import nounou.data.traits.{XChannels, XConcatenatable}

/**Immutable layout object, encapsulating how different channels are
 * laid out across the detector field. This will be used for display and spatial binning.
 * When layout changes due to filtering/binning,
 * new object should be generated.
 *
 */
abstract class XLayout extends X with XChannels with XConcatenatable {

  override def toString() = "XLayout(fieldX="+fieldX+", fY="+fieldY+", fWidth="+fieldWidth+", fHeight="+fieldHeight+")"


  // field bounding rectangle
  /** The bounding rectangle of the detector field.
   */
  val field: Rectangle
  /** X origin of the bounding rectangle of the detector field.*/
  final lazy val fieldX: Double = field.getX
  /** Y origin of the bounding rectangle of the detector field.*/
  final lazy val fieldY: Double = field.getY
  /** Width of the bounding rectangle of the detector field.*/
  final lazy val fieldWidth: Double = field.getWidth
  /** Height of the bounding rectangle of the detector field.*/
  final lazy val fieldHeight: Double = field.getHeight

  // correspondence between channel coordinates and channel
  /** Center coordinate of chosen detector (or channel,
   *  if it is designated a part of the field for display.
   *  Will return Array for Java compatibility, but this should be an immutable copy.
   */
  final def channelToCoordinates(ch: Int): Array[Double] = {
    require(isValidChannel(ch), "Invalid channel!")
    channelToCoordinatesImpl(ch)
  }
  def channelToCoordinatesImpl(ch: Int): Array[Double]

  /** Detector which covers the chosen coordinates.*/
  def coordinatesToChannel(x: Double, y: Double): Int

  /** Geometric radius of detectors.*/
  val channelRadius: Double

  // <editor-fold defaultstate="collapsed" desc="XConcatenatable">

  override def isCompatible(that: X): Boolean = {
    that match {
      case x: XLayout => {
        (this.fieldX == x.fieldX) &&
          (this.fieldY == x.fieldY) &&
          (this.fieldWidth == x.fieldWidth) &&
          (this.fieldHeight == x.fieldHeight) &&
          (this.channelRadius == x.channelRadius)
        //ToDo 2: Test channel coordinate equality and implement concatenation
      }
      case _ => false
    }
  }

  override def :::(x: X): XLayout = {
    throw loggerError("XLayout object should not be appended!")
  }
//  override def :::(x: X): XLayout = x match {
//    case x: XLayout if x.isCompatible(this) => this
////    case x: XData if x.layout.isCompatible(this) => this
//    case _ => throw loggerError("cannot combine, {} is not compatible with this layout {}", x.toString, this.toString())
//  }

  // </editor-fold>

}

class XLayoutPoint() extends XLayout {

  override val field: Rectangle = new Rectangle()
  override def channelToCoordinatesImpl(ch: Int): Vector[Double] = Vector(0D, 0D)
  /** Detector which covers the chosen coordinates. */
  override def coordinatesToChannel(x: Double, y: Double): Int = 0
  override val channelRadius: Double = 0

  override def toString() = "XLayoutPoint( )"//channels=" + channelNames.length.toString + " )"

//  override def :::(x: X): XLayout = x match {
//    case x: XLayoutPoint if x.isCompatible(this) => new XLayoutPoint( this.channelNames ++ x.channelNames )
//    //case x: XData if x.layout.isCompatible(this) => this
//    case _ => throw loggerError("cannot combine, {} is not compatible with this layout {}", x.toString, this.toString())
//  }

}

//ToDo 4: XLayoutPoints, with multiple points, graphable as image with trodeCount grouped

object XLayoutNull extends XLayout {

  //override val channelNames = Vector[String]("null layout")

  override val field: Rectangle = new Rectangle()
  override def channelToCoordinatesImpl(ch: Int): Vector[Double] = Vector(0D, 0D)
  /** Detector which covers the chosen coordinates. */
  override def coordinatesToChannel(x: Double, y: Double): Int = 0
  override val channelRadius: Double = 0

  override def isCompatible(that: X): Boolean = false

  override def toString() = "XLayoutNull"

//  override def :::(x: X): XLayout = x match {
//    case XLayoutNull => this
//    case _ => require(false, "cannot append incompatible data types (XLayoutNull)"); this
//  }

}
