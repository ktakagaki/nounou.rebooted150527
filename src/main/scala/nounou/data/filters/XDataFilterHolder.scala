package nounou.data.filters

import nounou.data._
import breeze.linalg.{DenseVector => DV}

/**This class serves as an "immutable" reference point for various data structures, which may change.
  *The `upstream` variable is not valid in the usual sense, and everything is redirected to the
  * variable `heldData`. Overwrite `heldData` to change the output of this class.
 * @author ktakagaki
 * @date 2/15/14.
  */
class XDataFilterHolder extends XData with XDataAux {

  private var _heldData: XData = XDataNull
  def heldData: XData = _heldData
  def heldData_=( newData: XData ) = {
    heldData.clearChildren()
    _heldData = newData
      heldData.clearChildren()
      heldData.setChildren( super.getChildren() )

    changedData()
    changedTiming()  //ToDo 3: buffer and only conditionally trigger timing change
  }
  def getHeldData = heldData
  def setHeldData( newData: XData ) = heldData_=(newData)

  //ToDo 2: Go through all classes, check channelCount. Fix dependency on layout
  override def channelCount() = _heldData.channelCount

  override def getChildren() = {
    heldData.getChildren() ++ super.getChildren()
  }
  override def setChild(x: XData) = {
    super.setChild(x)
    heldData.setChild(x)
  }
  override def clearChildren(): Unit = {
    super.clearChildren()
    heldData.clearChildren()
  }
  override def clearChild(x: XData): Unit = {
    super.clearChild(x)
    heldData.clearChild(x)
  }


  override def toString() = {
    "XDataFilterHolder: " + heldData.toString()
  }


//  override def channelNames: scala.Vector[String] = heldData.channelNames

  override def readPointImpl(channel: Int, frame: Int/*, segment: Int*/): Int = heldData.readPointImpl(channel, frame)//, segment)
  override def readTraceImpl(channel: Int, range: Range.Inclusive/*, segment: Int*/): DV[Int] = heldData.readTraceImpl(channel, range)//, segment)
  override def readFrameImpl(frame: Int/*, segment: Int*/): DV[Int] = heldData.readFrameImpl(frame)//, segment)
  override def readFrameImpl(frame: Int, channels: Vector[Int]/*, segment: Int*/): DV[Int] = heldData.readFrameImpl(frame, channels)//, segment)

  override def absUnit: String = heldData.absUnit
  override def absOffset: Double = heldData.absOffset
  override def absGain: Double = heldData.absGain
  override def scaleMax: Int = heldData.scaleMax
  override def scaleMin: Int = heldData.scaleMin

  override def sampleRate: Double = heldData.sampleRate
  override def segmentEndTs: scala.Vector[Long] = heldData.segmentEndTs
  override def segmentStartTs: scala.Vector[Long] = heldData.segmentStartTs
  override def segmentLength: scala.Vector[Int] = heldData.segmentLength
  override def segmentCount: Int = heldData.segmentCount

  override def layout: XLayout = heldData.layout()

  override def isCompatible(target: X) = false
  override def :::(target: X): XData = {
    throw new IllegalArgumentException("cannot append an XDataFilterHolder or child!")
  }

}
