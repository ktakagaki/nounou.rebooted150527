package nounou.data.ranges

//import nounou.data.Frame
import nounou.data.traits.XFrames
import nounou.util.LoggingExt

object RangeFrAll extends LoggingExt {

//  final def apply(step: Int, optSegment: OptSegment) = new RangeFrAll(step, optSegment)
//  final def apply(optSegment: OptSegment) = new RangeFrAll(optSegment)
  final def apply(step: Int) = new RangeFrAll(step)
  final def apply(): RangeFrAll = new RangeFrAll()

  //  @deprecated
//  final def apply(step: Int): RangeFrAll = new RangeFrAll(0, OptStep(step))

//  @deprecated
//  final def apply(step: Int, segment: Int): RangeFrAll = new RangeFrAll(segment, OptStep(step))

}

class RangeFrAll(val step: Int/*, val optSegment: OptSegment*/) extends RangeFrSpecifier {

//  val segment = optSegment.segment

//  def this(step: Int) = this(step, OptSegmentNone)
//  def this(optSegment: OptSegment) = this(1, optSegment)
  def this() = this(1)//,OptSegmentNone)

//  def this(segment: Int) = this(segment, OptNull)

  override def getRangeFr(xFrames: XFrames): RangeFr = {
//    segment match {
//      case -1 => {
//        loggerRequire(xFrames.segmentCount==1, "RangeFrAll was specified without a segment. Only single-segment data can be specified in this way.")
//        RangeFr( 0, xFrames.segmentLength(0), step, OptSegment(0) )
//      }
//      case _ =>
        RangeFr( 0, xFrames.length /*segmentLength(segment)*/, step)//, optSegment )
//    }
  }

}

