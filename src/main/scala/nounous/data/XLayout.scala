package nounous.data
 
import java.awt.Rectangle

abstract class XLayout extends X {

  /**Checks whether the input is a valid channel value.
   */
  def isValidChannel(channel: Int): Boolean = (0 <= channel && channel < channelCount)
  private def checkChannel(channel: Int): Unit = require(isValidChannel(channel), "detector: " + channel + " out of range!")
  /**The total number of detectors in the data array.
   */
  def channelCount: Int

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
    checkChannel(ch)
    channelToCoordinatesImpl(ch)
  }
  def channelToCoordinatesImpl(ch: Int): Array[Double]

  /** Detector which covers the chosen coordinates.*/
  def coordinatesToChannel(x: Double, y: Double): Int
  /** Geometric radius of detectors.*/

  val channelRadius: Double
  
}