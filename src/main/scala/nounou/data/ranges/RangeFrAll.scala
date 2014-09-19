package nounou.data.ranges

//import nounou.data.Frame

import nounou._
import nounou.data.traits.XFrames
import nounou.util.LoggingExt

object RangeFrAll extends LoggingExt {

  final def apply(step: Int, optSegment: OptSegment) = new RangeFrAll(step, optSegment)
  final def apply(optSegment: OptSegment) = new RangeFrAll(optSegment)
  final def apply(step: Int) = new RangeFrAll(step)
  final def apply(): RangeFrAll = new RangeFrAll()

}

class RangeFrAll(val step: Int, val optSegment: OptSegment) extends RangeFrSpecifier {

  override val getSegment = optSegment.segment
  override val getOptSegment = optSegment
  override def getRealSegment(xFrames: XFrames) = getOptSegment.getRealSegment(xFrames)
  override def getRealStepFrames(totalLength: Int) = step

  def this(step: Int) = this(step, OptSegmentAutomatic)
  def this(optSegment: OptSegment) = this(1, optSegment)
  def this() = this(1, OptSegmentAutomatic)

  override def getRangeFr(xFrames: XFrames): RangeFr = {
        RangeFr( 0, xFrames.segmentLength(getRealSegment(xFrames)), step, OptSegment(getRealSegment(xFrames)) )
  }

}

