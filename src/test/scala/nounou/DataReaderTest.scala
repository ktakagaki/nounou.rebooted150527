package nounou

import org.scalatest.FunSuite
import java.io.File

/**
 * @author ktakagaki
 * @date 2/14/14.
 */
class DataReaderTest extends FunSuite {

  val testFileTet4s = Array[String](
    getClass.getResource("/_testFiles/Neuralynx/t130911/Tet4a.ncs").getPath(),
    getClass.getResource("/_testFiles/Neuralynx/t130911/Tet4b.ncs").getPath(),
    getClass.getResource("/_testFiles/Neuralynx/t130911/Tet4c.ncs").getPath(),
    getClass.getResource("/_testFiles/Neuralynx/t130911/Tet4d.ncs").getPath()
  )

  test("loading"){

    val reader = new DataReader

    for(c <- 0 to 99){
      reader.load( testFileTet4s )
    }

  }

}
