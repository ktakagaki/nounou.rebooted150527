package nounou.elements.layouts

import javafx.scene.shape.Rectangle
import _root_.nounou.elements.NNElement
import nounou.elements.traits.{NNChannels, NNConcatenatable}

/**Immutable layout object, encapsulating how different channels are
 * laid out across the detector field. This will be used for display and spatial binning.
 * When layout changes due to filtering/binning,
 * new object should be generated.
 *
 */
abstract class NNLayout extends NNChannels {

  override def toString() = "XLayout(fieldX="+fieldX+", fY="+fieldY+", fWidth="+fieldWidth+", fHeight="+fieldHeight+")"

  // field bounding rectangle
  /** The bounding rectangle of the detector field.
   */
  val field: Rectangle
  /** X origin of the bounding rectangle of the detector field (left-hand edge).*/
  final lazy val fieldX: Double = field.getX
  /** Y origin of the bounding rectangle of the detector field (top edge).*/
  final lazy val fieldY: Double = field.getY
  /** Width of the bounding rectangle of the detector field.*/
  final lazy val fieldWidth: Double = field.getWidth
  /** Height of the bounding rectangle of the detector field.*/
  final lazy val fieldHeight: Double = field.getHeight
  /** Maximum X of the bounding rectangle of the detector field (right-hand edge).*/
  final lazy val fieldMaxX = fieldX + fieldWidth
  /** Maximum Y of the bounding rectangle of the detector field (bottom edge).*/
  final lazy val fieldMaxY = fieldY + fieldHeight

  // correspondence between channel coordinates and channel
  /** Center coordinate of chosen detector (or channel,
   *  if it is designated a part of the field for display.
   */
  final def channelToCoordinates(ch: Int): Array[Double] = {
    require(isValidChannel(ch), "Invalid channel!")
    channelToCoordinatesImpl(ch)
  }

  /** Implementation for [[channelToCoordinates(Int)]].
    * Will return Array for Java compatibility, but this should be a newly generated
    * copy Array with immutable intent.
    */
  def channelToCoordinatesImpl(ch: Int): Array[Double]

  /** Detector which covers the chosen coordinates.
    * If x or y coordinates are outside of field,
    * will call for closest corner.*/
  final def coordinatesToChannel(x: Double, y: Double): Int ={
    val realX = if( x < fieldX ) fieldX
                else {  if (x > fieldMaxX ) fieldMaxX
                        else x
                }
    val realY = if( y < fieldY ) fieldY
                else {  if (y > fieldMaxY ) fieldMaxY
                        else y
                }
    coordinatesToChannelImpl(realX, realY)
  }

  /** Implementation for [[coordinatesToChannel(Double, Double)]].
    */
  def coordinatesToChannelImpl(x: Double, y: Double): Int

  /** Geometric radius of detectors.*/
  val channelRadius: Double

  // <editor-fold defaultstate="collapsed" desc="XConcatenatable">

//  override def isCompatible(that: NNElement): Boolean = {
//    that match {
//      case x: NNLayout => {
//        (this.fieldX == x.fieldX) &&
//          (this.fieldY == x.fieldY) &&
//          (this.fieldWidth == x.fieldWidth) &&
//          (this.fieldHeight == x.fieldHeight) &&
//          (this.channelRadius == x.channelRadius)
//        //ToDo 2: Test channel coordinate equality and implement concatenation
//      }
//      case _ => false
//    }
//  }

//  override def :::(x: NNElement): NNLayout = {
//    throw loggerError("XLayout object should not be appended!")
//  }
//  override def :::(x: X): XLayout = x match {
//    case x: XLayout if x.isCompatible(this) => this
////    case x: XData if x.layout.isCompatible(this) => this
//    case _ => throw loggerError("cannot combine, {} is not compatible with this layout {}", x.toString, this.toString())
//  }

  // </editor-fold>

}

class NNLayoutNull$ extends NNLayout {

  override def toString() = "NNLayoutNull"
  //override val channelNames = Vector[String]("null layout")

  override val field: Rectangle = new Rectangle()
  override def channelToCoordinatesImpl(ch: Int): Array[Double] = Array(0D, 0D)
  override def coordinatesToChannelImpl(x: Double, y: Double): Int = -1
  override val channelRadius: Double = 0D
  override val channelCount = 0

  override def isCompatible(that: NNElement): Boolean = false


  //  override def :::(x: X): XLayout = x match {
  //    case XLayoutNull => this
  //    case _ => require(false, "cannot append incompatible data types (XLayoutNull)"); this
  //  }

}

object NNLayoutNull extends NNLayoutNull$

//ToDo 4: XLayoutPoints, with multiple points, graphable as image with trode Count grouped
object NNLayoutPoint extends NNLayoutNull$ {

  override def toString() = "NNLayoutPoint"

  override val field: Rectangle = new Rectangle(0D, 0D, 100D, 100D)
  override def channelToCoordinatesImpl(ch: Int): Array[Double] = Array(50D, 50D)
  override def coordinatesToChannelImpl(x: Double, y: Double): Int = 0
  override val channelRadius: Double = 25D
  override val channelCount = 1


//  override def :::(x: X): XLayout = x match {
//    case x: XLayoutPoint if x.isCompatible(this) => new XLayoutPoint( this.channelNames ++ x.channelNames )
//    //case x: XData if x.layout.isCompatible(this) => this
//    case _ => throw loggerError("cannot combine, {} is not compatible with this layout {}", x.toString, this.toString())
//  }

}

