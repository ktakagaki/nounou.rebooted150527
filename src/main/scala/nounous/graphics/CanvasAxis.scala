package nounous.graphics

import scalafx.scene.paint.Color

/**
 * Created with IntelliJ IDEA.
 * User: takagaki
 * Date: 05.07.13
 * Time: 18:01
 * To change this template use File | Settings | File Templates.
 */
class CanvasAxis(width:Double, height:Double) extends nounous.graphics.Canvas(width, height) {

  val gc = this.graphicsContext2D

  var axisColor = Color.BLACK
  var axisThickness = 4
  var axisOrigin = (0D, 0D)
  var axisLabels = ("", "")

  def draw() {
    gc.setStroke(axisColor)
    gc.setLineWidth(axisThickness)
    gc.strokeLine(0, 0, 200, 300)

    gc.beginPath
    gc.moveTo(50, 50)
    gc.bezierCurveTo(150, 20, 150, 150, 75, 150)
    gc.closePath


  }

  draw()


}
