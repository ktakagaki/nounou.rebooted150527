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
    //override def validRange(totalLength: Int) = 0 to (totalLength -1) by byVal
  }

}

class FrameRange(val start: Int, val endMarker: Int, val step: Int, val isAll: Boolean = false) /*extends Range(start, last, step)*/{

  require( step > 0, "In nounous, step > 0 is required for frame ranges.")
  require( start <= endMarker, "In nounous, start <= last is required for frame ranges. start=" + start + ", last=" + endMarker)

  override def toString() = "FrameRange(" + start + ", " + endMarker + ", " + step + ", isAll=" + isAll + ")"
  //override val isInclusive = true
  //println( this )

  /**range length*/
  def length(totalLength: Int): Int = {
    if(isAll) totalLength
    else (endMarker - start /*+ 1 - 1*/)/2  + 1
  }
  /**Inclusive start frame*/
  def start(totalLength: Int): Int = {
    start //only positive steps, start is always correct
  }
  /**Inclusive last frame*/
  def last(totalLength: Int): Int = {
    if(isAll) totalLength - 1
    else endMarker - start + 1
  }
  def getRange(tL: Int): Range = {
    new Range(start, last(tL), step)
  }


  def preLength(totalLength: Int): Int = {
    if( isAll ) 0 else {
      if ( start >= 0 ) 0
      else if ( start < - totalLength) totalLength //all pre padding
      else ( - start )
    }
  }

  def postLength(totalLength: Int): Int = {
    if( isAll ) 0 else {
      if ( endMarker < totalLength ) 0
      else if ( endMarker > 2 * totalLength - 1 ) totalLength //all post padding
      else ( endMarker - totalLength + 1 )
    }
  }

  def validRange(totalLength: Int): Range.Inclusive = {

    val (realStart: Int, realEnd: Int) =
      isAll match {
        case true => (0, totalLength - 1)
        case false => (if(start < totalLength) math.max(0, start) else 0,
          if(endMarker < 0 || start >= totalLength) 0 else math.min(totalLength - 1, endMarker)    )
      }

    new Range.Inclusive(realStart, realEnd, step)
  }

  def isNoOverhangs(totalLength: Int): Boolean =
    if (isAll) true
    else (start >= 0 && endMarker < totalLength)

}