//package nounou.io
//
//import nounou.elements.NNEvents
//import org.scalatest.FunSuite
//import java.io.File
//import nounou.NNDataReader
//import breeze.numerics.pow
//
///**
// * @author ktakagaki
// * //@date 1/31/14.
// */
//class FileLoaderNEVTest extends FunSuite {
//
//  val testFileEvents = new File( getClass.getResource("/_testFiles/Neuralynx/t130911/Events.nev").getPath() )
//
//  test("toPortValue"){
//    assert( FileAdapterNEV.toPortValue("TTL Input on AcqSystem1_2 board 200 port 4 value") == 102200004 )
//    assert( FileAdapterNEV.toPortValue("TTL Input on AcqSystem1_0 board 0 port 1 value (0x0001).") == 100000001 )
//    assert( FileAdapterNEV.fromPortValue(102200004) == (1,2,200,4) )
//  }
//
//  test("read events"){
//
//    val loaded = NNDataReader.load( testFileEvents ) // FileAdapterNEV.load( testFileEvents )
//    assert(loaded(0).isInstanceOf[NNEvents])
//    val events = loaded(0).asInstanceOf[NNEvents]
//    //println(events.portCount)
//    assert(events.portCount == 2)
//    assert( events.ports.toList == List(0, 100000001))
//    assert( events.lengths.toList == List(5, 2702) )
//    val eventArray = events.filterByPort(100000001).toArray
//    assert( eventArray(1-1).code == 1)
//    assert( eventArray(1-1).timestamp == -9223372015441894693L)
//    assert( eventArray(1-1).duration == 203562)
//    assert( eventArray(2702-1).code == 7)
//    assert( eventArray(2702-1).timestamp == -9223372012967273693L)
//    assert( eventArray(2702-1).duration == 203844)
//
//  }
//
//}
