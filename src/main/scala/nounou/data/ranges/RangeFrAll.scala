package nounou.data.ranges

//import nounou.data.Frame

import nounou._
import nounou.data.traits.XDataTiming
import nounou.util.LoggingExt

object RangeFrAll extends LoggingExt {

//  final def apply(step: Int, segment: Int) = new RangeFrAll(step, segment)
  final def apply(step: Int, optSegment: OptSegment) = new RangeFrAll(step, optSegment)
//  final def apply(optSegment: OptSegment) = new RangeFrAll(optSegment)
// deprecated due to potential confusion with this(segment: Int)
//  final def apply(step: Int) = new RangeFrAll(step)
  final def apply(): RangeFrAll = new RangeFrAll()

}

class RangeFrAll(val step: Int, val optSegment: OptSegment) extends RangeFrSpecifier {

  override def toString() = s"RangeFrAll(step=$step, segment=$optSegment)"
  override final def getRealSegment(xDataTiming: XDataTiming) = optSegment.getRealSegment(xDataTiming)

  override final def getRealStep(xFrames: XDataTiming): Int = {
    if ( step == -1 ) 1 else step
  }
  override final def getRangeFrReal(xFrames: XDataTiming): Range.Inclusive = {
    Range.inclusive( 0, xFrames.segmentLength(getRealSegment(xFrames)), getRealStep(xFrames))
  }
  override final def getRangeFrValid(xFrames: XDataTiming): Range.Inclusive = {
    val realSegment = RangeFr(0, xFrames.segmentLength(getRealSegment(xFrames)), step)
    realSegment.getRangeFrValid(xFrames)
  }
//  override val getSegment = optSegment.segment
//  override val getOptSegment = optSegment
//  override def getRealSegment(xFrames: XFrames) = getOptSegment.getRealSegment(xFrames)
//  override def getRealStep(totalLength: Int) = step

// deprecated due to potential confusion with this(segment: Int)
//  def this(step: Int) = this(step, OptSegmentAutomatic)
  def this(optSegment: OptSegment) = this(1, optSegment)
  def this() = this(1, OptSegmentAutomatic)

//  override def getRangeFr(xFrames: XFrames): RangeFr = {
//        RangeFr( 0, xFrames.segmentLength(getRealSegment(xFrames)), step, OptSegment(getRealSegment(xFrames)) )
//  }

}

