package nounous.data
 
import java.awt.Rectangle

abstract class Layout {

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
    def channelCoordinates(ch: Int): Array[Int] = {
      checkChannel(ch);
      channelCoordinatesImpl(ch)
    }
    protected def channelCoordinatesImpl(ch: Int): Array[Int]
    
    /** X coordinate of chosen detector (or channel,
     *  if it is designated a part of the field for display.*/
    final def channelX(ch: Int): Int = channelCoordinates(ch)(0)
    /** Y coordinate of chosen detector (or channel,
     *  if it is designated a part of the field for display.*/
    final def channelY(ch: Int): Int = channelCoordinates(ch)(1)
    
    
    /** Detector which covers the chosen coordinates.*/
    final def coordinateChannel(coordinates: Array[Int]): Int = coordinateChannel(coordinates(0), coordinates(1))
    /** Detector which covers the chosen coordinates.*/
    def coordinateChannel(x: Int, y: Int): Int
    /** Geometric radius of detectors.*/
    def channelRadius(): Int
  
}