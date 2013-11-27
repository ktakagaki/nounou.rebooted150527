package nounous.data


object Span {
  final val All = new Span(0, -1, 1)
}
final case class Span(start: Int, end: Int, step: Int = 1) {
  require(step != 0)

  def getRange(length: Int): scala.Range = {
    val tempStart = if( start<0 ){ length + start } else { start }
    val tempEnd = if( end<0 ){ length + end } else { end }

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

}


