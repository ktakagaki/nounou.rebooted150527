package nounou

import nounou.data.traits.{XFrames}

//TODO 1: Should really streamline this code and test better, but it is a minefield!

abstract class RangeFrSpecifier extends LoggingExt {
  def getFrameRange(x: XFrames): RangeFr
}

/**
 * @author ktakagaki
 * @date 2/9/14.
 */
object RangeFr extends LoggingExt {

  final def All(): RangeFr = All(1)

  final def All(step: Int): RangeFr = new RangeFr(0, 0, step, true)

  //ToDo 2: transfer to RangeMS
  final def msRange(startMs: Double, endMs: Double, stepMs: Double, sampleRate:Double): RangeFr = {

    loggerRequire(stepMs>0, "stepMs ({}) must be larger than zero!", stepMs.toString)
    loggerRequire(sampleRate>0, "sampleRate ({}) must be larger than zero!", sampleRate.toString)

    val startFr = (startMs/1000d * sampleRate).toInt
    val endFr = (endMs/1000d * sampleRate).toInt
    val stepReal = (stepMs/1000d * sampleRate).toInt

    new RangeFr(startFr, endFr, stepReal)
  }

  def msAnchorRange(anchorMs: Double, preMs: Double, postMs: Double, stepMs: Double, sampleRate:Double): RangeFr = {
    msRange(anchorMs-preMs, anchorMs+postMs, stepMs, sampleRate)
  }

}

class RangeFr(val start: Int, val endMarker: Int, val step: Int = 1, val isAll: Boolean = false) extends LoggingExt {

  loggerRequire( step > 0, "In nounous, stepMs > 0 is required for frame ranges; stepMs = {}!", step.toString)
  loggerRequire( start <= endMarker, "In nounous, start <= last is required for frame ranges. start=" + start + ", last=" + endMarker)

  override def toString() = "RangeFr(" + start + ", " + endMarker + ", " + step + ", isAll=" + isAll + ")"

  //private def getSamplesFromLength(len: Int) = (len -1)/step + 1
  private var buffRangeInclusive = new Range.Inclusive(0,0,1)
  private var buffRangeInclusiveLength = -1
  private def buffRefresh(totalLength: Int) = {
    if( totalLength != buffRangeInclusiveLength ) {
      buffRangeInclusive = if(isAll) {
        new Range.Inclusive(start, totalLength-1, step)
      } else {
        new Range.Inclusive(start, endMarker, step)//scala.math.min(endMarker, totalLength-1), step)
      }
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

  /** Valid last frame, taking into account step and overhang
    * @param totalLength full length of this segment in frames, used to realize with RangeFr.all()
    */
  def lastValid(totalLength: Int): Int = {
    val realEnd = scala.math.min(totalLength -1, endMarker)
    if(start <= realEnd){
      val tempRange = new Range.Inclusive(start, realEnd, step)
      tempRange.last
    }else{
      -1  //give errorif start>realEnd
    }
//    if(isAll) (totalLength - 1)/step + 1
//    else {
//      val tempLast = last(totalLength)
//      if(tempLast < totalLength) tempLast
//      else (totalLength - start - 1)/step*step + 1 + start //(new Range.Inclusive(start, totalLength-1, step)).last        //ToDo 3: make into more streamlined code
//    }
  }

  // <editor-fold defaultstate="collapsed" desc=" conversion to Range.Inclusive ">

  /** Get a [[Range.Inclusive]] taking into account length and stepMs, so that the start and last are exactly present values
    * @param totalLength full length of this segment in frames, used to realize with RangeFr.all()
    */
  def getRange(totalLength: Int): Range.Inclusive =
    if(isAll) new Range.Inclusive(0, last(totalLength), step)
    else new Range.Inclusive(start, endMarker, step)

  /** Get a [[Range.Inclusive]] which fits inside the given data vector length, and takes into account length and stepMs,
    * so that the start and last are exactly present values.
    * @param totalLength full length of this segment in frames, used to realize with RangeFr.all()
    */
  def getValidRange(totalLength: Int): Range.Inclusive = {

    if(isAll) {                            //full range
      new Range.Inclusive(0, last(totalLength), step)
    } else if(start >= totalLength ) {     //range starts after final data value
      new Range.Inclusive(0, -1, 1)// range with length zero
    } else if(start >= 0 ) {               //range starts within data
      new Range.Inclusive(start, lastValid(totalLength), step)
    } else {                               //range starts in negative range
        val realStart =
          if(start<0){
            start + ((- start - 1)/step + 1 ) * step
          } else { start }
        new Range.Inclusive(realStart, lastValid(totalLength), step)
    }

  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" How much padding? ">

  def preLength(totalLength: Int): Int = {
    if( isAll || start >= 0 ){              //all post padding or no padding
      0
    } else if ( totalLength > - start ) {   //pre padding from start to -1
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
      (endMarker - start )/step + 1
    } else if( isAll ){                            //no post padding
      0
    } else {
      //println( endMarker + " " + lastV + " " + step)
      ( endMarker - lastV )/step /* + 1 - 1*/
      // + step //scala.math.min(totalLength-1, endMarker)
//      if( endMarker <= lastV ) 0     //no padding
//      else (endMarker - lastV /* + 1 - 1*/)/step
    }
  }
  // </editor-fold>


}
