//package nounou.analysis.units
//
//import org.scalatest.FunSuite
//import org.junit.runner.RunWith
//import org.scalatest.junit.JUnitRunner
//import nounou.NNDataReader
//import nounou.data.filters.{XDataFilterFIR, XDataFilterBuffer, XDataFilterMedianSubtract}
//import nounou.data.XTrodesPreloaded
//import breeze.linalg.max
//import nounou.nounou.data.ranges.RangeFr
//
///**
// * @author ktakagaki
// * //@date 3/20/14.
// */
//
//@RunWith(classOf[JUnitRunner])
//class SpkDetPeakWidthTest  extends FunSuite{
//
//  val bigTestFilesHead = "V:/data/project.SPP/Nlx/SPP010/2013-12-02_17-07-31/"
//  val bigTestFiles = Array[String](
//    bigTestFilesHead + "Tet4a.ncs",
//    bigTestFilesHead + "Tet4b.ncs",
//    bigTestFilesHead + "Tet4c.ncs",
//    bigTestFilesHead + "Tet4d.ncs"
//  )
//
//  test("Spike Dectector Peak Width Profiling") {
//
//    val reader = new NNDataReader
//    reader.load(bigTestFiles)
////    val buffer = new XDataFilterBuffer(reader.dataORI)
//    //println(reader.toStringChain())
//
//    SpkDetPeakWidth.setTriggerData(reader.dataORI)
//    SpkDetPeakWidth.setTrodes(new XTrodesPreloaded( Array(Array(0,1,2,3))))
//    SpkDetPeakWidth.setPeakWidthMin(0.1)
//    SpkDetPeakWidth.setPeakWidthMax(0.9)
//    SpkDetPeakWidth.setMedianFilterWindowLength(2.53125)
//    println(SpkDetPeakWidth.detectSpikeTs(0, RangeFr(0, 5000000) ).length)
//    //println(SpkDetPeakWidth.detectSpikeTs(0, RangeFr(0, 5000) ).length)
//
//  }
//}
//
////@RunWith(classOf[JUnitRunner])
////class SpkDetQuirogaTest  extends FunSuite{
////
////  val bigTestFilesHead = "V:/data/project.SPP/Nlx/SPP010/2013-12-02_17-07-31/"
////  val bigTestFiles = Array[String](
////    bigTestFilesHead + "Tet4a.ncs",
////    bigTestFilesHead + "Tet4b.ncs",
////    bigTestFilesHead + "Tet4c.ncs",
////    bigTestFilesHead + "Tet4d.ncs"
////  )
////
////  test("Spike Dectector Quiroga Profiling") {
////
////    val reader = new NNDataReader
////    reader.load(bigTestFiles)
////    //println(reader.toStringChain())
////
////    val xDataMedian = new XDataFilterMedianSubtract( reader.dataORI )
////    xDataMedian.setWindowLength(32*10+1)
////    //println( max(xDataMedian.readTrace(0,RangeFr(0,500000))) )
////    val xDataBuffer = new XDataFilterBuffer( xDataMedian )
////
////    SpkDetQuiroga.setTriggerData(xDataMedian)
////    SpkDetQuiroga.setTrodes(new XTrodesPreloaded( Array(Array(0,1,2,3))))
////    println(SpkDetQuiroga.detectSpikeTs(0, RangeFr(0, 5000000) ).length)
////
////  }
////}
//
////@RunWith(classOf[JUnitRunner])
////class SpkDetPeakTest  extends FunSuite{
////
////  val bigTestFilesHead = "V:/data/project.SPP/Nlx/SPP010/2013-12-02_17-07-31/"
////  val bigTestFiles = Array[String](
////    bigTestFilesHead + "Tet4a.ncs",
////    bigTestFilesHead + "Tet4b.ncs",
////    bigTestFilesHead + "Tet4c.ncs",
////    bigTestFilesHead + "Tet4d.ncs"
////  )
////
////  test("Spike Dectector Peak Profiling") {
////
////    val reader = new NNDataReader
////    reader.load(bigTestFiles)
////    //println(reader.toStringChain())
////
////    val xDataFilt = new XDataFilterFIR( reader.dataORI )
////    xDataFilt.setFilterHz(300, 6000)
////    val xDataBuffer = new XDataFilterBuffer( xDataFilt )
////
////    SpkDetPeak.setTriggerData(xDataBuffer)
////    SpkDetPeak.setPeakWindow(16)
////    SpkDetPeak.setThresholdSD(4d)
////    SpkDetPeak.setTrodes(new XTrodesPreloaded( Array(Array(0,1,2,3))))
////
////    println(SpkDetPeak.detectSpikeTs(0, RangeFr(0, 5000000) ).length)
////
////  }
////}
