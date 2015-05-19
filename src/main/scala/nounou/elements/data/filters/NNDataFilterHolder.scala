//package nounou.elements.data.filters
//
//import nounou.elements.NNElement
//import nounou.elements.ranges.SampleRangeValid
//import nounou.elements.data.{NNDataNull, NNDataAux, NNData}
//import nounou.elements.layouts.NNDataLayout
//import breeze.linalg.{DenseVector => DV}
//
///**This class serves as an "immutable" reference point for various data structures, which may change.
//  *The `_parent` variable is not valid in the usual sense, and everything is redirected to the
//  * variable `heldData`. Overwrite `heldData` to change the output of this class.
// * @author ktakagaki
// * //@date 2/15/14.
//  */
//class NNDataFilterHolder extends NNData with NNDataAux {
//
//  private var _heldData: NNData = NNDataNull
//  def heldData: NNData = _heldData
//  def heldData_=( newData: NNData ) = {
//    //heldData.clearChildren()
//    _heldData = newData
////      heldData.clearChildren()
////      heldData.setChildren( super.getChildren() )
//
//    changedData()
//    changedTiming()  //ToDo 3: buffer and only conditionally trigger timing change
//  }
//  def getHeldData = heldData
//  def setHeldData( newData: NNData ) = heldData_=(newData)
//  def clearHeldData = setHeldData( NNDataNull )
//
//  //ToDo 2: Go through all classes, check channelCount. Fix dependency on layout
//  override def channelCount() = _heldData.channelCount
//
////  override def getChildren() = {
////    heldData.getChildren() ++ super.getChildren()
////  }
////  override def setChild(x: XData) = {
////    super.setChild(x)
////    heldData.setChild(x)
////  }
////  override def clearChildren(): Unit = {
////    super.clearChildren()
////    heldData.clearChildren()
////  }
////  override def clearChild(x: XData): Unit = {
////    super.clearChild(x)
////    heldData.clearChild(x)
////  }
//
//  override def toString() = {
//    "XDataFilterHolder: " + heldData.toString()
//  }
//
//
////  override def channelNames: scala.Vector[String] = heldData.channelNames
//
//  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int =
//    heldData.readPointImpl(channel, frame, segment)
//  override def readTraceDVImpl(channel: Int, range: SampleRangeValid): DV[Int] =
//    heldData.readTraceDVImpl(channel, range)
////  override def readFrameImpl(frame: Int): DV[Int] =
////    heldData.readFrameImpl(frame)
////  override def readFrameImpl(frame: Int, channels: Array[Int]): DV[Int] =
////    heldData.readFrameImpl(frame, channels)
//
//  override def absUnit: String =   heldData.absUnit
//  override def absOffset: Double = heldData.absOffset
//  override def absGain: Double =   heldData.absGain
//  override def scaleMax: Int =     heldData.scaleMax
//  override def scaleMin: Int =     heldData.scaleMin
//
//  override def sampleRate: Double =                 heldData.sampleRate
//  override def segmentEndTs: Array[Long] =   heldData.segmentEndTs
//  override def segmentStartTs: Array[Long] = heldData.segmentStartTs
//  override def segmentLengthImpl(segment: Int): Int =   heldData.segmentLengthImpl(segment)
//  override def segmentCount: Int =                  heldData.segmentCount
//
////  override def layout: NNLayout = heldData.layout()
//
//  override def isCompatible(target: NNElement) = false
//  override def :::(target: NNElement): NNData = {
//    throw loggerError("cannot append an XDataFilterHolder or child!")
//  }
//
//}
