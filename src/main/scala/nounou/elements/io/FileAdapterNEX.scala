//package nounou.obj.io
//
//import nounou.obj._
//import java.io.File
//import breeze.io.{RandomAccessFile, ByteConverterLittleEndian}
//import nounou.obj.data.NNDataPreloaded
//import scala.collection.immutable.TreeMap
//import scala.collection.mutable.ArrayBuffer
//import breeze.linalg.{DenseVector => DV}
//import nounou.obj.headers.{HeaderValue, NNHeader}
//
///**Reads in single NEX files. Partial implementation, with only waveforms processed.
//*
//* @author K. Takagaki
//*/
//object FileAdapterNEX extends FileAdapter {
//
//  override val canWriteExt: Array[String] = List[String]()
//  override def writeImpl(file: File, data: NNObject, options: OptFileAdapter) = writeCannotImpl(file, data, options)
//  override val canLoadExt: Array[String] = List[String]( "ncs" )
//
//
//  override def loadImpl(file: File): Array[NNObject] = {
//
//    val fHand = new RandomAccessFile(file, "r")(ByteConverterLittleEndian)
//    var tempRet: ArrayBuffer[NNObject] = ArrayBuffer[NNObject]()
//
//    val magic = fHand.readInt()
//    if ( magic != 827868494 ) throw new InvalidFileFormatException("Invalid NEX file!", null)
//
//    // <editor-fold defaultstate="collapsed" desc=" header ">
//
//    val nexFileVersion: Int = fHand.readInt
//    val nexFileComment: String = new String(fHand.readByte(256))
//    val nexFileFreq: Double = fHand.readDouble
//    val nexFileTBeg: Double = fHand.readInt / nexFileFreq
//    val nexFileTEnd: Double = fHand.readInt / nexFileFreq
//    val nexFileNVar: Int = fHand.readInt
//
//    val header = new NNHeaderNEX(
//      TreeMap[String, HeaderValue](
//        "nexFileVersion" -> HeaderValue(nexFileVersion),
//        "nexFileComment" -> HeaderValue(nexFileComment),
//        "nexFileFreq" -> HeaderValue(nexFileFreq),
//        "nexFileTBeg" -> HeaderValue(nexFileTBeg),
//        "nexFileTEnd" -> HeaderValue(nexFileTEnd),
//        "nexFileNVar" -> HeaderValue(nexFileNVar)
//      )
//    )
//
//    tempRet += header
//
//    // </editor-fold>
//
//    fHand.skipBytes(260)
//
//    if(nexFileNVar != 1){
//	  throw new InvalidFileFormatException(file + " has a waveform with more than one wave, this is unsupported", null)//ToDo
//    }
//
////	for(i <- 1 to nexFileNVar) {
//      val recType = 	fHand.readInt
//      val varVersion = 	fHand.readInt
//      val name = new String(fHand.readByte(64))
//      val offset = 		fHand.readInt
//      val n = 			fHand.readInt
//      val wireNumber = 	fHand.readInt
//      val unitNumber = 	fHand.readInt
//      val gain = 		fHand.readInt
//      val filter =		fHand.readInt
//      val xPos = 		fHand.readDouble
//      val yPos = 		fHand.readDouble
//      val wFrequency =  fHand.readDouble
//      val ADtoMV =		fHand.readDouble
//      val NPointsWave = fHand.readInt
//      val NMarkers =	fHand.readInt
//      val MarkerLength = fHand.readInt
//      val MVOffset =	fHand.readDouble
//      val filePosition =  fHand.getFilePointer
//
//      //TODO 3: Header again here
//
//      recType match {
//        //case 0 //neurons
//        //case 1 //events
//        //case 2 //intervals
//        case 5 => { //continuous data
//            val tempData = DV.zeros[Int](NPointsWave)
//            val extraBits = 1024
//
//          	fHand.seek(offset)
//            fHand.skipBytes(n*4) //timestamps, discard
//            fHand.skipBytes(n*4) //fragmetStarts, discard
//
//          	//tempData = Array.fill(NPointsWave){fHand.readShort.toInt * extraBits}
//            //tempData = fHand.readShort(NPointsWave) map ( _.toInt * extraBits)
//            val tempDataShort = fHand.readShort(NPointsWave)
//            //tempData = new Array[Int](NPointsWave)
//            var c = 0
//            while(c < NPointsWave){
//              tempData(c) = tempDataShort(c).toInt * extraBits
//              c += 1
//            }
//
//          tempRet += new NNDataChannelPreloadedSingleSegment(  data = tempData,
//                                                              xBits = extraBits,
//                                                              absGain = ADtoMV / extraBits,
//                                                              absOffset = MVOffset,
//                                                              absUnit = "mV",
//                                                              scaleMax = Short.MaxValue.toInt * extraBits,
//                                                              scaleMin = Short.MinValue.toInt * extraBits,
//                                                              channelName = name,
//                                                              segmentStartTS = nexFileTBeg.toLong, //TODO 1: Must fix this placeholder!!!
//                                                              sampleRate = nexFileFreq
//          )
//
//          //          tempRet += new XDataPreloaded( data = Vector(Vector(tempData.toVector)),
////                                        xBits = extraBits,
////                                        absGain = ADtoMV / extraBits,
////                                        absOffset = MVOffset,
////                                        absUnit = "mV",
////                                        channelNames = Vector[String]( name ),
////                                        segmentStartTSs = Vector[Long](nexFileTBeg.toLong), //TODO 1: Must fix this placeholder!!!
////                                        sampleRate = nexFileFreq,
////                                        layout = XLayoutNull
////                                          )
//         }/*case 5*/
//       } /*recType match*/
////    } /*for(i <- 1 to nexFileNVar)*/
//
//    tempRet.toArray
//  }//loadImpl
//
//
//}
//
//class NNHeaderNEX(override val header: TreeMap[String, HeaderValue]) extends NNHeader(header)
