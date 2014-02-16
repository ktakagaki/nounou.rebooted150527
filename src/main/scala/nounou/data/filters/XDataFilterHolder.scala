package nounou.data.filters

import nounou.data.{XDataNull, X, XData}

/**This class serves as an "immutable" reference point for various data structures, which may change.
  *The `upstream` variable is not valid in the usual sense, and everything is redirected to the
  * variable `realData`. Overwrite `realData` to change the output of this class.
 * @author ktakagaki
 * @date 2/15/14.
  */
class XDataFilterHolder( val upstream: XData = XDataNull ) extends XData {

  private var _realData: XData = XDataNull
  def realData: XData = _realData
  def realData_=( newData: XData ) = {
    _realData = newData
    changedData()
    changedTiming()
  }

  override def channelNames: scala.Vector[String] = realData.channelNames

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = realData.readPointImpl(channel, frame, segment)
  override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): Vector[Int] = realData.readTraceImpl(channel, range, segment)
  override def readFrameImpl(frame: Int, segment: Int): Vector[Int] = realData.readFrameImpl(frame, segment)
  override def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int): Vector[Int] = realData.readFrameImpl(frame, channels, segment)

  override def absUnit: String = realData.absUnit
  override def absOffset: Double = realData.absOffset
  override def absGain: Double = realData.absGain

  override def sampleRate: Double = realData.sampleRate
  override def segmentEndTSs: scala.Vector[Long] = realData.segmentEndTSs
  override def segmentStartTSs: scala.Vector[Long] = realData.segmentStartTSs
  override def segmentLengths: scala.Vector[Int] = realData.segmentLengths
  override def segmentCount: Int = realData.segmentCount

  override def isCompatible(target: X) = false
  override def :::(target: X): XData = {
    throw new IllegalArgumentException("cannot append an XDataFilterHolder or child!")
  }

}
