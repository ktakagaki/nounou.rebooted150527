package nounous.reader

import nounous.data.{XData, Span}
import org.scalatest.FunSuite

/**
 * Created with IntelliJ IDEA.
 * User: Kenta
 * Date: 11/7/13
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
class ReaderNEXTest extends FunSuite{

  val fileString = "V:/data/disp/2013-11-05_15-15-06/SE-CSC-Ch1.nex"

  test("readTrace"){
    //val obj = new ReaderNEX
    var readObj = ReaderNEX.read(fileString)(0)
    for(n<-1 to 500) readObj = ReaderNEX.read(fileString)(0)
      readObj match {
      case xd: XData => {
        println(xd.readPoint(0, 0, 0))
        println(xd.readTrace(0, 0, Span(0, 1000)).length)
      }
      case _ => println("Something's wrong!")
    }
    assert(true)
  }

}
