package nounous.data
 
import java.awt.Rectangle

abstract class DataLayout {

    /**Checks whether the input is a valid channel value.*/
    def isValidChannel(channel: Int): Boolean

    /** The bounding rectangle of the detector field.*/
    def field: Rectangle
    /** X origin of the bounding rectangle of the detector field.*/
    def fieldX: Int
    /** Y origin of the bounding rectangle of the detector field.*/
    def fieldY: Int
    /** Width of the bounding rectangle of the detector field.*/
    def fieldWidth: Int
    /** Height of the bounding rectangle of the detector field.*/
    def fieldHeight: Int
    

    private def checkDetector(det: Int): Unit = require(0 <= det && det < detectorCount, "detector: " + det + " out of range!")
      
    /**The total number of detectors in the data array.*/
    def detectorCount: Int
    /** Center coordinate of chosen detector (or channel,
     *  if it is designated a part of the field for display.*/
    def detectorCoordinates(det: Int): Array[Int] = {
      checkDetector(det);
      detectorCoordinatesImpl(det)
    }
    protected def detectorCoordinatesImpl(det: Int): Array[Int]
    
    /** X coordinate of chosen detector (or channel,
     *  if it is designated a part of the field for display.*/
    final def detectorX(detector: Int): Int = detectorCoordinates(detector)(0)
    /** Y coordinate of chosen detector (or channel,
     *  if it is designated a part of the field for display.*/
    final def detectorY(detector: Int): Int = detectorCoordinates(detector)(1)
    
    
    /** Detector which covers the chosen coordinates.*/
    final def coordinateDetector(coordinates: Array[Int]): Int = coordinateDetector(coordinates(0), coordinates(1))
    /** Detector which covers the chosen coordinates.*/
    def coordinateDetector(x: Int, y: Int): Int   
    /** Geometric radius of detectors.*/
    def detectorRadius(): Int
  
}