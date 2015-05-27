//package nounou.obj.io
//
//import nounou._
//import nounou.obj._
//import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}
//import java.io.File
//
//import nounou.obj.ranges.FrRange$
//
///**
// * @author ktakagaki
// * //@date 2/2/14.
// */
//object FileAdapterKlustaDAT extends FileAdapter {
//
//  override val canWriteExt: List[String] = List[String]("klusta.dat")
//  override val canLoadExt: List[String] = List[String]()
//
// // override def write(fileName: String, data: XData) = write(fileName, data, 0, RangeFr.All)
//
//    def loadImpl(file: File) = loadCannotImpl(file)
////  implicit val canWriteXData: CanWrite[XData] = new CanWrite[XData]{
////
//  def writeImpl(file: File, data: NNObject, opt: OptFileAdapter): Unit = {
//    data match {
//      case x: NNData => writeImpl(file, x, opt)
//      case x: NNObject => writeCannotImpl(file, x, opt)
//    }
//  }
//
//  def writeImpl(fileName: String, data: NNData, opt: OptFileAdapter): Unit = {
//
//      val actualFileName = {
//        if( fileName.toLowerCase.endsWith(".klusta.dat") ) fileName
//        else fileName + ".klusta.dat"
//      }
//      val fileObj = new RandomAccessFile(actualFileName, "rw")(ByteConverterLittleEndian)
//
//      val parsedOpt = opt match {
//        case OptFileAdapter.Automatic => new OptFileAdapter.XDataFrames()
//        case x: OptFileAdapter.XDataFrames => x
//        case _ => loggerError("{} is not a valid option for OptFileAdapter!", opt.toString ); throw new IllegalArgumentException
//      }
//
//      val realRange = parsedOpt.range.getSampleRangeValid( data )
//      val writeFrameLength = 1024 //32kb if 16 channels
//
//      var currentFrameStart = 0
//      if(realRange.length > writeFrameLength){
//        var currentIndex = 0
//        while(currentFrameStart + writeFrameLength < realRange.length){
//          val writeArray = new Array[Short]( data.channelCount * writeFrameLength )
//          currentIndex = 0
//          val writeData = for(ch <- 0 until data.channelCount) yield data.readTraceDV(ch, FrRange(currentFrameStart, currentFrameStart + writeFrameLength - 1))//, OptSegment(0)))
//          for(fr <- 0 until writeFrameLength)
//            for(ch <- 0 until data.channelCount) {
//              writeArray( currentIndex ) = ( writeData(ch)(fr) / data.xBits).toShort
//              currentIndex += 1
//            }
//          fileObj.writeInt16( writeArray)
//          currentFrameStart += writeFrameLength
//        }
//
//        val writeArray = new Array[Short]( data.channelCount * (realRange.length - currentFrameStart) )
//        currentIndex = 0
//        for(fr <- currentFrameStart until realRange.length)
//          for(ch <- 0 until data.channelCount) {
//            writeArray( currentIndex ) = (data.readPoint(ch, fr/*, parsedOpt.range.segment()*/) / data.xBits).toShort
//            currentIndex += 1
//          }
//        fileObj.writeInt16( writeArray)
//        fileObj.close()
//
//      }
//  }
//
//
//}
