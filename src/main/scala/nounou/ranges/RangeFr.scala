package nounou.ranges

import nounou.LoggingExt
import nounou.data.traits.XFrames

//TODO 1: Should really streamline this code and test better, but it is a minefield!

abstract class RangeFrSpecifier extends LoggingExt {
  def getFrameRange(x: XFrames): RangeFr
  def getValidRange(x: XFrames): Range.Inclusive = getFrameRange(x).getValidRange(x)
}

/**
 * @author ktakagaki
 * @date 2/9/14.
 */
object RangeFr extends LoggingExt {

  final def apply(start: Int, endMarker: Int, step: Int, segment: Int) = new RangeFr(start, endMarker, step, segment)
  final def apply(start: Int, endMarker: Int, step: Int) = new RangeFr(start, endMarker, step, segment = 0)
  final def apply(start: Int, endMarker: Int) = new RangeFr(start, endMarker, 1, segment = 0)

}


class RangeFr(val start: Int, val endMarker: Int, val step: Int = 1, val segment: Int/* = 0*/, val isAll: Boolean = false)
  extends RangeFrSpecifier with LoggingExt {

  loggerRequire( step > 0, "Step > 0 is required for frame ranges; step = {}! Did you mean to specify the segment variable instead? >> check calling syntax", step.toString)
  loggerRequire( start <= endMarker, "In nounous, start <= last is required for frame ranges. start=" + start + ", last=" + endMarker)

  override def toString() = "RangeFr(" + start + ", " + endMarker + ", " + step + ", isAll=" + isAll + ")"


  // <editor-fold defaultstate="collapsed" desc=" length/last, using a buffered Range.Inclusive ">

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

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" isFullyValid/lastValid ">

  /** Whether the frame range is completely contained within available data.
    * @param xFrames data object to which to apply the frames
    */
  def isFullyValid(xFrames: XFrames): Boolean = {
    if(isAll) true
    else isFullyValid( xFrames.segmentLength(segment) )
  }
  /** Whether the frame range is completely contained within available data.
    * @param totalLength full length of this segment in frames, used to realize with RangeFr.all()
    */
  def isFullyValid(totalLength: Int): Boolean = {
    if(isAll) true
    else start >= 0 && last(totalLength) < totalLength
  }

  /** Valid last frame, taking into account step and overhang
    * @param totalLength full length of this segment in frames, used to realize with RangeFr.all()
    */
  def lastValid(totalLength: Int): Int = {
    if(isAll){
      last(totalLength)
    } else {
      val realEnd = scala.math.min(totalLength - 1, endMarker)
      if (start <= realEnd) {
        //      val tempLast = last(totalLength)
        //      if(tempLast < totalLength) tempLast
        //      else (totalLength - start - 1)/step*step + 1 + start //(new Range.Inclusive(start, totalLength-1, step)).last        //ToDo 3: make into more streamlined code
        val tempRange = new Range.Inclusive(start, realEnd, step) //ToDo 5: streamline this to not use Range.Inclusive
        tempRange.last
      } else {
        -1 //give errorif start>realEnd
      }
    }
  }

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" getRange/getValidRange (conversion to Range.Inclusive) ">

//  /** Get a [[Range.Inclusive]] taking into account length and stepMs, so that the start and last are exactly present values
//    * @param totalLength full length of this segment in frames, used to realize with RangeFr.all()
//    */
//  def getRange(totalLength: Int): Range.Inclusive =
//    if(isAll) new Range.Inclusive(0, last(totalLength), step)
//    else new Range.Inclusive(start, endMarker, step)

  override def getValidRange(xFrames: XFrames) = getValidRange(xFrames.segmentLength(segment))

  /** Get a [[Range.Inclusive]] which fits inside the given data vector length, and takes into account length and stepMs,
    * so that the start and last are exactly present values.
    * @param totalLength full length of this segment in frames, used to realize with RangeFr.all()
    */
  def getValidRange(totalLength: Int): Range.Inclusive = {

    if(isAll) {
      //full range
      new Range.Inclusive(0, last(totalLength), step)
    } else if(start >= totalLength ) {
      //range starts after final data value
      new Range.Inclusive(0, -1, 1)// range with length zero
    } else if(start >= 0 ) {
      //range starts within data
      new Range.Inclusive(start, lastValid(totalLength), step)
    } else {
      //range starts in negative range
      val realStart =
        //if(start<0){
          start + ((- start - 1)/step + 1 ) * step
        //} else { start }
      new Range.Inclusive(realStart, lastValid(totalLength), step)
    }

  }

  def getValidRangeFr(xFrames: XFrames): RangeFr =  getValidRangeFr(xFrames.segmentLength(segment))

  def getValidRangeFr(totalLength: Int): RangeFr = {

    if(isAll) {
      //full range
      RangeFr(0, last(totalLength), step)
    } else if(start >= totalLength ) {
      //range starts after final data value
      RangeFr(0, -1, 1)// range with length zero   //ToDo 1: This will throw error!
    } else if(start >= 0 ) {
      //range starts within data
      RangeFr(start, lastValid(totalLength), step)
    } else {
      //range starts in negative range
      val realStart =
      //if(start<0){
        start + ((- start - 1)/step + 1 ) * step
      //} else { start }
      RangeFr(realStart, lastValid(totalLength), step)
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

  /**Will return self, this is in order to comply with [[RangeFrSpecifier]]
   */
  override def getFrameRange(x: XFrames): RangeFr = this

}

class RangeFrAll(override val step: Int = 1, override val segment: Int = 0) extends RangeFr(0, 0, step, segment, true)

object RangeFrAll extends LoggingExt {
  final def apply(): RangeFr = apply(1)
  final def apply(step: Int): RangeFr = new RangeFrAll(step, 0)
  final def apply(step: Int, segment: Int): RangeFr = new RangeFrAll(step, segment)
}
