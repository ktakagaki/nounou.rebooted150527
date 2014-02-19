package nounou.data.formats

import nounou._
import nounou.data._
import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}

/**
 * @author ktakagaki
 * @date 2/2/14.
 */
object FileWriterKlustaDAT extends XDataFileWriter {

  override def write(fileName: String, data: XData) = write(fileName, data, 0, FrameRange.All)


  def write(fileName: String, data: XData, segment: Int, range: FrameRange ): Unit = {

    val actualFileName = {
      if( fileName.toLowerCase.endsWith(".klusta.dat") ) fileName
      else fileName + ".klusta.dat"
    }

    val fileObj = new RandomAccessFile(actualFileName, "rw")(ByteConverterLittleEndian)

    val realRange = range.getValidRange(data.segmentLengths(segment))
    val writeFrameLength = 1024 //32kb if 16 channels

    var currentFrameStart = 0
    if(realRange.length > writeFrameLength){
      var currentIndex = 0
      while(currentFrameStart + writeFrameLength < realRange.length){
        val writeArray = new Array[Short]( data.channelCount * writeFrameLength )
        currentIndex = 0
        for(fr <- 0 until writeFrameLength)
          for(ch <- 0 until data.channelCount) {
            writeArray( currentIndex ) = (data.readPoint(ch, fr + currentFrameStart, segment) / data.xBits).toShort
            currentIndex += 1
          }
        fileObj.writeInt16( writeArray)
        currentFrameStart += writeFrameLength
      }

      val writeArray = new Array[Short]( data.channelCount * (realRange.length - currentFrameStart) )
      currentIndex = 0
      for(fr <- currentFrameStart until realRange.length)
        for(ch <- 0 until data.channelCount) {
          writeArray( currentIndex ) = (data.readPoint(ch, fr, segment) / data.xBits).toShort
          currentIndex += 1
        }
      fileObj.writeInt16( writeArray)
      fileObj.close()

    }
  }


}
