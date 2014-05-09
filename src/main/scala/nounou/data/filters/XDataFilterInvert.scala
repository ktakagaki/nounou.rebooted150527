package nounou.data.filters

import nounou.data.XData
import scala.beans.BooleanBeanProperty
import breeze.linalg.DenseVector

/**
 * @author ktakagaki
 * @date 04/16/2014.
 */
class XDataFilterInvert(override val upstream: XData ) extends XDataFilter( upstream ) {

  @BooleanBeanProperty
  var inverted = false
  def setInverted(trueFalse: Int) = trueFalse match {
    case 1 => inverted = false
    case -1 => inverted = true
    case _ => throw loggerError("argument for setInverted() must be 1 or -1")
  }
  def setInverted(trueFalse: Double) = trueFalse match {
    case 1d => inverted = false
    case -1d => inverted = true
    case _ => throw loggerError("argument for setInverted() must be 1 or -1")
  }

  override def toString() = {
    if(inverted) "XDataFilterInvert: on (inverted=true)"
    else "XDataFilterInvert: off (inverted=false)"
  }

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int =
    if(inverted){
      - upstream.readPointImpl(channel, frame, segment)
    } else {
      upstream.readPointImpl(channel, frame, segment)
    }

  override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): DenseVector[Int] =
    if(inverted){
      - upstream.readTraceImpl(channel, range, segment)
    } else {
      upstream.readTraceImpl(channel, range, segment)
    }

}
