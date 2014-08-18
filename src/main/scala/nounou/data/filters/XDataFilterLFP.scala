package nounou.data.filters

import breeze.linalg.DenseVector
import nounou.data.XData

import scala.beans.BeanProperty

/**
 * @author ktakagaki
 * @date 08/15/2014.
 */
class XDataFilterLFP( private var _parent: XData) extends XDataFilter(_parent) {

  val _decimate: XDataFilterDecimate = new XDataFilterDecimate(_parent)
  val _fir: XDataFilterFIR = new XDataFilterFIR(_decimate)

  // <editor-fold defaultstate="collapsed" desc=" get/set decimate settings ">

  def setDecimateOff(): Unit = _decimate.setDecimateOff()

  def setFactor(factor: Int): Unit = _decimate.setFactor(factor)

  def getFactor(): Int = _decimate.getFactor()

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" get/set filter settings ">

  def setFilterOff(): Unit = _fir.setFilterOff()

  def setFilter( omega0: Double, omega1: Double): Unit = _fir.setFilter(omega0, omega1)

  def setFilterHz( f0: Double, f1: Double): Unit = _fir.setFilterHz(f0, f1)

  def getFilterHz(): Array[Double] = _fir.getFilterHz()

  def getTaps() = _fir.getTaps

  def setTaps(taps: Int) = _fir.setTaps( taps )
  // </editor-fold>

  override def readPointImpl(channel: Int, frame: Int): Int = _fir.readPointImpl(channel, frame)
  override def readTraceImpl(channel: Int, range: Range.Inclusive): DenseVector[Int] = _fir.readTraceImpl(channel, range)

  }
