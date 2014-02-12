import breeze.linalg.DenseVector
import breeze.macros.expand
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

//  Doesn't really work due to implicit definitions of the relevant functions, such as convolve
//  @expand
//  implicit def vectorToDenseVector[@expand.args(Int, Double, Long, Float) T]( vector: Vector[T] ): DenseVector[T] = {
//    DenseVector[T]( vector.toArray )
//  }

  implicit def rangeInclusiveToFrameRange(range: Range.Inclusive): FrameRange = {
    //require( range.isInclusive, "Exclusive indexing with 'until' is not permitted in nounou, use inclusive 'to'!" )
    require( range.step > 0, "Only positive steps are allowed for indexing in nounou!" )
    require( range.start <= range.end, "In nounous, start <= last is required for frame ranges. start=" + range.start + ", last=" + range.end)

    //println( "1 " + new FrameRange(range.start, range.end, range.step, false) )
    new FrameRange(range.start, range.end, range.step, false)

  }


  def forJava(start: Int, endExclusive: Int, step: Int, function: (Int => Unit) ): Unit = {
    var count = start
    if( step>0 ) while( count < endExclusive){
      function(count)
      count = count + step
    } else if (step<0) while( count > endExclusive){
      function(count)
      count = count + step
    } else throw new IllegalArgumentException
  }
}
