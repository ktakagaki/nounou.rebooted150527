package nounou.data.traits


import java.lang.IllegalArgumentException

/**
 * @author ktakagaki
 * @date 2/12/14.
 */
class FramesTest extends XPreloadedTestDataTest {


  // <editor-fold defaultstate="collapsed" desc=" tsToFrameSegment ">

  test("tsToFrameSegment"){

    assert(testData.convertTS2FS(8000000) == (-2, 0), "testData.tsToFrameSegment(8000000)")
//      intercept[IllegalArgumentException]{ testData.tsToFrameSegment(8000000) }
  assert(testData.convertTS2FS(10000000) == (0, 0), "testData.tsToFrameSegment(10000000)")
//      intercept[IllegalArgumentException]{testData.tsToFrameSegment(10000000) == (0, 0)}
    assert(testData.convertTS2FS(19000000) == (9, 0), "testData.tsToFrameSegment(19000000)")
//    assert(testData.tsToFrameSegment(19000000) == (9, 0), "testData.tsToFrameSegment(19000000, false)")
    assert(testData.convertTS2FS(20000000) == (10, 0), "testData.tsToFrameSegment(20000000)")
//      intercept[IllegalArgumentException]{testData.tsToFrameSegment(20000000) == (9, 0)}
    assert(testData.convertTS2FS(23000000) == (13, 0), "testData.tsToFrameSegment(23000000)")
//      intercept[IllegalArgumentException]{testData.tsToFrameSegment(23000000) == (9, 0)}
    assert(testData.convertTS2FS(27000000) == (-3, 1), "testData.tsToFrameSegment(27000000)")
//      intercept[IllegalArgumentException]{testData.tsToFrameSegment(27000000) == (0, 1)}
    assert(testData.convertTS2FS(35000000) == (5, 1), "testData.tsToFrameSegment(20000000)")
//    assert(testData.tsToFrameSegment(35000000) == (5, 1), "testData.tsToFrameSegment(20000000, false)")
    assert(testData.convertTS2FS(55000000) == (25, 1), "testData.tsToFrameSegment(55000000)")
//        intercept[IllegalArgumentException]{testData.tsToFrameSegment(55000000) == (19, 1)}
  }

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" tsToClosestSegment ">

  test("tsToClosestSegment"){

    assert(testData.tsToClosestSg(8000000) == 0, "testData.tsToClosestSegment(8000000)")
    assert(testData.tsToClosestSg(10000000) == 0, "testData.tsToClosestSegment(10000000)")
    assert(testData.tsToClosestSg(19000000) == 0, "testData.tsToClosestSegment(19000000)")
    assert(testData.tsToClosestSg(20000000) == 0, "testData.tsToClosestSegment(20000000)")
    assert(testData.tsToClosestSg(23000000) == 0, "testData.tsToClosestSegment(23000000)")
    assert(testData.tsToClosestSg(27000000) == 1, "testData.tsToClosestSegment(27000000)")
    assert(testData.tsToClosestSg(35000000) == 1, "testData.tsToClosestSegment(20000000)")
    assert(testData.tsToClosestSg(55000000) == 1, "testData.tsToClosestSegment(55000000)")
  }

  // </editor-fold>
}
