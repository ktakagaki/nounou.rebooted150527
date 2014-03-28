import com.typesafe.scalalogging.slf4j.Logging

/**
 * @author ktakagaki
 * @date 2/4/14.
 */
package object nounou extends Logging {

  // <editor-fold defaultstate="collapsed" desc=" Range related ">

  import nounou.ranges._


  implicit def rangeInclusiveToFrameRange(range: Range.Inclusive): RangeFr = rangeInclusiveToFrameRange(range, 0)
  implicit def rangeInclusiveToFrameRange(range: Range.Inclusive, segment: Int): RangeFr = {
    require( range.step > 0, "Only positive steps are allowed for indexing in nounou!" )
    require( range.start <= range.end, "In nounous, start <= last is required for frame ranges. start=" + range.start + ", last=" + range.end)

    RangeFr(range.start, range.end, range.step, segment)
  }

  // </editor-fold>


  // <editor-fold defaultstate="collapsed" desc=" forJava ">

  /** Provides a quick java-based for loop, avoiding Scala for-comprehensions
    *
    */
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

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" trait LoggingExt (loggerError, loggerRequire) ">

  trait LoggingExt extends Logging {

    def loggerError(message: String, params: AnyRef*): IllegalArgumentException = {
      logger.error(message, params: _*)
      new IllegalArgumentException(message)
    }

    @throws[IllegalArgumentException]
    def loggerRequire(boolean: Boolean, message: String, params: AnyRef*): Unit = {
      if(!boolean){
          logger.error(message, params: _* )
          throw new IllegalArgumentException( "require:" +message)
      }
    }

  }

  // </editor-fold>



//  case object None extends Opt
//  case class All() extends Opt
//
//  implicit val noneImplicit: None = OptSpikeDetectorFlush.None
//  implicit def noneExpandToOptSpikeDetectorFlush(none: nounou.None) = OptSpikeDetectorFlush.None


}

//  /**A range of time stamps for extracting data, events, and spikes.*/
//  class RangeTS(val startTSInclusive: Long, val endTSInclusive: Long){
//
////    def this(val centerTS: Long, val framesPre: Int, val framesPost: Int )
//
////    def toSegmentAndRangeAndPadding( xd: XData ): (Int, Range, (Int, Int) ) = {
////      xd.tsToFrame()
////    }
//  }

