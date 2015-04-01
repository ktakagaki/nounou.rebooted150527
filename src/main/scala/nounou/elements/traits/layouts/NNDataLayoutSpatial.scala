package nounou.elements.traits.layouts

import javafx.scene.shape.Rectangle
import nounou.elements.NNElement
import nounou.elements.traits.{NNChannelsElement}

/**Immutable layout object, encapsulating how different channels are
 * laid out across the detector field.
 * This will be used for display and spatial binning.
 * When layout changes due to filtering/binning,
 * new object should be generated and passed downstream.
 *
 */
abstract class NNDataLayoutSpatial extends NNDataLayout {

  override def toString() = "XLayout(fieldX="+fieldX+", fY="+fieldY+", fWidth="+fieldWidth+", fHeight="+fieldHeight+")"

  // <editor-fold defaultstate="collapsed" desc=" field bounding rectangle ">

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

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" correspondence between channel coordinates and channel ">

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

  // </editor-fold>

  /** Geometric radius of detectors.*/
  val channelRadius: Double

  override def isCompatible(that: NNElement): Boolean = that match {
    case x: NNDataLayoutSpatial => this.getClass == x.getClass
    case _ => false
  }

}

class NNDataLayoutPoint(override val channelCount: Int) extends NNDataLayoutSpatial {

  override def toString() = s"${this.getClass.getName}(channelCount = ${channelCount})"

  override val field: Rectangle = new Rectangle(0D, 0D, 100D, 100D)
  override def channelToCoordinatesImpl(ch: Int): Array[Double] = Array(50D, 50D)
  override def coordinatesToChannelImpl(x: Double, y: Double): Int = 0
  override val channelRadius: Double = 25D

}

class NNDataLayoutNull$ extends NNDataLayoutPoint(channelCount = 0)
object NNDataLayoutNull$ extends NNDataLayoutNull$

//ToDo 4: NNLayoutPoints, with multiple points, graphable as image with trode Count grouped

