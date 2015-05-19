//package nounou.elements.traits
//
//
//import java.lang.IllegalArgumentException
//
///**
// * @author ktakagaki
// * //@date 2/12/14.
// */
//class FramesTest extends XPreloadedTestDataTest {
//
//
//  // <editor-fold defaultstate="collapsed" desc=" tsToFrameSegment ">
//
//  test("tsToFrameSegment"){
//
//    assert(testData.convertTStoFS(8000000) == (-2, 0), "testData.tsToFrameSegment(8000000)")
////      intercept[IllegalArgumentException]{ testData.tsToFrameSegment(8000000) }
//  assert(testData.convertTStoFS(10000000) == (0, 0), "testData.tsToFrameSegment(10000000)")
////      intercept[IllegalArgumentException]{testData.tsToFrameSegment(10000000) == (0, 0)}
//    assert(testData.convertTStoFS(19000000) == (9, 0), "testData.tsToFrameSegment(19000000)")
////    assert(testData.tsToFrameSegment(19000000) == (9, 0), "testData.tsToFrameSegment(19000000, false)")
//    assert(testData.convertTStoFS(20000000) == (10, 0), "testData.tsToFrameSegment(20000000)")
////      intercept[IllegalArgumentException]{testData.tsToFrameSegment(20000000) == (9, 0)}
//    assert(testData.convertTStoFS(23000000) == (13, 0), "testData.tsToFrameSegment(23000000)")
////      intercept[IllegalArgumentException]{testData.tsToFrameSegment(23000000) == (9, 0)}
//    assert(testData.convertTStoFS(27000000) == (-3, 1), "testData.tsToFrameSegment(27000000)")
////      intercept[IllegalArgumentException]{testData.tsToFrameSegment(27000000) == (0, 1)}
//    assert(testData.convertTStoFS(35000000) == (5, 1), "testData.tsToFrameSegment(20000000)")
////    assert(testData.tsToFrameSegment(35000000) == (5, 1), "testData.tsToFrameSegment(20000000, false)")
//    assert(testData.convertTStoFS(55000000) == (25, 1), "testData.tsToFrameSegment(55000000)")
////        intercept[IllegalArgumentException]{testData.tsToFrameSegment(55000000) == (19, 1)}
//  }
//
//  // </editor-fold>
//  // <editor-fold defaultstate="collapsed" desc=" tsToClosestSegment ">
//
//  test("tsToClosestSegment"){
//
//    assert(testData.convertTStoClosestSegment(8000000) == 0, "testData.tsToClosestSegment(8000000)")
//    assert(testData.convertTStoClosestSegment(10000000) == 0, "testData.tsToClosestSegment(10000000)")
//    assert(testData.convertTStoClosestSegment(19000000) == 0, "testData.tsToClosestSegment(19000000)")
//    assert(testData.convertTStoClosestSegment(20000000) == 0, "testData.tsToClosestSegment(20000000)")
//    assert(testData.convertTStoClosestSegment(23000000) == 0, "testData.tsToClosestSegment(23000000)")
//    assert(testData.convertTStoClosestSegment(27000000) == 1, "testData.tsToClosestSegment(27000000)")
//    assert(testData.convertTStoClosestSegment(35000000) == 1, "testData.tsToClosestSegment(20000000)")
//    assert(testData.convertTStoClosestSegment(55000000) == 1, "testData.tsToClosestSegment(55000000)")
//  }
//
//  // </editor-fold>
//}
