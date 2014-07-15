package nounou.data.io

import nounou._
import nounou.data._
import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}
import java.io.File

/**
 * @author ktakagaki
 * @date 2/2/14.
 */
object FileAdapterKlustaDAT extends FileAdapter {

  override val canWriteExt: List[String] = List[String]("klusta.dat")
  override val canLoadExt: List[String] = List[String]()

 // override def write(fileName: String, data: XData) = write(fileName, data, 0, RangeFr.All)

    def loadImpl(file: File) = loadCannotImpl(file)
//  implicit val canWriteXData: CanWrite[XData] = new CanWrite[XData]{
//
  def writeImpl(file: File, data: X, opt: OptFileAdapter): Unit = {
    data match {
      case x: XData => writeImpl(file, x, opt)
      case x: X => writeCannotImpl(file, x, opt)
    }
  }

  def writeImpl(fileName: String, data: XData, opt: OptFileAdapter): Unit = {

      val actualFileName = {
        if( fileName.toLowerCase.endsWith(".klusta.dat") ) fileName
        else fileName + ".klusta.dat"
      }
      val fileObj = new RandomAccessFile(actualFileName, "rw")(ByteConverterLittleEndian)

      val parsedOpt = opt match {
        case OptFileAdapter.Automatic => new OptFileAdapter.XDataFrames()
        case x: OptFileAdapter.XDataFrames => x
        case _ => loggerError("{} is not a valid option for OptFileAdapter!", opt.toString ); throw new IllegalArgumentException
      }

      val realRange = parsedOpt.range.getValidRange( data )
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
