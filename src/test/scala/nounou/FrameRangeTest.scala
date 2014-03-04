package nounou

import org.scalatest.FunSuite

/**
 * @author ktakagaki
 * @date 2/16/14.
 */
class FrameRangeTest extends FunSuite {

  test("length odd"){

    val totalLen = 5
    val totalLen2 = 7

    val testFrRange11 = new FrameRange(0, 4, 1)
    val testFrRange21= new FrameRange(-4, 4, 1)
    val testFrRange31= new FrameRange(0, 7, 1)
    val testFrRange41= new FrameRange(-3, 8, 1)

    val testFrRange12 = new FrameRange(0, 4, 2)
    val testFrRange22= new FrameRange(-4, 4, 2)
    val testFrRange32= new FrameRange(0, 7, 2)
    val testFrRange42= new FrameRange(-3, 8, 2)

    val testFrRange52= new FrameRange(3, 8, 2)
    val testFrRange62= new FrameRange(5, 8, 2)

    // <editor-fold defaultstate="collapsed" desc=" length ">

    assert( testFrRange11.length(totalLen) == 5 )
    assert( testFrRange21.length(totalLen) == 9 )
    assert( testFrRange31.length(totalLen) == 8)
    assert( testFrRange41.length(totalLen) == 12)
    assert( testFrRange12.length(totalLen) == 3 )
    assert( testFrRange22.length(totalLen) == 5)
    assert( testFrRange32.length(totalLen) == 4)
    assert( testFrRange42.length(totalLen) == 6)
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" last ">

    assert( testFrRange11.last(totalLen) == 4 )
    assert( testFrRange21.last(totalLen) == 4 )
    assert( testFrRange31.last(totalLen) == 7)
    assert( testFrRange41.last(totalLen) == 8)
    assert( testFrRange12.last(totalLen) == 4)
    assert( testFrRange22.last(totalLen) == 4)
    assert( testFrRange32.last(totalLen) == 6)
    assert( testFrRange42.last(totalLen) == 7)
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" last valid ">

    assert( testFrRange11.lastValid(totalLen) == 4 )
    assert( testFrRange21.lastValid(totalLen) == 4 )
    assert( testFrRange31.lastValid(totalLen) == 4)
    assert( testFrRange41.lastValid(totalLen) == 4)

    assert( testFrRange12.lastValid(totalLen) == 4)
    assert( testFrRange22.lastValid(totalLen) == 4)
    assert( testFrRange32.lastValid(totalLen) == 4)
    assert( testFrRange42.lastValid(totalLen) == 3)

    assert( testFrRange52.lastValid(totalLen) == 3)
    assert( testFrRange62.lastValid(totalLen) == -1)

    assert( testFrRange11.lastValid(totalLen2) == 4 )
    assert( testFrRange21.lastValid(totalLen2) == 4 )
    assert( testFrRange31.lastValid(totalLen2) == 6 )
    assert( testFrRange41.lastValid(totalLen2) == 6 )

    assert( testFrRange12.lastValid(totalLen2) == 4)
    assert( testFrRange22.lastValid(totalLen2) == 4)
    assert( testFrRange32.lastValid(totalLen2) == 6)
    assert( testFrRange42.lastValid(totalLen2) == 5)

    assert( testFrRange52.lastValid(totalLen2) == 5)
    assert( testFrRange62.lastValid(totalLen2) == 5)


    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" preLength ">

    assert( testFrRange11.preLength(totalLen) == 0 )
    assert( testFrRange21.preLength(totalLen) == 4 )
    assert( testFrRange31.preLength(totalLen) == 0 )
    assert( testFrRange41.preLength(totalLen) == 3 )

    assert( testFrRange12.preLength(totalLen) == 0 )
    assert( testFrRange22.preLength(totalLen) == 2 )
    assert( testFrRange32.preLength(totalLen) == 0 )
    assert( testFrRange42.preLength(totalLen) == 2 )

    assert( testFrRange52.preLength(totalLen) == 0 )
    assert( testFrRange62.preLength(totalLen) == 0 )
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" postLength ">
    assert( testFrRange11.postLength(totalLen) == 0 )
    assert( testFrRange21.postLength(totalLen) == 0 )
    assert( testFrRange31.postLength(totalLen) == 3 )
    assert( testFrRange41.postLength(totalLen) == 4 )

    assert( testFrRange12.postLength(totalLen) == 0 )
    assert( testFrRange22.postLength(totalLen) == 0 )
    assert( testFrRange32.postLength(totalLen) == 1 )
    assert( testFrRange42.postLength(totalLen) == 2 )

    assert( testFrRange52.postLength(totalLen) == 2 )
    assert( testFrRange62.postLength(totalLen) == 2 )

    assert( testFrRange11.postLength(totalLen2) == 0 )
    assert( testFrRange21.postLength(totalLen2) == 0 )
    assert( testFrRange31.postLength(totalLen2) == 1 )
    assert( testFrRange41.postLength(totalLen2) == 2 )

    assert( testFrRange12.postLength(totalLen2) == 0 )
    assert( testFrRange22.postLength(totalLen2) == 0 )
    assert( testFrRange32.postLength(totalLen2) == 0 )
    assert( testFrRange42.postLength(totalLen2) == 1 )

    assert( testFrRange52.postLength(totalLen2) == 1 )
    assert( testFrRange62.postLength(totalLen2) == 1 )
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" getValidRange ">

    assert( testFrRange11.getValidRange(totalLen) == new Range.Inclusive(0, 4, 1) )
    println( testFrRange21.getValidRange(totalLen) )
    assert( testFrRange21.getValidRange(totalLen) == new Range.Inclusive(0, 4, 1) )
    assert( testFrRange31.getValidRange(totalLen) == new Range.Inclusive(0, 4, 1) )
    assert( testFrRange41.getValidRange(totalLen) == new Range.Inclusive(0, 4, 1) )

    assert( testFrRange12.getValidRange(totalLen) == new Range.Inclusive(0, 4, 2) )
    assert( testFrRange22.getValidRange(totalLen) == new Range.Inclusive(0, 4, 2) )
    assert( testFrRange32.getValidRange(totalLen) == new Range.Inclusive(0, 4, 2) )
    assert( testFrRange42.getValidRange(totalLen) == new Range.Inclusive(1, 3, 2) )

    assert( testFrRange52.getValidRange(totalLen) == new Range.Inclusive(3, 3, 2) )
    assert( testFrRange62.getValidRange(totalLen) == new Range.Inclusive(0, -1, 1) )
    assert( (new Range.Inclusive(4, 4, 2)).length == 1 )
    // </editor-fold>

  }

  test("length even"){

    val totalLen = 6

    val testFrRange11 = new FrameRange(0, 5, 1)
    val testFrRange21= new FrameRange(-3, 5, 1)
    val testFrRange31= new FrameRange(0, 8, 1)
    val testFrRange41= new FrameRange(-2, 7, 1)

    val testFrRange12 = new FrameRange(0, 5, 2)
    val testFrRange22= new FrameRange(-3, 5, 2)
    val testFrRange32= new FrameRange(0, 8, 2)
    val testFrRange42= new FrameRange(-2, 7, 2)

    val testFrRange52= new FrameRange(3, 8, 2)
    val testFrRange62= new FrameRange(5, 8, 2)
    val testFrRange72= new FrameRange(4, 8, 2)

    // <editor-fold defaultstate="collapsed" desc=" length ">

    assert( testFrRange11.length(totalLen) == 6 )
    assert( testFrRange21.length(totalLen) == 9 )
    assert( testFrRange31.length(totalLen) == 9)
    assert( testFrRange41.length(totalLen) == 10)
    assert( testFrRange12.length(totalLen) == 3 )
    assert( testFrRange22.length(totalLen) == 5)
    assert( testFrRange32.length(totalLen) == 5)
    assert( testFrRange42.length(totalLen) == 5)
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" totalLen ">

    assert( testFrRange11.last(totalLen) == 5 )
    assert( testFrRange21.last(totalLen) == 5 )
    assert( testFrRange31.last(totalLen) == 8)
    assert( testFrRange41.last(totalLen) == 7)
    assert( testFrRange12.last(totalLen) == 4)
    assert( testFrRange22.last(totalLen) == 5)
    assert( testFrRange32.last(totalLen) == 8)
    assert( testFrRange42.last(totalLen) == 6)
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" preLength ">

    assert( testFrRange11.preLength(totalLen) == 0 )
    assert( testFrRange21.preLength(totalLen) == 3 )
    assert( testFrRange31.preLength(totalLen) == 0 )
    assert( testFrRange41.preLength(totalLen) == 2 )

    assert( testFrRange12.preLength(totalLen) == 0 )
    assert( testFrRange22.preLength(totalLen) == 2 )
    assert( testFrRange32.preLength(totalLen) == 0 )
    assert( testFrRange42.preLength(totalLen) == 1 )

    assert( testFrRange52.preLength(totalLen) == 0 )
    assert( testFrRange62.preLength(totalLen) == 0 )
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" postLength ">

    assert( testFrRange11.postLength(totalLen) == 0 )
    assert( testFrRange21.postLength(totalLen) == 0 )
    assert( testFrRange31.postLength(totalLen) == 3 )
    assert( testFrRange41.postLength(totalLen) == 2 )

    assert( testFrRange12.postLength(totalLen) == 0 )
    assert( testFrRange22.postLength(totalLen) == 0 )
    assert( testFrRange32.postLength(totalLen) == 2 )
    assert( testFrRange42.postLength(totalLen) == 1 )

    assert( testFrRange52.postLength(totalLen) == 1 )
    assert( testFrRange62.postLength(totalLen) == 1 )
    assert( testFrRange72.postLength(totalLen) == 2 )
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" getValidRange ">

    assert( testFrRange11.getValidRange(totalLen) == new Range.Inclusive(0, 5, 1) )
    assert( testFrRange21.getValidRange(totalLen) == new Range.Inclusive(0, 5, 1) )
    assert( testFrRange31.getValidRange(totalLen) == new Range.Inclusive(0, 5, 1) )
    assert( testFrRange41.getValidRange(totalLen) == new Range.Inclusive(0, 5, 1) )

    assert( testFrRange12.getValidRange(totalLen) == new Range.Inclusive(0, 4, 2) )
    assert( testFrRange22.getValidRange(totalLen) == new Range.Inclusive(1, 5, 2) )
    assert( testFrRange32.getValidRange(totalLen) == new Range.Inclusive(0, 4, 2) )
    assert( testFrRange42.getValidRange(totalLen) == new Range.Inclusive(0, 4, 2) )

    assert( testFrRange52.getValidRange(totalLen) == new Range.Inclusive(3, 5, 2) )
    assert( testFrRange62.getValidRange(totalLen) == new Range.Inclusive(5, 5, 2) )
    assert( testFrRange72.getValidRange(totalLen) == new Range.Inclusive(4, 4, 2) )
    assert( (new Range.Inclusive(4, 4, 2)).length == 1 )
    // </editor-fold>

  }

}
