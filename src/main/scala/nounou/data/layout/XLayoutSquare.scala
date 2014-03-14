package nounou.data

import javafx.scene.shape.Rectangle


/**XLayout object for detector fields with square, matrix layouts.
 * @author takagaki
 *
 */
class XLayoutSquare(val _xDimensions: Int, val _yDimensions: Int) extends XLayout {

  //This is inferred from channelNames.length
  //override val channelCount = xDimensions * yDimensions
  protected def _channelRadius: Double = 0.5
  def channelRadius() = _channelRadius
  def channelRadius_=(v: Double) = throw loggerError("channel radius cannot be overwritten!")

  def xDimensions() = _xDimensions
  def yDimensions() = _yDimensions

  protected var chNamesCnt = -1
  override def channelNames = _channelNames
  protected lazy val _channelNames: Vector[String] =
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

  override def :::(x: X): XLayoutSquare = x match {
    case x: XLayoutSquare if x.isCompatible(this) => throw loggerError("Concatenation of square layouts is not defined!")
    case _ => throw loggerError("cannot combine, {} is not compatible with this layout {}", x.toString, this.toString())
  }

}


/**XLayout object for detector fields with square, matrix layouts.
  * @author takagaki
  *
  */
class XLayoutSquareBinned(val xDimensionsOri: Int, val yDimensionsOri: Int, protected var _factor: Int = 1) extends XLayoutSquare(xDimensionsOri, yDimensionsOri) {

  // <editor-fold defaultstate="collapsed" desc=" factor setting/getting ">

  def factor(): Int = _factor

  /** Java-style alias for [[factor()]].
    */
  def getFactor(): Int = factor
  def factor_=( factor: Int ) = {
    loggerRequire( factor >= 1, "new factor {} cannot be less than 1!", factor.toString )
    if( factor == this.factor ){
      logger.trace( "factor is already {}}, not changing. ", factor.toString )
    } else if( factor == 1 ) {
      _factor = factor
      logger.info( "turned filter off, i.e. factor = {}", factor.toString )
    } else {
      loggerRequire( xDimensionsOri / factor > 0 && yDimensionsOri / factor > 0,
            "factor {} too large for dimensions ({}, {})", factor.toString, xDimensionsOri.toString, yDimensionsOri.toString)
      _factor = factor
      logger.info( "set binning factor to {}", factor.toString )
    }
  }
  /** Java-style alias for [[factor_=()]] aka [[[[factor_$eq()]]]].
    */
  def setFactor( factor: Int ): Unit = factor_=( factor )

  // </editor-fold>

  //This is inferred from channelNames.length
  override def channelRadius: Double = 0.5 * factor

  override def xDimensions(): Int = xDimensionsOri / factor
  override def yDimensions(): Int = yDimensionsOri / factor


  var channelNamesBuff: Vector[String] = Vector[String]()
  private var channelNamesBuffFactor = -1
  override def channelNames(): Vector[String] = {
    if( channelNamesBuffFactor == factor ) channelNamesBuff
    else {
//      if( factor == 1 ) super.channelNames
//      else {
        chNamesCnt = -1
        channelNamesBuff = (for( y <- 0 until yDimensions; x <- 0 until xDimensions ) yield {
             chNamesCnt += 1
             "(x, y) = ("+ x+", "+ y+"), pix# = "+ chNamesCnt + ", bin factor= " + factor }).toVector
        channelNamesBuffFactor = factor()
        channelNamesBuff
//      }
    }
  }

  /** Detector which covers the chosen coordinates. */
  override def coordinatesToChannel(x: Double, y: Double): Int = super.coordinatesToChannel( x/factor, y/factor)

  //ToDo 2: shift this to center of bin
  override def channelToCoordinatesImpl(ch: Int): Vector[Double] = Vector( ( ch % xDimensions).toDouble * factor, (ch / xDimensions).toDouble * factor )

//  override def field: Rectangle = new Rectangle(- channelRadius, - channelRadius,  xDimensions.toDouble,  yDimensions.toDouble)
}