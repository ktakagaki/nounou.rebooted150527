package nounou.elements.io

import breeze.linalg.DenseVector
import breeze.numerics.pow
import nounou._
import nounou.elements.NNElement
import org.scalatest.FunSuite
import java.io.File

/**
* @author ktakagaki
* @date 1/30/14.
*/
class FileAdapterNCSTest extends FunSuite {

  val testFileTet4a = getClass.getResource("/_testFiles/Neuralynx/t130911/Tet4a.ncs").getPath()
    //new File(  )
  //val testFileE04LC_CSC1 = new File( "C:\\prog\\_gh\\_kt\\nounou.testfiles\\Neuralynx\\E04LC\\CSC1.ncs" )
  val data = NN.load(testFileTet4a).apply(0)
  assert( data.isInstanceOf[NNDataChannelNCS] )
  val dataObj = data.asInstanceOf[NNDataChannelNCS]

  test("readInfo"){

    assert( dataObj.scale.absGain == 1.4901660156250002E-5 )
    assert( dataObj.scale.absOffset == 0d )
    assert( dataObj.scale.absUnit.contentEquals("microV") )

    //trait XFrames
    assert( dataObj.timing.segmentCount == 94 )
    assert( dataObj.timing.segmentLength(0) == 2546176 && dataObj.timing.segmentLength(8) == 3902976 )
    assert( dataObj.timing.segmentStartFrames(1) == 2546176)
//    intercept[IllegalArgumentException] {
//      dataObj.timing.length
//    }

    assert(dataObj.timing.sampleRate == 32000D)
    //assert(dataObj.timing.factorTSperFR == 1000000D/dataObj.timing.sampleRate)

    assert(dataObj.timing.segmentStartTss(0) == (10237373715L - 9223372036854775807L-1/*2^63*/))
    assert(dataObj.timing.convertTsToFrsg(10245373715L - 9223372036854775807L-1/*2^63*/) == (500*512, 0))
    assert(dataObj.timing.segmentStartTss(1) == (10664246433L - 9223372036854775807L-1/*2^63*/))
    assert(dataObj.timing.convertTsToFrsg(10664246433L - 9223372036854775807L-1/*2^63*/) == (0, 1))
    assert(dataObj.timing.segmentEndTs(0)   == (dataObj.timing.segmentStartTss(0) + (2546176L-1) *1000 /32 ) )

  }

  test("readPoint") {

    assert(dataObj.scale.xBits==1024)
    assert(dataObj.timing.segmentLength(0)==2546176)
    assert(dataObj.readPoint(0,0) == -1528*dataObj.scale.xBits)
    assert(dataObj.readPoint(512,0) == -1908*dataObj.scale.xBits)


  }

  test("readTrace") {

    assert( dataObj.readTrace( NN.SampleRange(0, 0, 1, 0) )(0) == -1528*dataObj.scale.xBits)
    assert( dataObj.readTraceDV( NN.SampleRange(0, 4, 1, 0) ) == DenseVector(-1528,-1841, -1282, -670, -500)*dataObj.scale.xBits)
    assert( dataObj.readTraceDV( NN.SampleRange(0, 4, 2, 0) ) == DenseVector(-1528, -1282, -500)*dataObj.scale.xBits)
    assert( dataObj.readTraceDV( NN.SampleRange(-2, 4, 2, 0) ) == DenseVector(0, -1528, -1282, -500)*dataObj.scale.xBits)

  }
//  test("readTrace"){
//
//    val d = new NNDataReader
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
