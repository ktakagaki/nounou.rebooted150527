package nounou.data.filters

import nounou.data.{XLayoutSquare, XLayout, XData}
import breeze.linalg.{DenseVector => DV}

/**
 * @author ktakagaki
 * @date 2/28/14.
 */
class XDataFilterImageBin(override val upstream: XData) extends XDataFilter( upstream ) {

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
      changedData()
    } else {
      layout match {
        case xLayout: XLayoutSquare => {
          _factor = factor
          logger.info( "set binning factor to {}", factor.toString )
          changedData()
        }
        case _ => throw loggerError("binning is not supported for this layout {} yet", layout.toString())
      }
    }
  }
  /** Java-style alias for [[factor_=()]] aka [[[[factor_$eq()]]]].
    */
  def setFactor( factor: Int ): Unit = factor_=( factor )
  protected var  _factor: Int = 1

  // </editor-fold>

  override def channelNames: scala.Vector[String] = upstream.channelNames

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = upstream.readPointImpl(channel, frame, segment)
  override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): DV[Int] = upstream.readTraceImpl(channel, range, segment)
  override def readFrameImpl(frame: Int, segment: Int): DV[Int] = upstream.readFrameImpl(frame, segment)
  override def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int): DV[Int] = upstream.readFrameImpl(frame, channels, segment)

  protected var layoutBuff = upstream.layout()
//  override def layout: XLayout = if( factor() == 1 ) upstream.layout()
//      else if ()


}
