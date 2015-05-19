//package nounou.data.ranges
//
//import nounou._
//import nounou.data.traits.XFrames
//
///**For specifying data extraction range in Ms.
// * @author ktakagaki
// * //@date 2/17/14.
// */
//object RangeMs {
//  def apply(startMs: Double, endMs: Double, stepMs: Double, optSegment: OptSegment) = new RangeMs(startMs, endMs, stepMs, optSegment)
//  def apply(startMs: Double, endMs: Double, optSegment: OptSegment) = new RangeMs(startMs, endMs, optSegment)
//  def apply(startMs: Double, endMs: Double, stepMs: Double) = new RangeMs(startMs, endMs, stepMs)
//  def apply(startMs: Double, endMs: Double) = new RangeMs(startMs, endMs)
//
// // class All(step: Double) extends RangeMs(0, 0, step, true)
//}
//
//class RangeMs(val startMs: Double, val endMs: Double, val stepMs: Double, val optSegment: OptSegment) extends RangeFrSpecifier {
//
//  def segment() = optSegment.segment
//
//  def this(startMs: Double, endMs: Double, optSegment: OptSegment) = this(startMs, endMs, -1d, optSegment)
//  def this(startMs: Double, endMs: Double, stepMs: Double) = this(startMs, endMs, stepMs, OptSegmentAutomatic)
//  def this(startMs: Double, endMs: Double) = this(startMs, endMs, -1, OptSegmentAutomatic)
//
//  override def stepFrames(xFrames: XFrames) = {
//    val stepReal = if(stepMs == -1d) 1 else (stepMs * xFrames.sampleRate / 1000d).toInt
//    loggerRequire(stepReal>0, "This amounts to a negative timestep! (stepMs=" + stepMs + " ms => " + stepReal + " frames)")
//    stepReal
//  }
//
//  def getRangeFr(xFrames: XFrames): RangeFr = {
//
//      val startReal = xFrames.msToFr(startMs)
//      val endReal = xFrames.msToFr(endMs)
//
//      RangeFr(startReal, endReal, stepFrames(xFrames), optSegment)
//  }
//
//}
