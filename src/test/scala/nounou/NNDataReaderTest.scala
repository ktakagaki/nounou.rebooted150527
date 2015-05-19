//package nounou
//
//import org.scalatest.FunSuite
//import java.io.File
//import org.junit.runner.RunWith
//import org.scalatest.junit.JUnitRunner
//
///**
// * @author ktakagaki
// * // //@date 2/14/14.
// */
//@RunWith(classOf[JUnitRunner])
//class NNDataReaderTest extends FunSuite {
//
////  val testFileTet4s = Array[String](
////    getClass.getResource("/_testFiles/Neuralynx/t130911/Tet4a.ncs").getPath(),
////    getClass.getResource("/_testFiles/Neuralynx/t130911/Tet4b.ncs").getPath(),
////    getClass.getResource("/_testFiles/Neuralynx/t130911/Tet4c.ncs").getPath(),
////    getClass.getResource("/_testFiles/Neuralynx/t130911/Tet4d.ncs").getPath()
////  )
////
////  test("loading"){
////
////    val reader = new NNDataReader
////
////    for(c <- 0 to 99){
////      reader.load( testFileTet4s )
////    }
////
////  }
//
////    val bigTestFilesHead = "V:/docs/k.VSDdata/project.SPP/Nlx/SPP010/2013-12-02_17-07-31/"
////    val bigTestFiles = Array[String](
////      bigTestFilesHead + "Tet4a.ncs",
////      bigTestFilesHead + "Tet4b.ncs",
////      bigTestFilesHead + "Tet4c.ncs",
////      bigTestFilesHead + "Tet4d.ncs"
////    )
////
////   test("Optimize masking") {
////     val reader = new NNDataReader
////     reader.load(bigTestFiles)
////     println(reader.toStringChain())
////     reader.maskMovementArtifacts(500d, 800d, 10d, 400d, 100, 150000)
////   }
////
//
//}
