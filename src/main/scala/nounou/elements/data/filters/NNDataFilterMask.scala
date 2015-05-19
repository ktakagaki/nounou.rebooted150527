//package scala.nounou.obj.data.filters
//
//import _root_.nounou.obj.ranges.SampleRangeValid
//import nounou.obj.data.NNData
//import nounou.obj.XSampleMask
//import breeze.linalg.{DenseVector => DV}
//
///**
// * @author ktakagaki
// * //@date 2/19/14.
// */
//class NNDataFilterMask(private var _parent: NNData, private var initialMask: XSampleMask = new XSampleMask()) extends NNDataFilter(_parent) {
//
//  override def toString() = "XDataFilterMask: " + mask.toString()
//  private var _mask: XSampleMask = initialMask
//
//  def mask(): XSampleMask = _mask
//  def mask_=(mask: XSampleMask): Unit = {
//    _mask = mask
//    changedData()
//  }
//  def setMask(mask: XSampleMask) = {
//    mask_=(mask)
//  }
//  def getMask() = mask
//
//
//  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = {
//    if( mask.isMaskedFrame(frame, segment, _parent) ){
//      0
//    }else{
//      _parent.readPointImpl(channel, frame, segment)
//    }
//  }
//
//  //ToDo 1: what to do with partially masked frames?
//  override def readTraceImpl(channel: Int, range: SampleRangeValid): DV[Int] = {
//    if( mask.isMaskedFrame(range, _parent) ){
//      //ToDo 2: make zero array
//      super.readTraceImpl(channel, range)
//    } else {
//      _parent.readTraceImpl(channel, range)
//    }
//  }
//
//  }
