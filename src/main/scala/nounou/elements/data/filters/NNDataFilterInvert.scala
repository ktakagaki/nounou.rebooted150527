package nounou.elements.data.filters

import nounou.elements.ranges.{SampleRangeValid, SampleRange}
import nounou.elements.data.NNData
import scala.beans.BooleanBeanProperty
import breeze.linalg.DenseVector

/**
 * @author ktakagaki
 * //@date 04/16/2014.
 */
class NNDataFilterInvert(private var _parent: NNData ) extends NNDataFilter( _parent ) {

  def this(upstream: NNData, inverted: Boolean) = {
    this(upstream)
    setInverted(inverted)
  }

  var inverted = true
  def setInverted(trueFalse: Boolean) = { inverted = trueFalse }
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
      - _parent.readPointImpl(channel, frame, segment)
    } else {
      _parent.readPointImpl(channel, frame, segment)
    }

  override def readTraceDVImpl(channel: Int, range: SampleRangeValid): DenseVector[Int] =
    if(inverted){
      - _parent.readTraceDVImpl(channel, range)
    } else {
      _parent.readTraceDVImpl(channel, range)
    }

}

