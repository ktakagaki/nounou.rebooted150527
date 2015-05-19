//package nounou.elements.ranges
//
//import org.junit.runner.RunWith
//import org.scalatest.FunSuite
//import org.scalatest.junit.JUnitRunner
//
///**
// * @author ktakagaki
// * //@date 2/16/14.
// */
//@RunWith(classOf[JUnitRunner])
//class FrRangeTest extends FunSuite {
//
//  test("length odd"){
//
//    val totalLen = 5
//    val totalLen2 = 7
//
//    val testFrRange11 = FrameRange(0, 4, 1)
//    val testFrRange21= FrameRange(-4, 4, 1)
//    val testFrRange31= FrameRange(0, 7, 1)
//    val testFrRange41= FrameRange(-3, 8, 1)
//
//    val testFrRange12 = FrameRange(0, 4, 2)
//    val testFrRange22= FrameRange(-4, 4, 2)
//    val testFrRange32= FrameRange(0, 7, 2)
//    val testFrRange42= FrameRange(-3, 8, 2)
//
//    val testFrRange52= FrameRange(3, 8, 2)
//    val testFrRange62= FrameRange(5, 8, 2)
//
//    // <editor-fold defaultstate="collapsed" desc=" intervalContains/intervalMod ">
//    assert( testFrRange11.intervalContains(-4, -1, 2) == 2 )
//    assert( testFrRange11.intervalContains(-4, 0, 2) == 3 )
//    assert( testFrRange11.intervalMod(-4, -1, 2) == 1 )
//    assert( testFrRange11.intervalMod(-4, -2, 2) == 2 )
//    // </editor-fold>
//
//    // <editor-fold defaultstate="collapsed" desc=" length ">
//
//    assert( testFrRange11.length(totalLen) == 5 )
//    assert( testFrRange21.length(totalLen) == 9 )
//    assert( testFrRange31.length(totalLen) == 8)
//    assert( testFrRange41.length(totalLen) == 12)
//    assert( testFrRange12.length(totalLen) == 3 )
//    assert( testFrRange22.length(totalLen) == 5)
//    assert( testFrRange32.length(totalLen) == 4)
//    assert( testFrRange42.length(totalLen) == 6)
//
//    // </editor-fold>
////    // <editor-fold defaultstate="collapsed" desc=" last ">
////    assert( testFrRange11.last(totalLen) == 4 )
////    assert( testFrRange21.last(totalLen) == 4 )
////    assert( testFrRange31.last(totalLen) == 7 )
////    assert( testFrRange41.last(totalLen) == 8)
////    assert( testFrRange12.last(totalLen) == 4)
////    assert( testFrRange22.last(totalLen) == 4)
////    assert( testFrRange32.last(totalLen) == 6)
////    assert( testFrRange42.last(totalLen) == 7)
////    // </editor-fold>
//    // <editor-fold defaultstate="collapsed" desc=" firstValid ">
//
//    assert( testFrRange11.firstValid(totalLen) == 0 )
//    assert( testFrRange21.firstValid(totalLen) == 0 )
//    assert( testFrRange31.firstValid(totalLen) == 0)
//    assert( testFrRange41.firstValid(totalLen) == 0)
//
//    assert( testFrRange12.firstValid(totalLen) == 0)
//    assert( testFrRange22.firstValid(totalLen) == 0)
//    assert( testFrRange32.firstValid(totalLen) == 0)
//    assert( testFrRange42.firstValid(totalLen) == 1)
//
//    assert( testFrRange52.firstValid(totalLen) == 3)
//    assert( testFrRange62.firstValid(totalLen) == Int.MaxValue)
//
//    assert( testFrRange11.firstValid(totalLen2) == 0 )
//    assert( testFrRange21.firstValid(totalLen2) == 0 )
//    assert( testFrRange31.firstValid(totalLen2) == 0 )
//    assert( testFrRange41.firstValid(totalLen2) == 0 )
//
//    assert( testFrRange12.firstValid(totalLen2) == 0)
//    assert( testFrRange22.firstValid(totalLen2) == 0)
//    assert( testFrRange32.firstValid(totalLen2) == 0)
//    assert( testFrRange42.firstValid(totalLen2) == 1)
//
//    assert( testFrRange52.firstValid(totalLen2) == 3)
//    assert( testFrRange62.firstValid(totalLen2) == 5)
//
//
//    // </editor-fold>
//    // <editor-fold defaultstate="collapsed" desc=" lastValid ">
//
//    assert( testFrRange11.lastValid(totalLen) == 4 )
//    assert( testFrRange21.lastValid(totalLen) == 4 )
//    assert( testFrRange31.lastValid(totalLen) == 4)
//    assert( testFrRange41.lastValid(totalLen) == 4)
//
//    assert( testFrRange12.lastValid(totalLen) == 4)
//    assert( testFrRange22.lastValid(totalLen) == 4)
//    assert( testFrRange32.lastValid(totalLen) == 4)
//    assert( testFrRange42.lastValid(totalLen) == 3)
//
//    assert( testFrRange52.lastValid(totalLen) == 3)
//    assert( testFrRange62.lastValid(totalLen) == Int.MinValue)
//
//    assert( testFrRange11.lastValid(totalLen2) == 4 )
//    assert( testFrRange21.lastValid(totalLen2) == 4 )
//    assert( testFrRange31.lastValid(totalLen2) == 6 )
//    assert( testFrRange41.lastValid(totalLen2) == 6 )
//
//    assert( testFrRange12.lastValid(totalLen2) == 4)
//    assert( testFrRange22.lastValid(totalLen2) == 4)
//    assert( testFrRange32.lastValid(totalLen2) == 6)
//    assert( testFrRange42.lastValid(totalLen2) == 5)
//
//    assert( testFrRange52.lastValid(totalLen2) == 5)
//    assert( testFrRange62.lastValid(totalLen2) == 5)
//
//    // </editor-fold>
//    // <editor-fold defaultstate="collapsed" desc=" preLength ">
//
//    assert( testFrRange11.preLength(totalLen) == 0 )
//    assert( testFrRange21.preLength(totalLen) == 4 )
//    assert( testFrRange31.preLength(totalLen) == 0 )
//    assert( testFrRange41.preLength(totalLen) == 3 )
//
//    assert( testFrRange12.preLength(totalLen) == 0 )
//    assert( testFrRange22.preLength(totalLen) == 2 )
//    assert( testFrRange32.preLength(totalLen) == 0 )
//    assert( testFrRange42.preLength(totalLen) == 2 )
//
//    assert( testFrRange52.preLength(totalLen) == 0 )
//    assert( testFrRange62.preLength(totalLen) == 0 )
//    // </editor-fold>
//    // <editor-fold defaultstate="collapsed" desc=" postLength ">
//    assert( testFrRange11.postLength(totalLen) == 0 )
//    assert( testFrRange21.postLength(totalLen) == 0 )
//    assert( testFrRange31.postLength(totalLen) == 3 )
//    assert( testFrRange41.postLength(totalLen) == 4 )
//
//    assert( testFrRange12.postLength(totalLen) == 0 )
//    assert( testFrRange22.postLength(totalLen) == 0 )
//    assert( testFrRange32.postLength(totalLen) == 1 )
//    assert( testFrRange42.postLength(totalLen) == 2 )
//
//    assert( testFrRange52.postLength(totalLen) == 2 )
//    assert( testFrRange62.postLength(totalLen) == 2 )
//
//    assert( testFrRange11.postLength(totalLen2) == 0 )
//    assert( testFrRange21.postLength(totalLen2) == 0 )
//    assert( testFrRange31.postLength(totalLen2) == 1 )
//    assert( testFrRange41.postLength(totalLen2) == 2 )
//
//    assert( testFrRange12.postLength(totalLen2) == 0 )
//    assert( testFrRange22.postLength(totalLen2) == 0 )
//    assert( testFrRange32.postLength(totalLen2) == 0 )
//    assert( testFrRange42.postLength(totalLen2) == 1 )
//
//    assert( testFrRange52.postLength(totalLen2) == 1 )
//    assert( testFrRange62.postLength(totalLen2) == 1 )
//    // </editor-fold>
////    // <editor-fold defaultstate="collapsed" desc=" getRangeFrValid ">
////
////    assert( testFrRange11.getRangeFrValid(totalLen) == new Range.Inclusive(0, 4, 1) )
////    //println( testFrRange21.getRangeFrValid(totalLen) )
////    assert( testFrRange21.getRangeFrValid(totalLen) == new Range.Inclusive(0, 4, 1) )
////    assert( testFrRange31.getRangeFrValid(totalLen) == new Range.Inclusive(0, 4, 1) )
////    assert( testFrRange41.getRangeFrValid(totalLen) == new Range.Inclusive(0, 4, 1) )
////
////    assert( testFrRange12.getRangeFrValid(totalLen) == new Range.Inclusive(0, 4, 2) )
////    assert( testFrRange22.getRangeFrValid(totalLen) == new Range.Inclusive(0, 4, 2) )
////    assert( testFrRange32.getRangeFrValid(totalLen) == new Range.Inclusive(0, 4, 2) )
////    assert( testFrRange42.getRangeFrValid(totalLen) == new Range.Inclusive(1, 3, 2) )
////
////    assert( testFrRange52.getRangeFrValid(totalLen) == new Range.Inclusive(3, 3, 2) )
////    assert( testFrRange62.getRangeFrValid(totalLen) == new Range.Inclusive(0, -1, 1) )
////    assert( (new Range.Inclusive(4, 4, 2)).length == 1 )
////    // </editor-fold>
//
//  }
//
//  test("length even"){
//
//    val totalLen = 6
//
//    val testFrRange11 = FrameRange(0, 3, 1)
//    val testFrRange21= FrameRange(-3, 3, 1)
//    val testFrRange31= FrameRange(0, 8, 1)
//    val testFrRange41= FrameRange(-2, 7, 1)
//
//    val testFrRange12 = FrameRange(0, 3, 2)
//    val testFrRange22= FrameRange(-3, 3, 2)
//    val testFrRange32= FrameRange(0, 8, 2)
//    val testFrRange42= FrameRange(-2, 7, 2)
//
//    val testFrRange52= FrameRange(3, 8, 2)
//    val testFrRange62= FrameRange(5, 8, 2)
//    val testFrRange72= FrameRange(4, 8, 2)
//
//    // <editor-fold defaultstate="collapsed" desc=" length ">
//
//    assert( testFrRange11.length(totalLen) == 4 )
//    assert( testFrRange21.length(totalLen) == 7 )
//    assert( testFrRange31.length(totalLen) == 9)
//    assert( testFrRange41.length(totalLen) == 10)
//    assert( testFrRange12.length(totalLen) == 2 )
//    assert( testFrRange22.length(totalLen) == 4)
//    assert( testFrRange32.length(totalLen) == 5)
//    assert( testFrRange42.length(totalLen) == 5)
//
//    // </editor-fold>
//    // <editor-fold defaultstate="collapsed" desc=" lastValid ">
//
//    assert( testFrRange11.lastValid(totalLen) == 3 )
//    assert( testFrRange21.lastValid(totalLen) == 3 )
//    assert( testFrRange31.lastValid(totalLen) == 5)
//    assert( testFrRange41.lastValid(totalLen) == 5)
//
//    assert( testFrRange12.lastValid(totalLen) == 2)
//    assert( testFrRange22.lastValid(totalLen) == 3)
//    assert( testFrRange32.lastValid(totalLen) == 4)
//    assert( testFrRange42.lastValid(totalLen) == 4)
//
//    assert( testFrRange52.lastValid(totalLen) == 5)
//    assert( testFrRange72.lastValid(totalLen) == 4)
//
//    // </editor-fold>
//    // <editor-fold defaultstate="collapsed" desc=" preLength ">
//
//    assert( testFrRange11.preLength(totalLen) == 0 )
//    assert( testFrRange21.preLength(totalLen) == 3 )
//    assert( testFrRange31.preLength(totalLen) == 0 )
//    assert( testFrRange41.preLength(totalLen) == 2 )
//
//    assert( testFrRange12.preLength(totalLen) == 0 )
//    assert( testFrRange22.preLength(totalLen) == 2 )
//    assert( testFrRange32.preLength(totalLen) == 0 )
//    assert( testFrRange42.preLength(totalLen) == 1 )
//
//    assert( testFrRange52.preLength(totalLen) == 0 )
//    assert( testFrRange62.preLength(totalLen) == 0 )
//    // </editor-fold>
//    // <editor-fold defaultstate="collapsed" desc=" postLength ">
//
//    assert( testFrRange11.postLength(totalLen) == 0 )
//    assert( testFrRange21.postLength(totalLen) == 0 )
//    assert( testFrRange31.postLength(totalLen) == 3 )
//    assert( testFrRange41.postLength(totalLen) == 2 )
//
//    assert( testFrRange12.postLength(totalLen) == 0 )
//    assert( testFrRange22.postLength(totalLen) == 0 )
//    assert( testFrRange32.postLength(totalLen) == 2 )
//    assert( testFrRange42.postLength(totalLen) == 1 )
//
//    assert( testFrRange52.postLength(totalLen) == 1 )
//    assert( testFrRange62.postLength(totalLen) == 1 )
//    assert( testFrRange72.postLength(totalLen) == 2 )
//    // </editor-fold>
////    // <editor-fold defaultstate="collapsed" desc=" getRangeFrValid ">
////
////    assert( testFrRange11.getRangeFrValid(totalLen) == new Range.Inclusive(0, 3, 1) )
////    assert( testFrRange21.getRangeFrValid(totalLen) == new Range.Inclusive(0, 3, 1) )
////    assert( testFrRange31.getRangeFrValid(totalLen) == new Range.Inclusive(0, 5, 1) )
////    assert( testFrRange41.getRangeFrValid(totalLen) == new Range.Inclusive(0, 5, 1) )
////
////    assert( testFrRange12.getRangeFrValid(totalLen) == new Range.Inclusive(0, 2, 2) )
////    assert( testFrRange22.getRangeFrValid(totalLen) == new Range.Inclusive(1, 3, 2) )
////    assert( testFrRange32.getRangeFrValid(totalLen) == new Range.Inclusive(0, 4, 2) )
////    assert( testFrRange42.getRangeFrValid(totalLen) == new Range.Inclusive(0, 4, 2) )
////
////    assert( testFrRange52.getRangeFrValid(totalLen) == new Range.Inclusive(3, 5, 2) )
////    assert( testFrRange62.getRangeFrValid(totalLen) == new Range.Inclusive(5, 5, 2) )
////    assert( testFrRange72.getRangeFrValid(totalLen) == new Range.Inclusive(4, 4, 2) )
////    assert( (new Range.Inclusive(4, 4, 2)).length == 1 )
////    // </editor-fold>
//
//  }
//
//}
