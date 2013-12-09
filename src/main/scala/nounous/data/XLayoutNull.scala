package nounous.data

import java.awt.Rectangle

/**
 * Created with IntelliJ IDEA.
 * User: Kenta
 * Date: 12/3/13
 * Time: 2:19 PM
 * To change this template use File | Settings | File Templates.
 */
class XLayoutNull extends XLayout {
  /** The total number of detectors in the data array.
    */
  def channelCount: Int = 0

  val field: Rectangle = new Rectangle()

  def channelToCoordinatesImpl(ch: Int): Array[Double] = Array(0D, 0D)

  /** Detector which covers the chosen coordinates. */
  def coordinatesToChannel(x: Double, y: Double): Int = 0

  val channelRadius: Double = 0

  def isCompatible(that: X): Boolean = false

  def :::(target: X): X = this
}
