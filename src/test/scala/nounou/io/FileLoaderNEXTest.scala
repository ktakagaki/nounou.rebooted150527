//package nounou.io
//
//import nounou.elements.{NNData}
//import org.scalatest.FunSuite
//import java.io.File
//import nounou.NNDataReader
//
///**
// * Created with IntelliJ IDEA.
// * User: Kenta
// * Date: 11/7/13
// * Time: 3:30 PM
// * To change this template use File | Settings | File Templates.
// */
//class FileLoaderNEXTest extends FunSuite {
//
////  val testFileEvents = new File( getClass.getResource("/_testFiles/NEX/130507/SE-CSC-Ch1.nex").getPath() )
////  //val fileString = "V:/data/disp/2013-11-05_15-15-06/SE-CSC-Ch1.nex"
////  val d = new NNDataReader
////  d.reload( testFileEvents )
////
////  println(d.header)
////  println(d.header("nexFileVersion"))
////val temp = d.header("nexFileVersion")
////  test("NEX file: header version"){ assert( d.header("nexFileVersion").value == 104 ) }
////  test("NEX file: header comment"){ assert( d.header("nexFileComment").value == "                                                                                                                                                                                                                                                              11" ) }
////  test("NEX file: header freq"){ assert( d.header("nexFileFreq").value == 30000D ) }
////  test("NEX file: header tbeg"){ assert( d.header("nexFileTBeg").value == 0 ) }
////  test("NEX file: header tend"){ assert( d.header("nexFileTEnd").value == 3.333333333333333E-04 ) }
////  test("NEX file: header nvar"){ assert( d.header("nexFileNVar").value == 1 ) }
////
//  }
//
////  test("file 1: start time") {
////    assert(tF1.startTime == 0)
////  }
////
////  test("file 1: nvar") {
////    assert(tF1.nexFileNVar == 1)
////  }
////
////  test("file 1: cont1: absoluteGain") {
////    assert(tF1.absoluteGain == 1D / tF1.extraBitsD)
////  }
////
////  test("file 1: cont1: absoluteOffset") {
////    assert(tF1.absoluteOffset == 6.013470016999068E-154)
////  }
////
////  test("file 1: cont1: samplingRate") {
////    assert(tF1.samplingRate == 30000D)
////  }
////
////}
