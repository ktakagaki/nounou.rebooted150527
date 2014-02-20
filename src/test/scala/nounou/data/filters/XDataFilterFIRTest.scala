package nounou.data.filters

import org.scalatest.FunSuite
import nounou.data.io.{XDataChannelNCS, FileAdapterNCS}
import nounou.data.{XDataChannel, XDataChannelArray}
import nounou.FrameRange

/**
 * @author ktakagaki
 * @date 2/14/14.
 */
class XDataFilterFIRTest extends FunSuite {

  val testFile = getClass.getResource("/_testFiles/Neuralynx/t130911/Tet4a.ncs").getPath()

  test("speed optimization"){
    val fileObject = new XDataChannelArray( Vector[XDataChannel]( FileAdapterNCS.load(testFile).head.asInstanceOf[XDataChannel] ) )
    val filterObject = new XDataFilterFIR( fileObject )
    filterObject.setFilterHz(5d, 10d)
//    for( count <- 0 until 2 ){
//      filterObject.setFilterHz(10d, 20d)
      filterObject.readTrace(0, 0 to 32000*100, 0)
//    }
  }
}
