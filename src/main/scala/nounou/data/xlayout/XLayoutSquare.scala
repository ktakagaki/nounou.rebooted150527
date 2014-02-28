package nounou.data

import javafx.scene.shape.Rectangle


/**XLayout object for detector fields with square, matrix layouts.
 * @author takagaki
 *
 */
class XLayoutSquare(val xDimensions: Int, val yDimensions: Int) extends XLayout {

  //This is inferred from channelNames.length
  //override val channelCount = xDimensions * yDimensions
  override val channelRadius: Double = 0.5

  private var chNamesCnt = -1
  override lazy val channelNames: Vector[String] =
    (for( y <- 0 until yDimensions.toInt; x <- 0 until xDimensions.toInt ) yield {chNamesCnt += 1; "(x, y) = ("+ x+", "+ y+"), pix# = "+ chNamesCnt}).toVector

  /** Detector which covers the chosen coordinates. */
  override def coordinatesToChannel(x: Double, y: Double): Int = {
    val tempx = x.toInt //scala.math.round(x).toInt
    val tempy = y.toInt
    (if( tempy < 0 ) 0 else if (tempy >= yDimensions ) yDimensions - 1 else tempy) * xDimensions +
                  (if( tempx < 0 ) 0 else if (tempx >= xDimensions ) xDimensions - 1 else tempx)
  }

  override def channelToCoordinatesImpl(ch: Int): Vector[Double] = Vector( ( ch % xDimensions).toDouble, (ch / xDimensions).toDouble )

  override val field: Rectangle = new Rectangle(- channelRadius, - channelRadius, xDimensions.toDouble, yDimensions.toDouble)
}