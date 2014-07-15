package nounou.ranges

//import nounou.analysis.units.OptThresholdPeakDetectWindow
import nounou.{OptNull, Opt, OptStep, LoggingExt}
import nounou.data.{XFrame, XData}
import nounou.data.traits.XFrames

//TODO 1: Should really streamline this code and test better, but it is a minefield!

abstract class RangeFrSpecifier extends LoggingExt {
  def segment(): Int

  @deprecated
  def getRangeFr(x: XFrames): RangeFr = getRangeFr( x.segmentLength(segment) )
  def getRangeFr(totalLength: Int): RangeFr
  def getValidRange(x: XFrames): Range.Inclusive = getValidRange(x.segmentLength(segment))
  def getValidRange(totalLength: Int): Range.Inclusive
}

// <editor-fold defaultstate="collapsed" desc=" RangeFrAll ">

object RangeFrAll extends LoggingExt {

  final def apply(): RangeFrAll = new RangeFrAll(0, OptStep(1))
  @deprecated
  final def apply(step: Int): RangeFrAll = new RangeFrAll(0, OptStep(step))
  @deprecated
  final def apply(step: Int, segment: Int): RangeFrAll = new RangeFrAll(segment, OptStep(step))
}

class RangeFrAll(val segment: Int, val opts: Opt*) extends RangeFrSpecifier {

  private var optStep = 1
  def step = optStep
  // <editor-fold defaultstate="collapsed" desc=" Handle options ">

  for( opt <- opts ) opt match {
    case OptStep( fr ) => optStep = fr
    case _ => {}
  }

  // </editor-fold>

  def this(segment: Int) = this(segment, OptNull)

  override def getRangeFr(totalLength: Int): RangeFr = RangeFr( XFrame(0, segment), XFrame(totalLength-1, segment), opts:_* )

  override def getValidRange(totalLength: Int): Range.Inclusive = getRangeFr(totalLength).getValidRange(totalLength)
}

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc=" RangeFr ">

/**
 * @author ktakagaki
 * @date 2/9/14.
 */
object RangeFr {

  final def apply(xFrameStart: XFrame, xFrameEnd: XFrame, opts: Opt*) = new RangeFr(xFrameStart, xFrameEnd, opts:_*)

  @deprecated
  final def apply(start: Int, endMarker: Int, step: Int, segment: Int) = new RangeFr(start, endMarker, step, segment)
  @deprecated
  final def apply(start: Int, endMarker: Int, step: Int) = new RangeFr(start, endMarker, step, segment = 0)
  @deprecated
  final def apply(start: Int, endMarker: Int) = new RangeFr(start, endMarker, 1, segment = 0)

}

class RangeFr(val xFrameStart: XFrame, val xFrameEnd: XFrame, opts: Opt*)
  extends RangeFrSpecifier with LoggingExt {

  private var optStep = 1
  def step = optStep
  // <editor-fold defaultstate="collapsed" desc=" Handle options ">

  for( opt <- opts ) opt match {
    case OptStep( fr ) => optStep = fr
    case _ => {}
  }

  // </editor-fold>

  @deprecated
  def this(start: Int, endMarker: Int, step: Int = 1, segment: Int/* = 0*/, isAll: Boolean = false) =
    this( XFrame(start, segment), XFrame(endMarker, segment), OptStep(step) )

  val segment = xFrameStart.segment
  val start = xFrameStart.frame
  val end = xFrameEnd.frame

  loggerRequire( start <= end,
    "RangeFr requires start <= last. start={}, end={}", start, end)
  loggerRequire( xFrameStart.segment == xFrameEnd.segment,
    "RangeFr cannot span segments. start segment={}, end segment={}", xFrameStart.segment, xFrameStart.segment)

  override def toString() = "RangeFr(" + xFrameStart + ", " + xFrameEnd+ ", " + step + ")"

  // <editor-fold defaultstate="collapsed" desc=" length/last, using a buffered Range.Inclusive ">

  //private def getSamplesFromLength(len: Int) = (len -1)/step + 1
  private var buffRangeInclusive = new Range.Inclusive(0,0,1)
  private var buffRangeInclusiveLength = -1
  private def buffRefresh(totalLength: Int) = {
    if( totalLength != buffRangeInclusiveLength ) {
//      buffRangeInclusive = if(isAll) {
//        new Range.Inclusive(start, totalLength-1, step)
//      } else {
        new Range.Inclusive(start, end, step)//scala.math.min(endMarker, totalLength-1), step)
//      }
      buffRangeInclusiveLength = totalLength
    }
  }

  /**range length*/
  def length(totalLength: Int): Int = {
    buffRefresh(totalLength)
    buffRangeInclusive.length
//    if(isAll || start >= vectDataLen) getSamplesFromLength( vectDataLen )
//    else getSamplesFromLength(endMarker - start + 1)//(endMarker - start /*+ 1 - 1*/)/stepMs  + 1
  }
  /**range length*/
  def length(data: XData): Int = length( data.segmentLength(start) )

  /**range last*/
  def last(data: XData): Int = last(data.segmentLength(segment))

  /** Inclusive last frame, taking into account step and overhang
    * @param totalLength full length of this segment in frames, used to realize with RangeFr.all()
    */
  def last(totalLength: Int): Int = {
    buffRefresh(totalLength)
    buffRangeInclusive.last
//    if(isAll) totalLength/step*step -1  //start + (length(totalLength) - 1 ) * step
//    else {
//      start + (endMarker-start+1)/step*step
//    }

  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" isFullyValid/lastValid ">

  /** Whether the frame range is completely contained within available data.
    * @param xFrames data object to which to apply the frames
    */
  def isFullyValid(xFrames: XFrames): Boolean = isFullyValid( xFrames.segmentLength(segment) )
  /** Whether the frame range is completely contained within available data.
    * @param totalLength full length of this segment in frames, used to realize with RangeFr.all()
    */
  def isFullyValid(totalLength: Int): Boolean = {
//    if(isAll) true
//    else
      start >= 0 && last(totalLength) < totalLength
  }

  /** Valid last frame, taking into account step and overhang
    * @param totalLength full length of this segment in frames, used to realize with RangeFr.all()
    */
  def lastValid(totalLength: Int): Int = {
//    if(isAll){
//      last(totalLength)
//    } else {
    buffRefresh(totalLength)
    buffRangeInclusive.last
//      val realEnd = scala.math.min(totalLength - 1, end)
//      if (start <= realEnd) {
//        //      val tempLast = last(totalLength)
//        //      if(tempLast < totalLength) tempLast
//        //      else (totalLength - start - 1)/step*step + 1 + start //(new Range.Inclusive(start, totalLength-1, step)).last        //ToDo 3: make into more streamlined code
//        val tempRange = new Range.Inclusive(start, realEnd, step) //ToDo 5: streamline this to not use Range.Inclusive
//        tempRange.last
//      } else {
//        -1 //give errorif start>realEnd
//      }
//    }
  }

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" getRange/getValidRange (conversion to Range.Inclusive) ">

//  /** Get a [[Range.Inclusive]] taking into account length and stepMs, so that the start and last are exactly present values
//    * @param totalLength full length of this segment in frames, used to realize with RangeFr.all()
//    */
//  def getRange(totalLength: Int): Range.Inclusive =
//    if(isAll) new Range.Inclusive(0, last(totalLength), step)
//    else new Range.Inclusive(start, endMarker, step)

  /** Get a [[Range.Inclusive]] which fits inside the given data vector length, and takes into account length and stepMs,
    * so that the start and last are exactly present values.
    * @param totalLength full length of this segment in frames, used to realize with RangeFr.all()
    */
  override def getValidRange(totalLength: Int): Range.Inclusive = {
    new Range.Inclusive(start, last(totalLength), step)
//
//    if(isAll) {
//      //full range
//      new Range.Inclusive(0, last(totalLength), step)
//    } else if(start >= totalLength ) {
//      //range starts after final data value
//      new Range.Inclusive(0, -1, 1)// range with length zero
//    } else if(start >= 0 ) {
//      //range starts within data
//      new Range.Inclusive(start, lastValid(totalLength), step)
//    } else {
//      //range starts in negative range
//      val realStart =
//        //if(start<0){
//          start + ((- start - 1)/step + 1 ) * step
//        //} else { start }
//      new Range.Inclusive(realStart, lastValid(totalLength), step)
//    }

  }

//  def getValidRangeFr(xFrames: XFrames): RangeFr =  getValidRangeFr(xFrames.segmentLength(segment))
//
//  def getValidRangeFr(totalLength: Int): RangeFr = {
//
//    if(isAll) {
//      //full range
//      RangeFr(0, last(totalLength), step)
//    } else if(start >= totalLength ) {
//      //range starts after final data value
//      RangeFr(0, -1, 1)// range with length zero   //ToDo 1: This will throw error!
//    } else if(start >= 0 ) {
//      //range starts within data
//      RangeFr(start, lastValid(totalLength), step)
//    } else {
//      //range starts in negative range
//      val realStart =
//      //if(start<0){
//        start + ((- start - 1)/step + 1 ) * step
//      //} else { start }
//      RangeFr(realStart, lastValid(totalLength), step)
//    }
//
//  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" How much padding? ">

  def preLength(totalLength: Int): Int = {
    if( /*isAll ||*/ start >= 0 ) 0    //all post padding or no padding
    else if ( totalLength > - start ) {   //pre padding from start to -1
      (-start-1)/step + 1
      //getSamplesFromLength( - start )
    } else {                                //all pre padding
      (totalLength-1)/step + 1
      //getSamplesFromLength( vectDataLen )
    }
  }

  def postLength(totalLength: Int): Int = {
    val lastV = lastValid(totalLength)
    if( lastV < 0 ){   //start>realEnd, all post padding
      //println( endMarker + " " + lastV + " " + step)
      //(endMarker - lastV)/step
      (end - start )/step + 1
    } //else if( isAll ){                            //no post padding
      //0
    //}
    else {
      //println( endMarker + " " + lastV + " " + step)
      ( end - lastV )/step /* + 1 - 1*/
      // + step //scala.math.min(totalLength-1, endMarker)
//      if( endMarker <= lastV ) 0     //no padding
//      else (endMarker - lastV /* + 1 - 1*/)/step
    }
  }
  // </editor-fold>

  /**Will return self, this is in order to comply with [[RangeFrSpecifier]]
   */
  override def getRangeFr(x: XFrames): RangeFr = this

}

