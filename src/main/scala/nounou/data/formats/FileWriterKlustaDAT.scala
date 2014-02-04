package nounou.data.formats

import nounou._
import nounou.data._
import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}

/**
 * @author ktakagaki
 * @date 2/2/14.
 */
object FileWriterKlustaDAT extends FileWriter {

  override def write(fileName: String, data: List[X]) = write(fileName, data, FrameRange.All,  0)

  def write(fileName: String, data: List[X], range: FrameRange, segment: Int = 0 ): Unit = {

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

    //val realRangegetRangeWithoutNegativeIndexes( actualXData.segmentLengths(segment) )
    val tempTraces: Array[Array[Short]] =
            Array.tabulate(actualXData.channelCount)( (ch: Int) => actualXData.readTrace(ch, range, segment).map( (i: Int) => ( i / actualXData.xBits).toShort ).toArray  )
    val tempArray: Array[Short] = new Array[Short]( range.length * actualXData.channelCount )

    var index: Int = 0
    for(frame <- range ){
      for(channel <- 0 until actualXData.channelCount ){
        tempArray(index) = tempTraces(channel)(frame)
        index += 1
      }
    }

    fileObj.writeInt16(tempArray)
    fileObj.close()

  }


}
