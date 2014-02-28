package nounou.data

import javafx.scene.shape.Rectangle
import nounou.data.traits.{XConcatenatable, XChannelsImmutable}

abstract class XLayout extends X with XChannelsImmutable with XConcatenatable {

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
   */
  final def channelToCoordinates(ch: Int): Vector[Double] = {
    require(isValidChannel(ch), "Invalid channel!")
    channelToCoordinatesImpl(ch)
  }
  def channelToCoordinatesImpl(ch: Int): Vector[Double]

  /** Detector which covers the chosen coordinates.*/
  def coordinatesToChannel(x: Double, y: Double): Int
  /** Geometric radius of detectors.*/

  val channelRadius: Double

  // <editor-fold defaultstate="collapsed" desc="XConcatenatable">

  override def isCompatible(that: X): Boolean = {
    that match {
      case x: XLayout => {
        //not Channels
        (this.field == x.field) && (this.channelRadius == x.channelRadius)
      }
      case _ => false
    }
  }

  override def :::(x: X): XLayout = x match {
    case x: XLayout if x.isCompatible(this) => this
    case x: XData if x.layout.isCompatible(this) => this
    case _ => throw loggerError("cannot combine, {} is not compatible with this layout {}", x.toString, this.toString())
  }

  // </editor-fold>

  override def toString() = "XLayout(fieldX="+fieldX+", fY="+fieldY+", fWidth="+fieldWidth+", fHeight="+fieldHeight+")"

}

object XLayoutNull extends XLayout {

  override val channelNames = Vector[String]("null layout")

  val field: Rectangle = new Rectangle()

  def channelToCoordinatesImpl(ch: Int): Vector[Double] = Vector(0D, 0D)

  /** Detector which covers the chosen coordinates. */
  def coordinatesToChannel(x: Double, y: Double): Int = 0

  val channelRadius: Double = 0

  override def isCompatible(that: X): Boolean = false

  override def toString() = "XLayoutNull"

}
