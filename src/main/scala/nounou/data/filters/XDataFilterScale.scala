//package nounou.data.filters
//
//import breeze.linalg.{DenseVector => DV, mean}
//import nounou.data.{XData}
//import nounou.FrameRange
//
///** A passthrough object, which is overriden and inherited with various XDataFilterTr traits to create a filter block.
//  */
//class XDataFilterScale( override val upstream: XData ) extends XDataFilter(upstream) {
//
//  private var _fixedScale = false
//  def fixScale(range: FrameRange) = fixScale(range, 0)
//  def fixScale(range: FrameRange, seg: Int): Unit = {
//    fixRange = range.getValidRange(this.segmentLengths(seg))
//    fixSegment = seg
//
//  }
//  def unfixScale() = {
//    _fixedScale = true
//  }
//
//  /** Range for which the scaling is fixed, when _fixedScale is true
//    */
//  private var fixRange: FrameRange = null
//  private var fixSegment: Int = 0
//  private val fixedScale: Array[Boolean] = Array.tabulate[Boolean](channelCount)(p => false)
//  private val fixOffset: DV[Int] = DV.zeros[Int](channelCount)
//  private val fixGain: DV[Int] = DV.zeros[Int](channelCount)
//  private def fixScaleChannel(ch: Int, ) = {
//    if(!fixedScale(ch)){
//      val trace = readTrace(ch, fixRange, fixSegment)
//      val tempMean = mean( trace )
//      val (tempMin, tempMax) = minMax( trace )
//
//      fixedScale(ch) = true
//    }
//  }
//
//  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = {
////    logger.warn("XDataFilterScale should not be used via reading single points... only use via trace methods!")
////    upstream.readPointImpl(channel, frame, segment)
//  }
//  override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): DV[Int] = {
//    if(_fixedScale){
//
//
//    } else {
//
//    }
//  }
//
//  override def absUnit: String = "scaled"
//  override def absOffset: Double = upstream.absOffset
//  override def absGain: Double = upstream.absGain
//
//  private def minMax(v: DV[Int]): (Int, Int) = {
//    var min = Int.MaxValue
//    var max = Int.MinValue
//    var cnt = 0
//    while( cnt < v.length ){
//      min = scala.math.min(min, v(cnt))
//      max = scala.math.max(max, v(cnt))
//      cnt += 1
//    }
//    (min, max)
//  }
//
//}
