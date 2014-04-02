package nounou.data.trodes

import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import nounou.data.XTrodesPreloaded

/**
 * @author ktakagaki
 * @date 03/31/2014.
 */
@RunWith(classOf[JUnitRunner])
class XTrodesTest extends FunSuite {

  test("trode declarations"){
    val testTrode = new XTrodesPreloaded(Array(Array(0,1,2,3,4)))
    assert(testTrode.channelCount ==5)
    assert(testTrode.trodeCount == 1)
//    println(testTrode)
  }
}
