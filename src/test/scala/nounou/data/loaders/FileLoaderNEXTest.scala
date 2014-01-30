package nounou.data.loaders

import nounou.data.{Span, XData}
import org.scalatest.FunSuite

/**
 * Created with IntelliJ IDEA.
 * User: Kenta
 * Date: 11/7/13
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
class FileLoaderNEXTest extends FunSuite {

  val fileString = "V:/data/disp/2013-11-05_15-15-06/SE-CSC-Ch1.nex"

  test("readTrace"){
    //val obj = new FileLoaderNEX
    var readObj = FileLoaderNEX.load(fileString).apply(0)
    for(n<-1 to 500) readObj = FileLoaderNEX.load(fileString).apply(0)
      readObj match {
      case xd: XData => {
        println(xd.readPoint(0, 0, 0))
        println(xd.readTrace(0, Span.Seq(0, 1000), 0).length)
      }
      case _ => println("Something's wrong!")
    }
    assert(true)
  }

}
