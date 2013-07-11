/**
 *
 */
package nounous.data

import java.awt.Rectangle

/**
 * @author takagaki
 *
 */
class LayoutSingle extends DataLayout {

  override def isValidChannel(channel: Int) = (channel == 0)

  override val fieldX = 0
  override val fieldY = 0
  override val fieldWidth = 10000
  override val fieldHeight = 10000
  override val field = new Rectangle(fieldWidth, fieldHeight)
  
  override val detectorCount = 1

  override def detectorCoordinatesImpl(det: Int) = Array(5000, 5000)

  override def coordinateDetector(x: Int, y: Int): Int = 0   
  override val detectorRadius = 5000

}