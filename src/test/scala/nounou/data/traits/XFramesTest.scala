package nounou.data.traits


import java.lang.IllegalArgumentException

/**
 * @author ktakagaki
 * @date 2/12/14.
 */
class XFramesTest extends XPreloadedTestDataTest {


  // <editor-fold defaultstate="collapsed" desc=" tsToFrameSegment ">

  test("tsToFrameSegment"){

    assert(testData.tsToFrameSegment(8000000, true) == (-2, 0), "testData.tsToFrameSegment(8000000)")
      intercept[IllegalArgumentException]{ testData.tsToFrameSegment(8000000) }
  assert(testData.tsToFrameSegment(10000000, true) == (0, 0), "testData.tsToFrameSegment(10000000)")
      intercept[IllegalArgumentException]{testData.tsToFrameSegment(10000000) == (0, 0)}
    assert(testData.tsToFrameSegment(19000000, true) == (9, 0), "testData.tsToFrameSegment(19000000)")
    assert(testData.tsToFrameSegment(19000000) == (9, 0), "testData.tsToFrameSegment(19000000, false)")
    assert(testData.tsToFrameSegment(20000000, true) == (10, 0), "testData.tsToFrameSegment(20000000)")
      intercept[IllegalArgumentException]{testData.tsToFrameSegment(20000000) == (9, 0)}
    assert(testData.tsToFrameSegment(23000000, true) == (13, 0), "testData.tsToFrameSegment(23000000)")
      intercept[IllegalArgumentException]{testData.tsToFrameSegment(23000000) == (9, 0)}
    assert(testData.tsToFrameSegment(27000000, true) == (-3, 1), "testData.tsToFrameSegment(27000000)")
      intercept[IllegalArgumentException]{testData.tsToFrameSegment(27000000) == (0, 1)}
    assert(testData.tsToFrameSegment(35000000, true) == (5, 1), "testData.tsToFrameSegment(20000000)")
    assert(testData.tsToFrameSegment(35000000) == (5, 1), "testData.tsToFrameSegment(20000000, false)")
    assert(testData.tsToFrameSegment(55000000, true) == (25, 1), "testData.tsToFrameSegment(55000000)")
        intercept[IllegalArgumentException]{testData.tsToFrameSegment(55000000) == (19, 1)}
//    println("testData.tsToFrameSegment(8000000): " + testData.tsToFrameSegment(8000000))
//    println("testData.tsToFrameSegment(8000000, false): " + testData.tsToFrameSegment(8000000, false))
//    println("testData.tsToFrameSegment(10000000): " + testData.tsToFrameSegment(10000000))
//    println("testData.tsToFrameSegment(10000000, false): " + testData.tsToFrameSegment(10000000, false))
//    println("testData.tsToFrameSegment(19000000): " + testData.tsToFrameSegment(19000000))
//    println("testData.tsToFrameSegment(19000000, false): " + testData.tsToFrameSegment(19000000, false))
//    println("testData.tsToFrameSegment(20000000): " + testData.tsToFrameSegment(20000000))
//    println("testData.tsToFrameSegment(20000000, false): " + testData.tsToFrameSegment(20000000, false))
//    println("testData.tsToFrameSegment(23000000): " + testData.tsToFrameSegment(23000000))
//    println("testData.tsToFrameSegment(23000000, false): " + testData.tsToFrameSegment(23000000, false))
//    println("testData.tsToFrameSegment(27000000): " + testData.tsToFrameSegment(27000000))
//    println("testData.tsToFrameSegment(27000000, false): " + testData.tsToFrameSegment(27000000, false))
//    println("testData.tsToFrameSegment(35000000): " + testData.tsToFrameSegment(35000000))
//    println("testData.tsToFrameSegment(35000000, false): " + testData.tsToFrameSegment(35000000, false))
//    println("testData.tsToFrameSegment(55000000): " + testData.tsToFrameSegment(55000000))
//    println("testData.tsToFrameSegment(55000000, false): " + testData.tsToFrameSegment(55000000, false))
  }

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" tsToClosestSegment ">

  test("tsToClosestSegment"){

    assert(testData.tsToClosestSegment(8000000) == 0, "testData.tsToClosestSegment(8000000)")
    assert(testData.tsToClosestSegment(10000000) == 0, "testData.tsToClosestSegment(10000000)")
    assert(testData.tsToClosestSegment(19000000) == 0, "testData.tsToClosestSegment(19000000)")
    assert(testData.tsToClosestSegment(20000000) == 0, "testData.tsToClosestSegment(20000000)")
    assert(testData.tsToClosestSegment(23000000) == 0, "testData.tsToClosestSegment(23000000)")
    assert(testData.tsToClosestSegment(27000000) == 1, "testData.tsToClosestSegment(27000000)")
    assert(testData.tsToClosestSegment(35000000) == 1, "testData.tsToClosestSegment(20000000)")
    assert(testData.tsToClosestSegment(55000000) == 1, "testData.tsToClosestSegment(55000000)")
  }

  // </editor-fold>
}
