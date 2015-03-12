package nounou.elements.ranges

//import nounou.data.Frame

import nounou._
import nounou.elements.traits.NNDataTiming
import nounou.util.LoggingExt

//convenience accessors all to NN
//object SampleRangeAll extends LoggingExt {
//
////  final def apply(segment: Int) = new RangeFrAll(segment)
//  final def apply(step: Int, segment: Int) = new SampleRangeAll(step, segment)
////  final def apply(step: Int, optSegment: OptSegment) = new SampleRangeAll(step, optSegment)
////  final def apply(optSegment: OptSegment) = new RangeFrAll(optSegment)
//// deprecated due to potential confusion with this(segment: Int)
////  final def apply(step: Int) = new RangeFrAll(step)
//  final def apply(): SampleRangeAll = new SampleRangeAll()
//
//}

//class SampleRangeAll(val step: Int, val optSegment: OptSegment) extends SampleRangeSpecifier {
class SampleRangeAll(val step: Int, val segment: Int) extends SampleRangeSpecifier {

  override def toString() = s"RangeFrAll(step=$step, segment=$segment)"
  override final def getRealSegment(xDataTiming: NNDataTiming) = xDataTiming.getRealSegment(segment)

  override final def getRealStep(xFrames: NNDataTiming): Int = {
    if ( step == -1 ) 1 else step
  }
  override final def getSampleRangeReal(xFrames: NNDataTiming): SampleRangeReal = {
    new SampleRangeValid( 0, xFrames.segmentLength(getRealSegment(xFrames)), getRealStep(xFrames), segment)
  }
  override final def getSampleRangeValid(xFrames: NNDataTiming): SampleRangeValid = getSampleRangeValid(xFrames)
  override final def getSampleRangeValidPrePost(xFrames: NNDataTiming): (Int, SampleRangeValid, Int) =
    (0, getSampleRangeValid(xFrames), 0)
//  override final def getSampleRangeReal(xFrames: XDataTiming): Range.Inclusive = {
//    Range.inclusive( 0, xFrames.segmentLength(getRealSegment(xFrames)), getRealStep(xFrames))
//  }
//  override final def getSampleRangeValid(xFrames: XDataTiming): Range.Inclusive = {
//    val realSegment = FrameRange(0, xFrames.segmentLength(getRealSegment(xFrames)), step)
//    realSegment.getFrameRangeValid(xFrames)
//  }
//  override val getSegment = optSegment.segment
//  override val getOptSegment = optSegment
//  override def getRealSegment(xFrames: XFrames) = getOptSegment.getRealSegment(xFrames)
//  override def getRealStep(totalLength: Int) = step

// deprecated due to potential confusion with this(segment: Int)
//  def this(step: Int) = this(step, OptSegmentAutomatic)
//  def this(optSegment: OptSegment) = this(1, optSegment)
//  def this() = this(1, OptSegmentAutomatic)

//  override def getRangeFr(xFrames: XFrames): RangeFr = {
//        RangeFr( 0, xFrames.segmentLength(getRealSegment(xFrames)), step, OptSegment(getRealSegment(xFrames)) )
//  }

}

