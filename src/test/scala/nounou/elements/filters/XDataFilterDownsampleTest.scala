//package nounou.elements.filters
//
//import nounou.elements.{NNDataChannel, NNDataChannelArray}
//import nounou.io.neuralynx.FileAdapterNCS
//import org.scalatest.FunSuite
//
//import scala.nounou.obj.data.filters.NNDataFilterDownsample
//
///**
// * @author ktakagaki
// * //@date 2/19/14.
// */
//class XDataFilterDownsampleTest extends FunSuite {
//
//  val testFile = getClass.getResource("/_testFiles/Neuralynx/t130911/Tet4a.ncs").getPath()
//
//  test("check timing info"){
//    val ori = new NNDataChannelArray( Vector[NNDataChannel]( FileAdapterNCS.load(testFile).head.asInstanceOf[NNDataChannel] ) )
//    val obj = new NNDataFilterDownsample( ori, 16 )
//
//    assert(obj.sampleRate == 2000.0)
//    assert(obj.segmentLength == Array(208704, 7712, 422176))
//    assert(obj.segmentStartTs == Array(-9223372034262519500L, -9223372032258605500L, -9223372032227369500L))
//    assert(obj.segmentEndTs == Array(-9223372034158168000L, -9223372032254750000L, -9223372032016282000L))
////    assert(obj.frsgToTs(208704-1, 0) == -9223372034158168000L)
////    intercept[IllegalArgumentException](obj.frsgToTs(208704, 0) == -9223372034158168000L)
////    assert(obj.frsgToTs(7712-1, 1) == -9223372032254750000L)
////    assert(obj.frsgToTs(422176-1, 2) == -9223372032016282000L)
//    assert(obj.convertTStoFS(-9223372032258605500L) == (0, 1) )
//    assert(obj.convertTStoFS(-9223372032254750000L) == (7712-1, 1) )
//    assert(obj.convertTStoClosestSegment(-9223372034262519500L) == 0)
//    assert(obj.convertTStoClosestSegment(-9223372032016282000L) == 2)
////    println(ori.sampleRate)
////    println(obj.sampleRate)
////    println(obj.sampleInterval)
////    println(ori.segmentLengths)
////    println(obj.segmentLengths)
////    println(ori.segmentStartTSs)
////    println(obj.segmentStartTSs)
////    println(ori.segmentEndTSs)
////    println(obj.segmentEndTSs)
//  }
//}
