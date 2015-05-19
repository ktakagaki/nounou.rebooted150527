//package nounou.elements.filters
//
//import org.junit.runner.RunWith
//import org.scalatest.junit.JUnitRunner
//import org.scalatest.FunSuite
//import nounou.NNDataReader
//import java.io.File
//
///**
// * @author ktakagaki
// * //@date 05/29/2014.
// */
//@RunWith(classOf[JUnitRunner])
//class NNDataFilterBufferTest  extends FunSuite {
//
//  val bigTestFilesHead = "V:/data/project.SPP/Nlx/SPP010/2013-12-02_17-07-31/"
//  val bigTestFiles = Array[String](
//    bigTestFilesHead + "Tet4a.ncs",
//    bigTestFilesHead + "Tet4b.ncs",
//    bigTestFilesHead + "Tet4c.ncs",
//    bigTestFilesHead + "Tet4d.ncs"
//  )
////  val testFileTet4a = new File( getClass.getResource("/_testFiles/Neuralynx/t130911/Tet4a.ncs").getPath() )
//
////  test("Spike Dectector Peak Width Profiling") {
////    val reader = new NNDataReader
////    reader.load(bigTestFiles)
////    val buffer = new XDataFilterBuffer(reader.dataORI)
////
////    val hashKey1 = buffer.bufferHashKey(123, 300, 5)
//////    println(hashKey1)
//////    println(buffer.bufferHashKeyToChannel(hashKey1))
//////    println(buffer.bufferHashKeyToPage(hashKey1))
//////    println(buffer.bufferHashKeyToSegment(hashKey1))
//////    println(buffer.maxChannel)
//////    println(buffer.maxPage)
//////    println(buffer.maxPageChannel)
//////    println(buffer.maxInt64)
////    assert(buffer.bufferHashKeyToChannel(hashKey1) == 123)
////    assert(buffer.bufferHashKeyToPage(hashKey1) == 300)
////    assert(buffer.bufferHashKeyToSegment(hashKey1) == 5)
////  }
//
//}