package nounou.data.formats

import org.scalatest.FunSuite
import java.io.{File}
import nounou.DataReader
import nounou.data.Span
import breeze.numerics.abs

/**
 * @author ktakagaki
 * @date 1/30/14.
 */
class FileLoaderNCSTest extends FunSuite {

  val testFileTet4a = new File( getClass.getResource("/_testFiles/Neuralynx/t130911/Tet4a.ncs").getPath() )

  test("readTrace"){
    val d = new DataReader
    d.reload( testFileTet4a )
    assert(d.data.segmentCount == 3, "Segment count " + d.data.segmentCount + " should be 3!" )

    assert(d.data.readTrace(0, Span.All, 0).length == 3339264, "read whole segment length incorrect!")

    val tempTrace1 = d.data.readTrace(0, Span.Seq(0, 5120, 1), 0)
    //println(Span.Seq(0, 5120, 1).length(3339265))
    val xBits = d.data.xBits
    assert(tempTrace1(0)/xBits == -888, "tempTrace1-0: read value incorrect!")
    assert(tempTrace1(1)/xBits == -603, "tempTrace1-1: read value incorrect!")
    assert(tempTrace1(2)/xBits == -107, "tempTrace1-2: read value incorrect!")
    assert(tempTrace1.length == 5121, "tempTrace1: trace read length incorrect!" )

    val tempTrace2 = d.data.readTraceAbs(0, Span.Seq(0, 10, 1), 0)
    //println(tempTrace2(0) + "  " + d.data.absGain + " " + d.data.absOffset )
    assert( tempTrace2(0) + 27.1000428 < 1.0E-6 , "tempTrace2-0: abs read value incorrect!")
    assert( tempTrace2(1) + 18.4026555 < 1.0E-6 , "tempTrace2-1: abs read value incorrect!")
    assert( tempTrace2(2) + 3.2654795 < 1.0E-6 , "tempTrace2-2: abs read value incorrect!")

  }

}
