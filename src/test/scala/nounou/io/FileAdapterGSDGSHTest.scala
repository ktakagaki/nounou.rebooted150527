//package nounou.io
//
//import org.scalatest.FunSuite
//import java.io.File
//import nounou.NNDataReader
//import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}
//
///**
// * @author ktakagaki
// * //@date 2/26/14.
// */
//class FileAdapterGSDGSHTest extends FunSuite {
//
//
////  test("load test"){
////    val testFile = new File( getClass.getResource("/_testFiles/Micam/mag130712008A_3.gsd").getPath() )
////
////    val dataList = FileAdapterGSDGSH.load(testFile)
////
////    println(dataList(0).toString)
////    println(dataList(1).toString)
////    println(dataList(2).toString)
////
////  }
//
//
//  test("memory leak"){
//
////    val dataList = FileAdapterGSDGSH.load("E:/data/Micam/dc-stimulation/140224/mag140224006A.gsd")
////    //dataReader.load()
////    val dataReader = new NNDataReader()
////    dataReader.load("E:/data/Micam/dc-stimulation/140224/mag140224006A.gsd")
////
////    println(dataReader.header.toString)
////    println(dataReader.data.toStringChain)
//
////    println(dataList(0).toString)
////    println(dataList(1).toString)
////    println(dataList(2).toString)
//
//  }
//
////  test("load manual tests"){
////
////    val raf = new RandomAccessFile(testFile)(ByteConverterLittleEndian)
////    raf.seek(256)
////    val nDataXsize = raf.readInt16()     //256... number of pixels on X axis
////    val nDataYsize = raf.readInt16()     //258... number of pixels on Y axis
////    val nLeftSkip = raf.readInt16()      //260
////    val nTopSkip = raf.readInt16()       //262
////    val nImgXsize = raf.readInt16()      //264
////    val nImgYsize = raf.readInt16()      //266
////    val nFrameSize = raf.readInt16()     //268... number of frames
////    val nOrgImgXsize = raf.readInt16()   //270
////    val nOrgImgYsize = raf.readInt16()   //272
////    val nOrgFrmSize = raf.readInt16()    //274
////    val nShift = raf.readInt16()         //276
////    val nDummy = raf.readInt16()         //278
////    val dAverage = raf.readFloat()       //280
////    val dSampleTime = raf.readFloat()    //284... sampling rate (msec)
////    val sampleRate = 1000d/dSampleTime
////    val dOrgSampleTime = raf.readFloat() //288
////    val dDummy = raf.readFloat()         //292
////    //val chDum32 = raf.readFloat()        //296
////
////    raf.seek(328)
////    val AUXnChanum = raf.readInt16()        //328
////    val AUXnRate = raf.readInt16()          //330... temporal resolution
////    val sampleRateAux = 1000d/AUXnRate
////    val AUXnOfset = raf.readInt16()         //332
////    val AUXnChNext = raf.readInt16()        //334
////    val AUXnTimeNext = raf.readInt16()      //336
////    val AUXnFrameSize = raf.readInt16()     //338... number of frames
////    val AUXnShift = raf.readInt16()         //340
////    val AUXnDummy1 = raf.readInt16()        //342
////    val AUXnDummy2 = raf.readInt16()        //344
////    val AUXnDummy3 = raf.readInt16()        //346
////
////    println("hello")
////  }
//
//}
