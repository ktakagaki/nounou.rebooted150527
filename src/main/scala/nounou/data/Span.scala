package nounou.data

object Span {

  case object All extends Span(0, -1, 1)
  case class Seq(start: Int, end: Int, step: Int = 1) extends Span(start, end, step)
}

/** Span encapsulates vector and matrix indexes, and is similar to [[breeze.Range]].
  * CAUTION: span is inclusive of the "end" parameter, ie [start, end]
  *         this is in contrast to [[breeze.Range]], which is [start, end).
  */
class Span(start: Int, end: Int, step: Int = 1) {
  require(step != 0, "step size cannot be zero!")

  def getMaxIndex(length: Int): Int = {
    val (tempStart, tempEnd) = getStartEndIndexes(length)
    val tempret = math.max(tempStart, tempEnd)
    //require(tempret < length, "maximum index is beyond the given length!")
    tempret
  }
  def getMinIndex(length: Int): Int = {
    val (tempStart, tempEnd) = getStartEndIndexes(length)
    math.min(tempStart, tempEnd)
  }

  def getRange(length: Int): scala.Range = {

    val (tempStart, tempEnd) = getStartEndIndexes(length)

    if(step > 0){
      if(tempStart <= tempEnd && tempEnd < length){
        new scala.Range(tempStart, tempEnd+1, step)  //note: end is exclusive, hence (last index) + 1 to give end
      }else{
        throw new IllegalArgumentException("Step > 0  but start > end!")
      }
    } else {
      if(tempEnd <= tempStart && tempStart < length){
        new scala.Range(tempStart, tempEnd+1, step) //note: end is exclusive, hence (last index) + 1 to give end
      }else{
        throw new IllegalArgumentException("Step < 0  but start < end!")
      }
    }
  }

  def length(length: Int): Int = getRange(length).length

  def getStartEndIndexes(length: Int): (Int, Int) = {
    (
      if( start<0 ){ length + start } else { start },
      if( end<0 ){ length + end } else { end }   //note: here we use actual indexes, note that Range.end is exclusive
    )
  }

  def jInstance = this

}


