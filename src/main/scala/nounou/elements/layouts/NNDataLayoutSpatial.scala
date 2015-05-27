package nounou.elements.layouts

import javafx.scene.shape.Rectangle
import nounou.elements.NNElement
import nounou.elements.traits.{NNChannelsElement}

/**Immutable layout object, encapsulating how different channels are
 * laid out across the detector field.
 * This will be used for display and spatial binning.
 * When layout changes due to filtering/binning,
 * a new object should be generated and passed downstream, together with
 * change event trigger.
 *
 */
abstract class NNDataLayoutSpatial extends NNDataLayout {

  override def toString() = "XLayout(fieldX="+fieldX+", fY="+fieldY+", fWidth="+fieldWidth+", fHeight="+fieldHeight+")"

  @transient protected var initialized = false
  @transient protected var fieldCache: Rectangle = null
  @transient protected var channelCountCache: Int = -1
  protected var channelRadius: Double = 50d
  protected var channelDistance: Double = 100d

  /**Should use given parameters to initialize this object, namely
    * fieldCache, channelCountCache,
    * and then set initialized to true.
   */
  def initialize(): Unit

  @transient final lazy val getChannelCount: Int = {
    if(!initialized) initialize()
    channelCountCache
  }
  /** Geometric radius of detectors.*/
  @transient final lazy val getChannelRadius: Double = channelRadius
  /** Geometric radius of detectors.*/
  @transient final lazy val getChannelDistance: Double = channelDistance


  // <editor-fold defaultstate="collapsed" desc=" field bounding rectangle ">

  /** The bounding rectangle of the detector field.
   */
  @transient final lazy val field: Rectangle = {
    if(!initialized) initialize()
    fieldCache
  }
  /** X origin of the bounding rectangle of the detector field (left-hand edge).*/
  @transient final lazy val fieldX: Double = field.getX
  /** Y origin of the bounding rectangle of the detector field (top edge).*/
  @transient final lazy val fieldY: Double = field.getY
  /** Width of the bounding rectangle of the detector field.*/
  @transient final lazy val fieldWidth: Double = field.getWidth
  /** Height of the bounding rectangle of the detector field.*/
  @transient final lazy val fieldHeight: Double = field.getHeight
  /** Maximum X of the bounding rectangle of the detector field (right-hand edge).*/
  @transient final lazy val fieldMaxX = fieldX + fieldWidth
  /** Maximum Y of the bounding rectangle of the detector field (bottom edge).*/
  @transient final lazy val fieldMaxY = fieldY + fieldHeight

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" correspondence between channel coordinates and channel ">

  /** Center coordinate of chosen detector (or channel,
   *  if it is designated a part of the field for display.
   */
  final def getChannelCoordinates(ch: Int): Array[Double] = {
    if(!initialized) initialize()
    require(isValidChannel(ch), "Invalid channel!")
    getChannelCoordinatesImpl(ch)
  }

  /** Implementation for [[nounou.elements.layouts.NNDataLayoutSpatial.getChannelCoordinates getChannelCoordinates]].
    * Will return Array for Java compatibility, but this should be a newly generated
    * copy Array with immutable intent.
    */
  def getChannelCoordinatesImpl(ch: Int): Array[Double]

//  /** Detector which covers the chosen coordinates.
//    * If x or y coordinates are outside of field,
//    * will call for closest corner.*/
//  final def coordinatesToChannel(x: Double, y: Double): Int ={
//    val realX = if( x < fieldX ) fieldX
//                else {  if (x > fieldMaxX ) fieldMaxX
//                        else x
//                }
//    val realY = if( y < fieldY ) fieldY
//                else {  if (y > fieldMaxY ) fieldMaxY
//                        else y
//                }
//    coordinatesToChannelImpl(realX, realY)
//  }
//
//  /** Implementation for [[coordinatesToChannel(Double, Double)]].
//    */
//  def coordinatesToChannelImpl(x: Double, y: Double): Int

  // </editor-fold>


  override def isCompatible(that: NNElement): Boolean = that match {
    case x: NNDataLayoutSpatial => this.getClass == x.getClass
    case _ => false
  }

}


//class NNDataLayoutNull$ extends NNDataLayoutPoint(channelCount = 0)
//object NNDataLayoutNull$ extends NNDataLayoutNull$
//
////ToDo 4: NNLayoutPoints, with multiple points, graphable as image with trode Count grouped

