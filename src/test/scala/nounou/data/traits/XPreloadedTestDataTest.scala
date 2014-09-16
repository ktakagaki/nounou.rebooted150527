package nounou.data.traits

import nounou.data.XDataPreloaded
import org.scalatest.FunSuite
import breeze.linalg.{DenseVector => DV, DenseMatrix => DM}

/**
 * @author ktakagaki
 * @date 2/12/14.
 */
class XPreloadedTestDataTest extends FunSuite {

//  val testSeg1 = DV.tabulate[Int](10)( (i: Int) => i*12  )
//  val testSeg2 = DV.tabulate[Int](20)( (i: Int) => i*12  )
  val testChan = DV.tabulate[Int](30)( (i: Int) => i*12  )//DV.horzcat( testSeg1, testSeg2 )

  val testData = new XDataPreloaded(  DV.horzcat(testChan, testChan),
    xBits = 12, absGain = 7d, absOffset = 0.1, absUnit = "mV",
    scaleMax = 500, scaleMin = 0,
    //channelNames = Vector[String]("testChan", "testChan"),
    segmentStartTs = Array(10000000L, 30000000L),
  segmentLength = Array(0, 10),
    sampleRate = 1.0)

}
