package nounou.data.filters

import nounou._
import breeze.linalg.RangeExtender
import scala.collection.mutable.{ArrayBuffer, HashMap}
import com.typesafe.scalalogging.slf4j.Logging
import nounou.data.XData


//ToDo: buffer timing info?
//ToDo: parallelize?

/** Buffer filter, which will save intermediate calculation results for an XData object.
  */
class XDataFilterBuffer(override val upstream: XData ) extends XDataFilter(upstream) {

  var buffer: HashMap[(Int, Int, Int), Vector[Int]] = new ReadingHashMapBuffer()
  var garbageQue: ArrayBuffer[(Int, Int, Int)] = new ArrayBuffer[(Int, Int, Int)]()

  lazy val bufferPageLength: Int = (32768 / 2) //default page length will be 32 kB
  lazy val garbageQueBound: Int = 1024 //32MB in data + //1073741824 / 8 / (bufferPageLength * 2)  //default buffer maximum size will be 128 MB

  logger.debug("initialized XDataFilterTrBuffer w/ bufferPageLength={} and garbageQueBound={}", bufferPageLength.toString, garbageQueBound.toString)


  // <editor-fold defaultstate="collapsed" desc=" changes (XDataSource related) and flushing ">

  override def changedData() = {
    flushBuffer()
    for(child <- children) child.changedData()
  }
  override def changedData(channel: Int) = {
    flushBuffer( channel )
    for(child <- children) child.changedData( channel )
  }
  override def changedData(channels: Vector[Int]) = {
    flushBuffer( channels )
    for(child <- children) child.changedData( channels )
  }

  def flushBuffer(): Unit = {
    logger.debug( "flushBuffer() pre, buffer.size={}, garbageQue.length={}", buffer.size.toString, garbageQue.length.toString )
    buffer.clear()
    garbageQue.clear()
  }

  def flushBuffer(channel: Int): Unit = {
    logger.debug( "flushBuffer({}) conducted", channel.toString )
    buffer = buffer.filter( ( p:((Int, Int, Int), Vector[Int]) ) => ( p._1._1 != channel ) )
    garbageQue = garbageQue.filter( ( p:(Int, Int, Int) ) => (p._1 != channel) )
  }

  def flushBuffer(channels: Vector[Int]): Unit = {
    logger.debug( "flushBuffer({}) conducted", channels.toString )
    buffer = buffer.filterNot( ( p:((Int, Int, Int), Vector[Int]) ) => ( channels.contains( p._1._1 ) ) )
    garbageQue = garbageQue.filterNot( ( p:(Int, Int, Int) ) => ( channels.contains( p._1 ) ) )
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" data reading ">

  def getBufferPage(frame: Int) = frame/bufferPageLength
  def getBufferIndex(frame: Int) = frame%bufferPageLength

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = {
    buffer( (channel, getBufferPage(frame), segment) )( getBufferIndex(frame) )
  }

  override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): Vector[Int] = {

        //val totalLength = segmentLengths(segment)
        val tempret = ArrayBuffer[Int]()
          tempret.sizeHint(range.length )
        var tempretArr: Array[Int] = null
        val startPage =  getBufferPage( range.start)
        val startIndex = getBufferIndex(range.start)
        val endPage =    getBufferPage( range.last )
        val endIndex =   getBufferIndex(range.last )

        if(startPage == endPage) {
          tempretArr = buffer( (channel, startPage, segment) ).slice(startIndex, endIndex + 1).toArray
        } else {
          tempret ++= buffer( (channel, startPage, segment) ).slice(startIndex, bufferPageLength)  //deal with startPage separately
          //if( startPage + 1 <= endPage ) {
            for( page <- (startPage + 1 to endPage)/*.par*/ ){//endPage - 1) {
              tempret ++= buffer( (channel, page, segment) )
            }
          //}
          //tempret ++= buffer( (channel, endPage, segment) ).slice(0, endIndex + 1)  //deal with endPage separately
          tempretArr = tempret.toArray
        }

        val realRet = new Array[Int]( range.length )
    //println("realRet.length=" + realRet.length + ", tempretArr.length="+ tempretArr.length)
        var index = 0
        for(cnt <- 0 until range.length){
          realRet(cnt) = tempretArr(index)
          index += range.step
        }
        realRet.toVector
  }

  //redirection function to deal with scope issues regarding super
  private def tempTraceReader(ch: Int, range: Range.Inclusive, segment: Int) = upstream.readTraceImpl(ch, range, segment)

  class ReadingHashMapBuffer extends HashMap[(Int, Int, Int), Vector[Int]] {

    //do not use applyOrElse!
    override def apply( key: (Int, Int, Int)  ): Vector[Int] = {
      val index = garbageQue.indexOf( key )
      if( index == -1 ){
        if(garbageQue.size >= garbageQueBound ){
          this.remove( garbageQue(1) )
          garbageQue.drop(1)
        }
        garbageQue.append( key )
        default( key )
      }else{
        garbageQue.remove( index )
        garbageQue.append( key )
        super.apply(key)
      }
    }

    override def default( key: (Int, Int, Int)  ): Vector[Int] = {
      val startFrame = key._2 * bufferPageLength
      val endFramePlusOne: Int = scala.math.min( startFrame + bufferPageLength, segmentLengths( key._3 ) )
      val returnValue = tempTraceReader( key._1, new Range.Inclusive(startFrame, endFramePlusOne-1, 1), key._3  )
      this.+=( key -> returnValue )
      returnValue
    }
  }

  // </editor-fold>

}
