package nounou.data

import java.awt.Rectangle
import nounou.data.traits.XChannelsImmutable

abstract class XLayout extends X with XChannelsImmutable {

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
  final def channelToCoordinates(ch: Int): Array[Double] = {
    require(isValidChannel(ch), "Invalid channel!")
    channelToCoordinatesImpl(ch)
  }
  def channelToCoordinatesImpl(ch: Int): Array[Double]

  /** Detector which covers the chosen coordinates.*/
  def coordinatesToChannel(x: Double, y: Double): Int
  /** Geometric radius of detectors.*/

  val channelRadius: Double

  override def isCompatible(that: X): Boolean = {
    that match {
      case x: XLayout => {
        //not Channels
        (this.field == x.field) && (this.channelRadius == x.channelRadius)
      }
      case _ => false
    }
  }

  override def toString() = "XLayout(fieldX="+fieldX+", fieldY="+fieldY+", fieldWidth="+fieldWidth+", fieldHeight="+fieldHeight+")"

}

object XLayoutNull extends XLayout {

  override val channelNames = Vector[String]("null layout")

  val field: Rectangle = new Rectangle()

  def channelToCoordinatesImpl(ch: Int): Array[Double] = Array(0D, 0D)

  /** Detector which covers the chosen coordinates. */
  def coordinatesToChannel(x: Double, y: Double): Int = 0

  val channelRadius: Double = 0

  override def isCompatible(that: X): Boolean = false

  override def toString() = "XLayoutNull"
}
