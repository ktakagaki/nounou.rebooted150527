package nounou.util

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

/**
 * Created by ktakagaki on 15/03/24.
 */
@RunWith(classOf[JUnitRunner])
class UtilTest extends FunSuite {

  test("getFileExtension") {

    assert( nounou.util.getFileExtension("hello.exe") == "exe" )
    assert( nounou.util.getFileExtension("hello.123.eexe") == "eexe" )

  }

}
