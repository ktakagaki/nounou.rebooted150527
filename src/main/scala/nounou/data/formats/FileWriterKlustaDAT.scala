package nounou.data.formats

import nounou.data._
import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}

/**
 * @author ktakagaki
 * @date 2/2/14.
 */
object FileWriterKlustaDAT extends FileWriter {

  override def write(fileName: String, data: List[X]) = write(fileName, data, 0, Span.All)

  def write(fileName: String, data: List[X], segment: Int = 0, span: Span ): Unit = {

    val actualFileName = {
      if( fileName.toLowerCase.endsWith(".klusta.dat") ) fileName
      else fileName + ".klusta.dat"
    }

    val actualXData =
      if( data.length == 1 ) {
        data(0) match {
          case x: XData => x
          case x: XDataChannel => new XDataChannelArray( Vector(x) )
          case _ => throw new IllegalArgumentException("Cannot write data type " + data(0) + " as .klusta.dat file!")
        }
      } else if (data.forall( _.isInstanceOf[XDataChannel] )) {
        new XDataChannelArray( data.map(_.asInstanceOf[XDataChannel]).toVector )
      } else {
        throw new IllegalArgumentException("Cannot write given list of data as .klusta.dat file!")
      }

    val fileObj = new RandomAccessFile(actualFileName, "w")(ByteConverterLittleEndian)

    val (start: Int, end: Int) = span.getStartEndIndexes( actualXData.segmentLengths(segment) )
    val tempArray: Array[Short] = new Array[Short]( (end-start +1) * actualXData.channelCount )
    var index: Int = 0

    for(frame <- start to end){
      for(channel <- 0 until actualXData.channelCount ){
        tempArray(index) = (actualXData.readPointImpl( channel, frame, segment ) / actualXData.xBits).toShort
      }
    }

    fileObj.writeInt16(tempArray)
    fileObj.close()

  }


}
