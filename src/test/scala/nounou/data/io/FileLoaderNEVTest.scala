package nounou.data.io

import org.scalatest.FunSuite
import java.io.File
import nounou.DataReader
import breeze.numerics.pow

/**
 * @author ktakagaki
 * @date 1/31/14.
 */
class FileLoaderNEVTest extends FunSuite {

  val testFileEvents = new File( getClass.getResource("/_testFiles/Neuralynx/t130911/Events.nev").getPath() )

  test("read events"){
    val d = new DataReader
    d.reload( testFileEvents )

    val tempSlice = d.events.events.slice(0, 3).toArray
    assert( tempSlice(0)._2.eventCode == 1, "Event code 0 is incorrect!" )
    assert( tempSlice(1)._2.eventCode == 8, "Event code 1 is incorrect!" )
    assert( tempSlice(2)._2.eventCode == 6, "Event code 2 is incorrect!" )

    assert( tempSlice(0)._1 == (21412881115L - 9223372036854775807L -1 ), "Event timestamp 0 is incorrect!" )
    assert( tempSlice(1)._1 == (21413605959L - 9223372036854775807l -1 ), "Event timestamp 1 is incorrect!" )
    assert( tempSlice(2)._1 == (21414493771L - 9223372036854775807l -1 ), "Event timestamp 2 is incorrect!" )

    assert( d.events.length == 91, "Event counts are incorrect!" )
    assert( d.events.getEventsByCode(8).size == 10, "Event extraction is not working!" )
  }

}
