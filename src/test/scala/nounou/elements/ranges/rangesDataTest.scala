//package nounou.elements.ranges
//
//import java.io.File
//
//import breeze.linalg.DenseVector
//import nounou.{OptSegment, NNDataReader}
//import nounou.io.neuralynx.NNDataChannelNCS
//import org.junit.runner.RunWith
//import org.scalatest.FunSuite
//import org.scalatest.junit.JUnitRunner
//
///**
// * @author ktakagaki
// * //@date 2/16/14.
// */
//@RunWith(classOf[JUnitRunner])
//class rangesDataTest extends FunSuite {
//
//  test("RangeFr All") {
//
//    val testFileE04LC_CSC1 = new File("C:\\prog\\_gh\\_kt\\nounou.testfiles\\Neuralynx\\E04LC\\CSC1.ncs")
//    val temp = NNDataReader.load(testFileE04LC_CSC1).apply(0)
//    assert( temp.isInstanceOf[NNDataChannelNCS] )
//    val dataObj = temp.asInstanceOf[NNDataChannelNCS]
//
//    assert(dataObj.segmentLength(0)==2546176)
//    assert(dataObj.readTrace( FrameRange(0, 9, 1, OptSegment(0))) ==
//      DenseVector(-1564672, -1885184, -1312768, -686080, -512000, -267264, 312320, 630784, 272384, -385024)
//    )
////    println(dataObj.readTrace( RangeFr(0, 9, 2, OptSegment(0))))
//
//  }
//
//
//}
