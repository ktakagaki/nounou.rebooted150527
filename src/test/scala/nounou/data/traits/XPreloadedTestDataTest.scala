package nounou.data.traits

import nounou.data.XDataPreloaded
import org.scalatest.FunSuite

/**
 * @author ktakagaki
 * @date 2/12/14.
 */
class XPreloadedTestDataTest extends FunSuite {

  val testSeg1 = Vector.tabulate[Int](10)( (i: Int) => i*12  )
  val testSeg2 = Vector.tabulate[Int](20)( (i: Int) => i*12  )
  val testChan = Vector[Vector[Int]]( testSeg1, testSeg2 )

  val testData = new XDataPreloaded(  Vector[Vector[Vector[Int]]](testChan, testChan),
    xBits = 12, absGain = 7d, absOffset = 0.1, absUnit = "mV",
    channelNames = Vector[String]("testChan", "testChan"),
    segmentStartTSs = Vector(10000000L, 30000000L),
    sampleRate = 1.0)

}
