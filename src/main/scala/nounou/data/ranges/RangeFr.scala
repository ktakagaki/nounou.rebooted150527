package nounou.data.ranges

//import nounou.analysis.units.OptThresholdPeakDetectWindow
import nounou._
import nounou.util.LoggingExt
import nounou.data.{Frame, XData}
import nounou.data.traits.XFrames


abstract class RangeFrSpecifier extends LoggingExt {
  def segment(): Int

  def getRangeFr(x: XFrames): RangeFr// = getRangeFr( x.segmentLength(segment) )
  //def getRangeFr(totalLength: Int): RangeFr
  def getValidRange(x: XFrames): Range.Inclusive = getRangeFr(x).getValidRange(x)
  //def getValidRange(totalLength: Int): Range.Inclusive
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

  override def getRangeFr(xFrames: XFrames): RangeFr = RangeFr( Frame(0, segment), Frame(xFrames.segmentLength(segment), segment), opts:_* )
}

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc=" RangeFr ">

/**
 * @author ktakagaki
 * @date 2/9/14.
 */
object RangeFr {

  final def apply(xFrameStart: Frame, xFrameEnd: Frame, opts: Opt*) = new RangeFr(xFrameStart, xFrameEnd, opts:_*)

  @deprecated
  final def apply(start: Int, endMarker: Int, step: Int, segment: Int) = new RangeFr(start, endMarker, step, segment)
  @deprecated
  final def apply(start: Int, endMarker: Int, step: Int) = new RangeFr(Frame(start, 0), Frame(endMarker, 0), OptStep(step) )
  @deprecated
  final def apply(start: Int, endMarker: Int) = new RangeFr(start, endMarker, 1, segment = 0)

}

class RangeFr(val xFrameStart: Frame, val xFrameEnd: Frame, opts: Opt*)
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
    this( Frame(start, segment), Frame(endMarker, segment), OptStep(step) )

  val segment = xFrameStart.segment
  val start = xFrameStart.frame
  val end = xFrameEnd.frame

  loggerRequire( start <= end,
    "RangeFr requires start <= lastValid. start={}, end={}", start.toString, end.toString)
  loggerRequire( xFrameStart.segment == xFrameEnd.segment,
    "RangeFr cannot span segments. start segment={}, end segment={}", xFrameStart.segment.toString, xFrameStart.segment.toString)

  override def toString() = "RangeFr(" + xFrameStart + ", " + xFrameEnd+ ", " + step + ")"

  // <editor-fold defaultstate="collapsed" desc=" utility functions: intervalContains/intervalMod ">

  protected[ranges] def intervalContains(start: Int, end: Int, step: Int): Int = {
    if (start > end) 0
    else if (start == end) 1
    else (end - start)/step + 1
  }

  /** How many units to
    * = add to the end (when counting from start) or
    * = to subtract from the start (when counting backwards from end)
    * to get to the next step value
    */
  protected[ranges] def intervalMod(start: Int, end: Int, step: Int): Int = {
    if (start > end) Int.MinValue
    else if (start == end) step
    else intervalContains(start, end, step) * step - (end - start)
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" first/lastValid ">

  /** Inclusive last valid frame, taking into account step and overhang
    */
  def firstValid(data: XFrames): Int = firstValid(data.segmentLength(segment))
  def firstValid(totalLength: Int) = {
    if(start >= totalLength ) Int.MaxValue //no valid values
    else if( start >= 0 ) start
    else if( step == 1 ) 0
    else {
      val temp = intervalMod(start, -1, step) - 1
      if( temp < totalLength ) temp
      else Int.MaxValue //no valid values
    }
  }
  /** Inclusive last valid frame, taking into account step and overhang
    */
  def lastValid(data: XFrames): Int = lastValid(data.segmentLength(segment))
  /** Valid lastValid frame, taking into account step and overhang
    * @param totalLength full length of this segment in frames, used to realize with RangeFr.all()
    */
  def lastValid(totalLength: Int) = {
    if(end < 0 ) Int.MinValue //no valid values
    else if( 0 == end ) {
      val temp = firstValid(totalLength)
      if( temp == 0 ) 0
      else Int.MinValue //no valid values
    }
    else if( /*0 < end*/ end < totalLength ) {
      val fv = firstValid(totalLength)
      if (fv == end) fv
      else {
        val temp = intervalContains(fv, end, step)
        if (temp > 0) fv + (temp - 1) * step
        else Int.MinValue //no valid values
      }
    }
    else { //totalLength <= end
      if( step == 1 ) totalLength - 1
      else {
        val fv = firstValid(totalLength)
        val temp = intervalContains(fv, totalLength - 1, step)
        if (temp > 0) fv + (temp - 1) * step
        else Int.MinValue //no valid values
      }
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" length ">

  /** range length, can include zero padding if start<0 or totalLength<=end
    */
  def length(totalLength: Int): Int = intervalContains(start, end, step)

  /**range length, can include zero padding if start<0 or totalLength<=end
    */
  def length(data: XFrames): Int = length( data.segmentLength(start) )

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" preLength/postLength ">

  def preLength(totalLength: Int): Int = {
    if( start >= 0 ) 0    //all post padding or no padding
    else if (end < 0) { //all pre padding
      (end - start)/step + 1
    } else{
      intervalContains(start, -1, step)
    }
  }

  def postLength(totalLength: Int): Int = {
    if( start >= totalLength )  (end - start)/step + 1   //all post padding
    else if (end < totalLength) 0   //all pre padding or no padding
    else{
      intervalContains(lastValid(totalLength), end, step) - 1
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" isFullyValid ">

  /** Whether the frame range is completely contained within available data.
    * @param xFrames data object to which to apply the frames
    */
  def isFullyValid(xFrames: XFrames): Boolean = isFullyValid( xFrames.segmentLength(segment) )

  /** Whether the frame range is completely contained within available data.
    * @param totalLength full length of this segment in frames, used to realize with RangeFr.all()
    */
  def isFullyValid(totalLength: Int): Boolean = {
      start >= 0 && lastValid(totalLength) < totalLength
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" getValidRange (conversion to Range.Inclusive) ">

  /** Get a [[Range.Inclusive]] which fits inside the given data vector length, and takes into account length and stepMs,
    * so that the start and lastValid are exactly present values.
    */
  def getValidRange(totalLength: Int): Range.Inclusive = {
    new Range.Inclusive( firstValid(totalLength), lastValid(totalLength), step)
  }
  override def getValidRange(xFrames: XFrames): Range.Inclusive = getValidRange(xFrames.segmentLength( segment ))

  // </editor-fold>

  /**Will return self, this is in order to comply with [[RangeFrSpecifier]]
   */
  def getRangeFr(xFrames: XFrames): RangeFr = this
  def getRangeFr(totalLength: Int): RangeFr = this


}

// </editor-fold>
