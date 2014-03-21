package nounou.data.io

import nounou._
import org.scalatest.FunSuite
import java.io.{File}
import breeze.numerics.abs

/**
 * @author ktakagaki
 * @date 1/30/14.
 */
class FileAdapterNCSTest extends FunSuite {

  val testFileTet4a = new File( getClass.getResource("/_testFiles/Neuralynx/t130911/Tet4a.ncs").getPath() )

  test("readTrace"){

    val d = new DataReader
    d.reload( testFileTet4a )

    println(d.dataORI.segmentLengths)
    //println(d.dataORI.readTrace(0, RangeFr.All, 0).length)
    //println(d.dataORI.readTrace(0, RangeFr.All, 1).length)

    assert(d.dataORI.segmentCount == 3, "Segment count " + d.data.segmentCount + " should be 3!" )
    assert(d.dataORI.readTrace(0, RangeFrAll(), 0).length == 3339264, "read whole segment length incorrect!")

    val tempTrace1 = d.dataORI.readTrace(0, 0 to 5120, 0)

//    println( tempTrace1.length )
//    println( (0 to 5120) )

    val xBits = d.dataORI.xBits
    assert(tempTrace1(0)/xBits == -888, "tempTrace1-0: read value incorrect!")
    assert(tempTrace1(1)/xBits == -603, "tempTrace1-1: read value incorrect!")
    assert(tempTrace1(2)/xBits == -107, "tempTrace1-2: read value incorrect!")
    assert(tempTrace1.length == 5121, "tempTrace1: trace read length incorrect!" )

    val tempTrace2 = d.dataORI.readTraceAbs(0, 0 to 10, 0)
    //println(tempTrace2(0) + "  " + d.data.absGain + " " + d.data.absOffset )
    assert( tempTrace2(0) + 27.1000428 < 1.0E-6 , "tempTrace2-0: abs read value incorrect!")
    assert( tempTrace2(1) + 18.4026555 < 1.0E-6 , "tempTrace2-1: abs read value incorrect!")
    assert( tempTrace2(2) + 3.2654795 < 1.0E-6 , "tempTrace2-2: abs read value incorrect!")

  }

}
