//package nounou.elements.filters
//
////import nounou.OptSegment
//import org.scalatest.FunSuite
//import nounou.io.{NNDataChannelNCS, FileAdapterNCS}
//import nounou.elements.{NNDataChannel, NNDataChannelArray}
//import nounou.elements.ranges.FrRange$
//
//import scala.nounou.obj.data.filters.NNDataFilterFIR
//
///**
// * @author ktakagaki
// * //@date 2/14/14.
// */
//class XDataFilterFIRTest extends FunSuite {
//
//  val testFile = getClass.getResource("/_testFiles/Neuralynx/t130911/Tet4a.ncs").getPath()
//
//  test("speed optimization"){
//    val fileObject = new NNDataChannelArray( Vector[NNDataChannel]( FileAdapterNCS.load(testFile).head.asInstanceOf[NNDataChannel] ) )
//    val filterObject = new NNDataFilterFIR( fileObject )
//    filterObject.setFilterHz(5d, 10d)
////    for( count <- 0 until 2 ){
////      filterObject.setFilterHz(10d, 20d)
////      filterObject.readTrace(0, RangeFr(0, 32000*100, OptSegment(0)))
////    }
//  }
//}
