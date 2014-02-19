import breeze.linalg.DenseVector
import breeze.macros.expand
import com.typesafe.scalalogging.slf4j.Logger
import nounou.data.XData
import scala.math
import scala.reflect.runtime.universe._

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

  // <editor-fold defaultstate="collapsed" desc=" Some Scala Implicit Pimps ">

    def toLong(vect: Vector[Int]): Vector[Long] = {
      val tempArr = new Array[Long](vect.length)
      for( c <- 0 until tempArr.length ){
          tempArr(c) = Int.int2long(vect(c))
      }
      tempArr.toVector
  }

  def toInt(vect: Vector[Long]): Vector[Int] = {
    val tempArr = new Array[Int](vect.length)
    for( c <- 0 until tempArr.length ){
      tempArr(c) = vect(c).asInstanceOf[Int]
    }
    tempArr.toVector
  }

  @throws[IllegalArgumentException]
  def loggerError(logger: Logger, message: String, params: AnyRef*): Unit = {
    logger.error(message, params: _*)
    throw new IllegalArgumentException(message)
  }

  @throws[IllegalArgumentException]
  def loggerRequire(logger: Logger, boolean: Boolean, message: String, params: AnyRef*): Unit = {
    if(!boolean){
        logger.error(message, params: _* )
        throw new IllegalArgumentException( "require:" +message)
    }
  }

  //def toArrayInt(vect: Vector[Int]): Array[Int] = vect.toArray
}
