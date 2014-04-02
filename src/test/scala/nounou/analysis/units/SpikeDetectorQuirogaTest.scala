package nounou.analysis.units

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import nounou.DataReader
import nounou.data.filters.XDataFilterMedianSubtract
import nounou.data.XTrodesPreloaded
import breeze.linalg.max
import nounou.ranges.RangeFr

/**
 * @author ktakagaki
 * @date 3/20/14.
 */
@RunWith(classOf[JUnitRunner])
class SpikeDetectorQuirogaTest  extends FunSuite{

  val bigTestFilesHead = "V:/data/project.SPP/Nlx/SPP010/2013-12-02_17-07-31/"
  val bigTestFiles = Array[String](
    bigTestFilesHead + "Tet4a.ncs",
    bigTestFilesHead + "Tet4b.ncs",
    bigTestFilesHead + "Tet4c.ncs",
    bigTestFilesHead + "Tet4d.ncs"
  )

  test("Spike Dectector Quiroga Profiling") {

    val reader = new DataReader
    reader.load(bigTestFiles)
    //println(reader.toStringChain())

    val xDataMedian = new XDataFilterMedianSubtract( reader.dataORI )
    xDataMedian.setWindowLength(32*10+1)
    //println( max(xDataMedian.readTrace(0,RangeFr(0,500000))) )

    println(SpikeDetectorQuiroga(xDataMedian, new XTrodesPreloaded( Array(Array(0,1,2,3))), 0, RangeFr(0, 500000) ).length)

  }
}
