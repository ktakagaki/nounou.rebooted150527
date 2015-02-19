package nounou.data.ranges

import breeze.linalg.{max, min}
import nounou._
import nounou.util.LoggingExt
import nounou.data.traits.XDataTiming

/**FrameRange is the central class for specifying frame-based
  * data sample timepoints. Constructor methods are located within the
  * convenience class [[nounou.NN]].
  *
 * @author ktakagaki
 * @date 2/9/14.
 */

class SampleRange(val start: Int, val last: Int, val step: Int, val segment: Int)
  extends SampleRangeSpecifier with LoggingExt {

  override def toString() = s"FrameRange($start, $last, step=$step, segment=$segment)"

  loggerRequire( start <= last, s"FrameRange requires start <= last. start=$start, last=$last")
  loggerRequire( step >= 1 || step == -1, s"step must be -1 (automatic) or positive. Invalid value: $step")
  loggerRequire( segment >= -1, s"segment must be -1 (automatic first segment) or positive. Invalid value: $segment")

  // <editor-fold defaultstate="collapsed" desc=" range info accessors ">

  override final def getSampleRangeReal(xDataTiming: XDataTiming): SampleRangeReal = { //Range.Inclusive = {
    val realSegment = getRealSegment(xDataTiming)
    if(0<=start){
      val segmentLength = xDataTiming.segmentLength(realSegment)
      if( last < segmentLength){
        new SampleRangeValid( start, last, getRealStep(xDataTiming), realSegment)
      }
      else new SampleRangeReal( start, last, getRealStep(xDataTiming), realSegment)
    }
    else new SampleRangeReal( start, last, getRealStep(xDataTiming), realSegment)
  }

  override final def getSampleRangeValid(xDataTiming: XDataTiming): SampleRangeValid = { //Range.Inclusive = {
    new SampleRangeValid( firstValid(xDataTiming), lastValid(xDataTiming), getRealStep(xDataTiming), getRealSegment(xDataTiming) )
  }

  override final def getSampleRangeValidPrePost(xDataTiming: XDataTiming): (Int, SampleRangeValid, Int) = {
    val totalLength =  xDataTiming.segmentLength( getRealSegment(xDataTiming) )
    val preL = preLength( totalLength )
    val postL = postLength( totalLength )
    (preL, getSampleRangeValid(xDataTiming), postL)
  }

  /** For FrameRange, just read -1 as 1 (default).
    */
  override final def getRealStep(xDataTiming: XDataTiming): Int =  if ( step == -1 ) 1 else step

  override final def getRealSegment(xDataTiming: XDataTiming) = xDataTiming.getRealSegment( segment )

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

}

/**Extends FrameRange but with the enforced assumption that all defaults are instantiated.
 */
class SampleRangeReal(val start: Int, val last: Int, val step: Int, val segment: Int) extends SampleRangeSpecifier {
//  extends Range.Inclusive(start, end, step) {

  override def toString() = s"FrameRangeReal($start, $last, step=$step, segment=$segment)"
  loggerRequire(start <= last, s"Start $start must be < last $last")
  loggerRequire(1 <= step, s"Step $step must be >= 1")
  loggerRequire(0 <= segment, s"Segment $segment must be >= 0")

  def length() = (last-start)/step + 1

  // <editor-fold defaultstate="collapsed" desc=" RangeFrSpecifier ">
  override final def getRealSegment(xDataTiming: XDataTiming): Int = segment
  override final def getRealStep(xDataTiming: XDataTiming): Int = step
  override final def getSampleRangeReal(xDataTiming: XDataTiming): SampleRangeReal = this
  override def getSampleRangeValid(xDataTiming: XDataTiming): SampleRangeValid =
    (new SampleRange(start, last, step, segment)).getSampleRangeValid(xDataTiming)
  override def getSampleRangeValidPrePost(xDataTiming: XDataTiming): (Int, SampleRangeValid, Int) =
    (new SampleRange(start, last, step, segment)).getSampleRangeValidPrePost(xDataTiming)
  // </editor-fold>

}

/**Extends FrameRange but with the implicit assumption that start and last are within range,
  * and that step and segment are instantiated.
  */
/**Extends RangeFrReal to be within valid data range.
  */
class SampleRangeValid(override val start: Int, override val last: Int, override val step: Int, override val segment: Int)
  extends SampleRangeReal(start, last, step, segment) {

  override def toString() = s"FrameRangeValid($start, $last, step=$step, segment=$segment)"

  loggerRequire(0 <= start, s"Start $start must be >= 0")

  // <editor-fold defaultstate="collapsed" desc=" RangeFrSpecifier ">
  override def getSampleRangeValid(xDataTiming: XDataTiming): SampleRangeValid = this
  override def getSampleRangeValidPrePost(xDataTiming: XDataTiming): (Int, SampleRangeValid, Int) = (0, this, 0)
  // </editor-fold>

}
