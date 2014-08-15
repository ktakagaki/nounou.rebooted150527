package nounou.data.io

import nounou._
import org.scalatest.FunSuite
import java.io.{File}
import nounou.data.ranges.{RangeFr, RangeFrAll}
import nounou.data.XDataChannelArray
import nounou.data.filters.XDataFilterHolder

/**
 * @author ktakagaki
 * @date 1/30/14.
 */
class FileAdapterNCSTest extends FunSuite {

  val testFileTet4a = new File( getClass.getResource("/_testFiles/Neuralynx/t130911/Tet4a.ncs").getPath() )

//  test("readTrace"){
//
//    val d = new DataReader
//    d.reload( testFileTet4a )
//
//    println(d.dataORI.segmentLength)
//    //println(d.dataORI.readTrace(0, RangeFr.All, 0).length)
//    //println(d.dataORI.readTrace(0, RangeFr.All, 1).length)
//
//    assert(d.dataORI.segmentCount == 3, "Segment count " + d.data.segmentCount + " should be 3!" )
////    assert(d.dataORI.readTrace(0, RangeFrAll(1, segment=0)).length == 3339264, "read whole segment length incorrect!")
//
//
//    /////Tests of XDataChannelNCS
//    assert(d.dataORI.asInstanceOf[XDataFilterHolder].heldData.isInstanceOf[XDataChannelArray])
//    assert(d.dataORI.asInstanceOf[XDataFilterHolder].heldData.asInstanceOf[XDataChannelArray].array(0).isInstanceOf[XDataChannelNCS])
//    val channelNCS = d.dataORI.asInstanceOf[XDataFilterHolder].heldData.asInstanceOf[XDataChannelArray].array(0).asInstanceOf[XDataChannelNCS]
//    assert(channelNCS.frameSegmentToRecordIndex(0,0) === (0,0))
//    assert(channelNCS.frameSegmentToRecordIndex(511,0) === (0,511))
//    assert(channelNCS.frameSegmentToRecordIndex(512,0) === (1,0))
//    assert(channelNCS.frameSegmentToRecordIndex(513,0) === (1,1))
//    assert(channelNCS.frameSegmentToRecordIndex(3339264,0) === (6522,0))
//
//
//    /////Trace reading tests
//    val xBits = d.dataORI.xBits
//    val tempTrace1 = d.dataORI.readTrace(0, RangeFr(0, 5120, step=1, OptSegmentNone))
//    assert(tempTrace1(0)/xBits == -888, "tempTrace1-0: read value incorrect!")
//    assert(tempTrace1(1)/xBits == -603, "tempTrace1-1: read value incorrect!")
//    assert(tempTrace1(2)/xBits == -107, "tempTrace1-2: read value incorrect!")
//    assert(tempTrace1.length == 5121, "tempTrace1: trace read length incorrect!" )
//    /////Absolute trace reading tests
//    val tempTrace2 = d.dataORI.readTraceAbs(0, RangeFr(0, 10, step=1, OptSegmentNone))
//    //println(tempTrace2(0) + "  " + d.data.absGain + " " + d.data.absOffset )
//    assert( tempTrace2(0) + 27.1000428 < 1.0E-6 , "tempTrace2-0: abs read value incorrect!")
//    assert( tempTrace2(1) + 18.4026555 < 1.0E-6 , "tempTrace2-1: abs read value incorrect!")
//    assert( tempTrace2(2) + 3.2654795 < 1.0E-6 , "tempTrace2-2: abs read value incorrect!")
//    /////Point reading tests
//    assert(d.dataORI.readPoint(0,1,0)/xBits == -603)
//    assert(d.dataORI.readPoint(0,512,0)/xBits == 238)
//    assert(d.dataORI.readPoint(0,513,0)/xBits == -240)
//    assert(d.dataORI.readPoint(0,3339264,0)/xBits == 0)
//
//
//  }

}
