import nounou.data.XData
import scala.math

/**
 * @author ktakagaki
 * @date 2/4/14.
 */
package object nounou {

  /**A range of time stamps for extracting data, events, and spikes.*/
  class RangeTS(val startTSInclusive: Long, val endTSInclusive: Long){

//    def this(val centerTS: Long, val framesPre: Int, val framesPost: Int )

//    def toSegmentAndRangeAndPadding( xd: XData ): (Int, Range, (Int, Int) ) = {
//      xd.tsToFrame()
//    }
  }

  object FrameRange {
    def All: FrameRange = All(1)
    def All(byVal: Int): FrameRange = new FrameRange(0, 0, byVal){
      override def preLength(totalLength: Int) = 0
      override def postLength(totalLength: Int) = 0
      override def validRange(totalLength: Int) = 0 to (totalLength -1) by byVal
    }
  }

  class FrameRange(start: Int, end: Int, step: Int) extends Range.Inclusive(start, end, step){

    def preLength(totalLength: Int): Int = {
      if ( start >= 0 ) 0
      else if ( start < - totalLength) totalLength //all pre padding
      else ( - start )
    }

    def postLength(totalLength: Int): Int = {
      if ( end < totalLength ) 0
      else if ( end > 2 * totalLength - 1 ) totalLength //all post padding
      else ( end - totalLength + 1 )
    }

    def validRange(totalLength: Int): Range.Inclusive = {
      val realStart = if (start < totalLength) math.max(0, start) else 0
      val realEnd = if (end < 0) 0 else if (start >= totalLength) 0 else math.min(totalLength -1, end)
      realStart to realEnd by step
    }

    def isFullRange(totalLength: Int): Boolean = (start < 0 || totalLength <= end)

  }

  implicit def rangeToFrameRange(range: Range): FrameRange = {
    require( range.isInclusive, "Exclusive indexing with 'until' is not permitted in nounou, use inclusive 'to'!" )
    require( range.step > 0, "Only positive steps are allowed for indexing in nounou!" )

    new FrameRange(range.step, range.end, range.step)
  }

}
