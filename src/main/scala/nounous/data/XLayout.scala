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

    /** The bounding rectangle of the detector field.
     */
    def field: Rectangle
    /** X origin of the bounding rectangle of the detector field.*/
    def fieldX: Int
    /** Y origin of the bounding rectangle of the detector field.*/
    def fieldY: Int
    /** Width of the bounding rectangle of the detector field.*/
    def fieldWidth: Int
    /** Height of the bounding rectangle of the detector field.*/
    def fieldHeight: Int
    
    /** Center coordinate of chosen detector (or channel,
     *  if it is designated a part of the field for display.
     */
    def channelToCoordinates(ch: Int): Array[Int] = {
      checkChannel(ch)
      channelToCoordinatesImpl(ch)
    }
    protected def channelToCoordinatesImpl(ch: Int): Array[Int]
    
    /** X coordinate of chosen detector (or channel,
     *  if it is designated a part of the field for display.*/
    final def channelX(ch: Int): Int = channelToCoordinates(ch)(0)
    /** Y coordinate of chosen detector (or channel,
     *  if it is designated a part of the field for display.*/
    final def channelY(ch: Int): Int = channelToCoordinates(ch)(1)
    
    
    /** Detector which covers the chosen coordinates.*/
    final def coordinateToChannel(coordinates: Array[Int]): Int = coordinateToChannel(coordinates(0), coordinates(1))
    /** Detector which covers the chosen coordinates.*/
    def coordinateToChannel(x: Int, y: Int): Int
    /** Geometric radius of detectors.*/
    def channelRadius(): Int
  
}