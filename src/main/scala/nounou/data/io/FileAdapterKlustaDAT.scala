package nounou.data.io

import nounou._
import nounou.data._
import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}

/**
 * @author ktakagaki
 * @date 2/2/14.
 */
class FileAdapterKlustaDAT extends FileAdapter {

  override val canWriteExt: List[String] = List[String]()
  override val canLoadExt: List[String] = List[String]()

 // override def write(fileName: String, data: XData) = write(fileName, data, 0, FrameRange.All)


  implicit val canWriteXData: CanWrite[XData] = new CanWrite[XData]{

    def apply(fileName: String, data: XData, opt: OptFileAdapter): Unit = {

      val actualFileName = {
        if( fileName.toLowerCase.endsWith(".klusta.dat") ) fileName
        else fileName + ".klusta.dat"
      }
      val fileObj = new RandomAccessFile(actualFileName, "rw")(ByteConverterLittleEndian)

      val parsedOpt = opt match {
        case OptFileAdapter.Automatic => new OptFileAdapter.XData()
        case x: OptFileAdapter.XData => x
        case _ => loggerError("{} is not a valid option for OptFileAdapter!", opt.toString ); throw new IllegalArgumentException
      }

      val realRange = parsedOpt.range.getValidRange( data.segmentLengths(parsedOpt.segment) )
      val writeFrameLength = 1024 //32kb if 16 channels

      var currentFrameStart = 0
      if(realRange.length > writeFrameLength){
        var currentIndex = 0
        while(currentFrameStart + writeFrameLength < realRange.length){
          val writeArray = new Array[Short]( data.channelCount * writeFrameLength )
          currentIndex = 0
          val writeData = for(ch <- 0 until data.channelCount) yield data.readTrace(ch, currentFrameStart to currentFrameStart + writeFrameLength - 1, 0)
          for(fr <- 0 until writeFrameLength)
            for(ch <- 0 until data.channelCount) {
              writeArray( currentIndex ) = ( writeData(ch)(fr) / data.xBits).toShort
              currentIndex += 1
            }
          fileObj.writeInt16( writeArray)
          currentFrameStart += writeFrameLength
        }

        val writeArray = new Array[Short]( data.channelCount * (realRange.length - currentFrameStart) )
        currentIndex = 0
        for(fr <- currentFrameStart until realRange.length)
          for(ch <- 0 until data.channelCount) {
            writeArray( currentIndex ) = (data.readPoint(ch, fr, parsedOpt.segment) / data.xBits).toShort
            currentIndex += 1
          }
        fileObj.writeInt16( writeArray)
        fileObj.close()

      }
    }
  }


}
