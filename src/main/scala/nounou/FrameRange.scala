package nounou

/**
 * @author ktakagaki
 * @date 2/9/14.
 */
object FrameRange {

  val All = new All(1)

  class All(byVal: Int) extends FrameRange(0, 0 /*Integer.MAX_VALUE*/, byVal, true){
    override def preLength(totalLength: Int) = 0
    override def postLength(totalLength: Int) = 0
    //override def getValidRange(totalLength: Int) = 0 to (totalLength -1) by byVal
  }

}

class FrameRange(val start: Int, val endMarker: Int, val step: Int, val isAll: Boolean = false) /*extends Range(start, last, step)*/{

  require( step > 0, "In nounous, step > 0 is required for frame ranges.")
  require( start <= endMarker, "In nounous, start <= last is required for frame ranges. start=" + start + ", last=" + endMarker)

  override def toString() = "FrameRange(" + start + ", " + endMarker + ", " + step + ", isAll=" + isAll + ")"

  //private val tempRange: Range.Inclusive = if(isAll) null else new Range.Inclusive(start, endMarker, step)
//  val indexedStart = 0
//  def indexedDataStart(vectDataLen: Int) = {
//    if(start < 0) - start
//    else if (start < vectDataLen) start
//  }
  private def getSamplesFromLength(len: Int) = (len -1)/step + 1

  /**range length*/
  def length(vectDataLen: Int): Int = {
    if(isAll || start >= vectDataLen) getSamplesFromLength( vectDataLen )
    else getSamplesFromLength(endMarker - start + 1)//(endMarker - start /*+ 1 - 1*/)/step  + 1
  }

  /**Inclusive last frame, taking into account step and overhang*/
  def last(vectDataLen: Int): Int = start + (length(vectDataLen) - 1 ) * step

  // <editor-fold defaultstate="collapsed" desc=" conversion to Range.Inclusive ">

  /** get a [[Range.Inclusive]] taking into account length and step, so that the start and last are exactly present values
    */
  def getRange(tL: Int): Range.Inclusive = {
    new Range.Inclusive(start, last(tL), step)
  }

  /** get a [[Range.Inclusive]] which fits inside the given data vector length, and takes into account length and step,
    * so that the start and last are exactly present values
    */
  def getValidRange(vectDataLen: Int): Range.Inclusive = {

    if(isAll) {
        new Range.Inclusive(0, last(vectDataLen), step)
    } else if(start >= vectDataLen ) {
        new Range.Inclusive(0, -1, 1)// range with length zero
    } else if(start >= 0 ) {
        val realLast = last(vectDataLen)

        if( realLast < vectDataLen  ) new Range.Inclusive(start, realLast , step)
        else new Range.Inclusive(start, start + getSamplesFromLength(vectDataLen - start + 1), step )
    } else {
        if(endMarker < 0) new Range.Inclusive(0, -1, 1)// range with length zero
        else new Range.Inclusive(start + preLength(vectDataLen)*step, last(vectDataLen), step)
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" How much padding? ">

  def preLength(vectDataLen: Int): Int = {
    if( isAll || start >= 0 ){
        0 //all post padding or no padding
    } else if ( - vectDataLen < start ) {
        getSamplesFromLength( - start ) //pre padding from start to -1
    } else {
        getSamplesFromLength( vectDataLen ) //all pre padding
    }
  }

  def postLength(vectDataLen: Int): Int = {
    if( isAll ){
      0
    }else{
      val realLast = last(vectDataLen)
      if( realLast < vectDataLen ) 0
      else length(vectDataLen) - getSamplesFromLength(vectDataLen - start)
    }
//    if( isAll ) 0 else {
//      if ( endMarker < vectDataLen ) 0  //all pre padding or no padding
//      else if ( endMarker > 2 * vectDataLen - 1 ) getSamplesFromLength(vectDataLen) //all post padding
//      else getSamplesFromLength(endMarker - start + 1) - getSamplesFromLength(vectDataLen - start) //( endMarker - vectDataLen + 1 )
//    }
  }

//  def isNoOverhangs(vectDataLen: Int): Boolean =
//    if (isAll) true
//    else (start >= 0 && endMarker < vectDataLen)

  // <editor-fold defaultstate="collapsed" desc="  ">

}

