package nounous.data


object Span {
  final val All = new Span(0, -1, 1)
}
final case class Span(start: Int, end: Int, step: Int = 1) {
  require(step != 0, "step size cannot be zero!")

  def getMaxIndex(length: Int): Int = {
    val (tempStart, tempEnd) = getActualStartEnd(length)
    val tempret = math.max(tempStart, tempEnd)
    require(tempret < length, "maximum index is beyond the given length!")
    tempret
  }
  def getMinIndex(length: Int): Int = {
    val (tempStart, tempEnd) = getActualStartEnd(length)
    math.min(tempStart, tempEnd)
  }

  def getRange(length: Int): scala.Range = {
    val (tempStart, tempEnd) = getActualStartEnd(length)

    if(step > 0){
      if(tempStart <= tempEnd && tempEnd < length){
        new scala.Range(tempStart, tempEnd, step)
      }else{
        throw new IllegalArgumentException
      }
    } else {
      if(tempEnd <= tempStart && tempStart < length){
        new scala.Range(tempStart, tempEnd, step)
      }else{
        throw new IllegalArgumentException
      }
    }
  }

  def getActualStartEnd(length: Int): (Int, Int) = {
    (
      if( start<0 ){ length + start } else { start },
      if( end<0 ){ length + end } else { end }
    )
  }


}


