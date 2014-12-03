package nounou.data.ranges

import breeze.linalg.{max, min}
import nounou._
import nounou.util.LoggingExt
import nounou.data.traits.XDataTiming

// <editor-fold defaultstate="collapsed" desc=" RangeFr ">

/**
 * @author ktakagaki
 * @date 2/9/14.
 */
object RangeFr {

  final def apply(start: Int, last: Int, step: Int, optSegment: OptSegment) = new RangeFr(start, last, step, optSegment)
  final def apply(start: Int, last: Int, step: Int) = new RangeFr(start, last, step)
  final def apply(start: Int, last: Int, optSegment: OptSegment) = new RangeFr(start, last, optSegment)
  final def apply(start: Int, last: Int) = new RangeFr(start, last)

}

class RangeFr(val start: Int, val last: Int, val step: Int, val optSegment: OptSegment)
  extends RangeFrSpecifier with LoggingExt {

  override def toString() = s"RangeFr($start, $last, step=$step, segment=$optSegment)"

  def this(start: Int, last: Int, step: Int) = this(start, last, step, OptSegmentAutomatic)
  def this(start: Int, last: Int, optSegment: OptSegment) = this(start, last, 1, optSegment)
  def this(start: Int, last: Int) = this(start, last, 1, OptSegmentAutomatic)

  loggerRequire( start <= last, "RangeFr requires start <= last. start={}, last={}", start.toString, last.toString)
  loggerRequire( step > 0, "step must be specified as positive. Invalid value: " + step.toString)

  // <editor-fold defaultstate="collapsed" desc=" range info accessors ">

  override final def getRangeFrReal(xDataTiming: XDataTiming): RangeFrReal = { //Range.Inclusive = {
    val realSegment = getRealSegment(xDataTiming)
    if(0<=start){
      val segmentLength = xDataTiming.segmentLength(realSegment)
      if( last < segmentLength){
        new RangeFrValid( start, last, getRealStep(xDataTiming), realSegment)
      }
      else new RangeFrReal( start, last, getRealStep(xDataTiming), realSegment)
    }
    else new RangeFrReal( start, last, getRealStep(xDataTiming), realSegment)
  }

  override final def getRangeFrValid(xDataTiming: XDataTiming): RangeFrValid = { //Range.Inclusive = {
    new RangeFrValid( firstValid(xDataTiming), lastValid(xDataTiming), getRealStep(xDataTiming), getRealSegment(xDataTiming) )
  }
  override final def getRangeFrValidPrePost(xDataTiming: XDataTiming): (Int, RangeFrValid, Int) = {
    val totalLength =  xDataTiming.segmentLength( getRealSegment(xDataTiming) )
    val preL = preLength( totalLength )
    val postL = postLength( totalLength )
    (preL, getRangeFrValid(xDataTiming), postL)
  }

  override final def getRealStep(xDataTiming: XDataTiming): Int =  if ( step == -1 ) 1 else step

  override final def getRealSegment(xDataTiming: XDataTiming) = xDataTiming.getRealSegment( optSegment )

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" utility functions ">

  // <editor-fold defaultstate="collapsed" desc=" protected utility functions: intervalContains/intervalMod ">

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

  /** Inclusive first valid frame, taking into account step and overhang
    */
  def firstValid(xDataTiming: XDataTiming): Int = firstValid(xDataTiming.segmentLength(getRealSegment(xDataTiming)))
  private var fvBuffTL = -1
  private var fvBuff = - 156111
  /** Inclusive first valid frame, taking into account step and overhang
    */
  def firstValid(totalLength: Int) = {
    if( fvBuffTL == totalLength ) fvBuff
    else {
      fvBuff = firstValidImpl(totalLength)
      fvBuffTL = totalLength
      fvBuff
    }
  }
  private def firstValidImpl(totalLength: Int) = {
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
  def lastValid(xDataTiming: XDataTiming): Int = lastValid(xDataTiming.segmentLength(getRealSegment(xDataTiming)))
  /** Valid lastValid frame, taking into account step and overhang
    */
  private var lvBuffTL = -1
  private var lvBuff = - 156112
  /** Inclusive last valid frame, taking into account step and overhang
    */
  def lastValid(totalLength: Int) = {
    if( lvBuffTL == totalLength ) lvBuff
    else {
      lvBuff = lastValidImpl(totalLength)
      lvBuffTL = totalLength
      lvBuff
    }
  }
  def lastValidImpl(totalLength: Int) = {
    if(last < 0 ) Int.MinValue //no valid values
    else if( 0 == last ) {
      val temp = firstValid(totalLength)
      if( temp == 0 ) 0
      else Int.MinValue //no valid values
    }
    else if( /*0 < end*/ last < totalLength ) {
      val fv = firstValid(totalLength)
      if (fv == last) fv
      else {
        val temp = intervalContains(fv, last, step)
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
  def length(totalLength: Int): Int = intervalContains(start, last, step)

  /**range length, can include zero padding if start<0 or totalLength<=end
    */
  def length(xDataTiming: XDataTiming): Int = xDataTiming.segmentLength(getRealSegment(xDataTiming))

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" preLength/postLength ">

  /** How many points to pad at the beginning given the valid data range.
    */
  def preLength(totalLength: Int): Int = {
    if( start >= 0 ) 0    //all post padding or no padding
    else if (last < 0) { //all pre padding
      (last - start)/step + 1
    } else{
      intervalContains(start, -1, step)
    }
  }

  /** How many points to pad at the end given the valid data range.
    */
  def postLength(totalLength: Int): Int = {
    if( start >= totalLength )  (last - start)/step + 1   //all post padding
    else if (last < totalLength) 0   //all pre padding or no padding
    else{
      intervalContains(lastValid(totalLength), last, step) - 1
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" isFullyValid ">

  /** Whether the frame range is completely contained within available data.
    * @param xFrames data object to which to apply the frames
    */
  def isFullyValid(xFrames: XDataTiming): Boolean = isFullyValid( xFrames.segmentLength(getRealSegment(xFrames)) )

  /** Whether the frame range is completely contained within available data.
    * @param totalLength full length of this segment in frames, used to realize with RangeFr.all()
    */
  def isFullyValid(totalLength: Int): Boolean = {
      firstValid(totalLength) <= start && last <= lastValid(totalLength) //< totalLength
  }

  // </editor-fold>

  // </editor-fold>

//  /**Will return self, this is in order to comply with [[RangeFrSpecifier]]
//   */
//  def getRangeFr(xFrames: XFrames): RangeFr = this
//  //def getRangeFr(totalLength: Int): RangeFr = this


}

/**Extends Range.Inclusive to include "segment" variable. All values should be real values (ie not including "-1"
 * for "Automatic", etc).
 */
class RangeFrReal(val start: Int, val last: Int, val step: Int, val segment: Int) extends RangeFrSpecifier {
//  extends Range.Inclusive(start, end, step) {

  override def toString() = s"RangeFrReal($start, $last, step=$step, segment=$segment)"
  loggerRequire(start < last, s"Start $start must be < last $last")
  loggerRequire(1 <= step, s"Step $step must be >= 1")
  loggerRequire(0 <= segment, s"Segment $segment must be >= 0")

  def length() = (last-start)/step + 1

  // <editor-fold defaultstate="collapsed" desc=" RangeFrSpecifier ">
  override final def getRealSegment(xDataTiming: XDataTiming): Int = segment
  override final def getRealStep(xDataTiming: XDataTiming): Int = step
  override final def getRangeFrReal(xDataTiming: XDataTiming): RangeFrReal = this
  override def getRangeFrValid(xDataTiming: XDataTiming): RangeFrValid =
    (new RangeFr(start, last, step, OptSegment(segment))).getRangeFrValid(xDataTiming)
  override def getRangeFrValidPrePost(xDataTiming: XDataTiming): (Int, RangeFrValid, Int) =
    (new RangeFr(start, last, step, OptSegment(segment))).getRangeFrValidPrePost(xDataTiming)
  // </editor-fold>

}

/**Extends RangeFrReal to be within valid data range.
  */
class RangeFrValid(override val start: Int, override val last: Int, override val step: Int, override val segment: Int)
  extends RangeFrReal(start, last, step, segment) {

  override def toString() = s"RangeFrValid($start, $last, step=$step, segment=$segment)"

  loggerRequire(0 <= start, s"Start $start must be >= 0")

  // <editor-fold defaultstate="collapsed" desc=" RangeFrSpecifier ">
  override def getRangeFrValid(xDataTiming: XDataTiming): RangeFrValid = this
  override def getRangeFrValidPrePost(xDataTiming: XDataTiming): (Int, RangeFrValid, Int) = (0, this, 0)
  // </editor-fold>

}
