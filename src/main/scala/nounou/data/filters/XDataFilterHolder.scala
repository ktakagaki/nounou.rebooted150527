package nounou.data.filters

import nounou.data.{XDataNull, X, XData}

/**This class serves as an "immutable" reference point for various data structures, which may change.
  *The `upstream` variable is not valid in the usual sense, and everything is redirected to the
  * variable `heldData`. Overwrite `heldData` to change the output of this class.
 * @author ktakagaki
 * @date 2/15/14.
  */
class XDataFilterHolder( val upstream: XData = XDataNull ) extends XData {

  private var _heldData: XData = XDataNull
  def heldData: XData = _heldData
  def heldData_=( newData: XData ) = {
    _heldData = newData
    changedData()
    changedTiming()
  }

  override def channelNames: scala.Vector[String] = heldData.channelNames

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = heldData.readPointImpl(channel, frame, segment)
  override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): Vector[Int] = heldData.readTraceImpl(channel, range, segment)
  override def readFrameImpl(frame: Int, segment: Int): Vector[Int] = heldData.readFrameImpl(frame, segment)
  override def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int): Vector[Int] = heldData.readFrameImpl(frame, channels, segment)

  override def absUnit: String = heldData.absUnit
  override def absOffset: Double = heldData.absOffset
  override def absGain: Double = heldData.absGain

  override def sampleRate: Double = heldData.sampleRate
  override def segmentEndTSs: scala.Vector[Long] = heldData.segmentEndTSs
  override def segmentStartTSs: scala.Vector[Long] = heldData.segmentStartTSs
  override def segmentLengths: scala.Vector[Int] = heldData.segmentLengths
  override def segmentCount: Int = heldData.segmentCount

  override def isCompatible(target: X) = false
  override def :::(target: X): XData = {
    throw new IllegalArgumentException("cannot append an XDataFilterHolder or child!")
  }

}
