package nounou.data.ranges

import java.io.File

import breeze.linalg.DenseVector
import nounou.{OptSegment, NNDataReader}
import nounou.data.io.XDataChannelNCS
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

/**
 * @author ktakagaki
 * @date 2/16/14.
 */
@RunWith(classOf[JUnitRunner])
class rangesDataTest extends FunSuite {

  test("RangeFr All") {

    val testFileE04LC_CSC1 = new File("C:\\prog\\_gh\\_kt\\nounou.testfiles\\Neuralynx\\E04LC\\CSC1.ncs")
    val temp = NNDataReader.load(testFileE04LC_CSC1).apply(0)
    assert( temp.isInstanceOf[XDataChannelNCS] )
    val dataObj = temp.asInstanceOf[XDataChannelNCS]

    assert(dataObj.segmentLength(0)==2546176)
    assert(dataObj.readTrace( RangeFr(0, 9, 1, OptSegment(0))) ==
      DenseVector(-1528, -1841) )
//    println(dataObj.readTrace( RangeFr(0, 9, 2, OptSegment(0))))

  }


}