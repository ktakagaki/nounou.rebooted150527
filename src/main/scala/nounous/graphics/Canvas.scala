package nounous.graphics


/**
 * Created with IntelliJ IDEA.
 * User: takagaki
 * Date: 05.07.13
 * Time: 18:02
 * To change this template use File | Settings | File Templates.
 */
class Canvas(width:Double, height:Double) extends scalafx.scene.canvas.Canvas(width, height) {

  private var xOffset: Double = 0D
  private var yOffset: Double = 0D
  private var xGain: Double = 250D
  private var yGain: Double = 300D

  def toCoordX(x: Double) = x * xGain + xOffset
  def toCoordY(y: Double) = y * yGain + yOffset
  def toCoord(x: Double, y: Double): (Double, Double) = (toCoordX(x), toCoordY(y))


}
