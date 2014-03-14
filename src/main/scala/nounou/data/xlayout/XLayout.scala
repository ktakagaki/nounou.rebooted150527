package nounou.data

import javafx.scene.shape.Rectangle
import nounou.data.traits.{XChannels, XConcatenatable, XChannelsImmutable}

/**Mutable layout object, works for filtering/binning... //ToDo 3: make immutable layout object
 *
 */
abstract class XLayout extends X with XChannels with XConcatenatable {
  //ToDo 2: XChannels is not immutable d/t binning filter downstream. Reevaluate, perhaps regenerate Layout for each new filter?

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

  def channelRadius: Double

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

  //ToDo 3: better concatenation(XLayoutPoint) and compatibility testing
  override def :::(x: X): XLayout
//  override def :::(x: X): XLayout = x match {
//    case x: XLayout if x.isCompatible(this) => this
////    case x: XData if x.layout.isCompatible(this) => this
//    case _ => throw loggerError("cannot combine, {} is not compatible with this layout {}", x.toString, this.toString())
//  }

  // </editor-fold>

  override def toString() = "XLayout(fieldX="+fieldX+", fY="+fieldY+", fWidth="+fieldWidth+", fHeight="+fieldHeight+")"

}

class XLayoutPoint(override val channelNames: Vector[String]) extends XLayout {

  override val field: Rectangle = new Rectangle()
  override def channelToCoordinatesImpl(ch: Int): Vector[Double] = Vector(0D, 0D)
  /** Detector which covers the chosen coordinates. */
  override def coordinatesToChannel(x: Double, y: Double): Int = 0
  override val channelRadius: Double = 0

  override def toString() = "XLayoutPoint( channels=" + channelNames.length.toString + " )"

  override def :::(x: X): XLayout = x match {
    case x: XLayoutPoint if x.isCompatible(this) => new XLayoutPoint( this.channelNames ++ x.channelNames )
    //case x: XData if x.layout.isCompatible(this) => this
    case _ => throw loggerError("cannot combine, {} is not compatible with this layout {}", x.toString, this.toString())
  }

}

//ToDo 4: XLayoutPoints, with multiple points, graphable as image with trodes grouped

object XLayoutNull extends XLayout {

  override val channelNames = Vector[String]("null layout")

  override val field: Rectangle = new Rectangle()
  override def channelToCoordinatesImpl(ch: Int): Vector[Double] = Vector(0D, 0D)
  /** Detector which covers the chosen coordinates. */
  override def coordinatesToChannel(x: Double, y: Double): Int = 0
  override val channelRadius: Double = 0

  override def isCompatible(that: X): Boolean = false

  override def toString() = "XLayoutNull"

  override def :::(x: X): XLayout = x match {
    case XLayoutNull => this
    case _ => require(false, "cannot append incompatible data types (XLayoutNull)"); this
  }

}
