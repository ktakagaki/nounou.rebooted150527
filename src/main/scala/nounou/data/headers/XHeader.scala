package nounou.data.headers

import nounou.data.X
import nounou.data.traits.XConcatenatable
import scala.collection.immutable.TreeMap

//ToDo 3: find example of header needed, and complete this class

/**
 * @author ktakagaki
 */
class XHeader(val header: TreeMap[String, HeaderValue]) extends X with XConcatenatable {

  override def toString = "XHeader: " + header.size + " entries"
  def toStringFull = "XHeader: " + header.toString

  def apply(key: String) = header(key)

  // <editor-fold desc="XConcatenatable">

  override def isCompatible(that: X): Boolean = {
    that match {
      case t: XHeader => this.header == t.header
      case _ => false
    }
  }
  override def :::(target: X): XHeader = ???

  // </editor-fold>
}


object XHeaderNull extends XHeader(TreeMap[String, HeaderValue]()){

  override def toString() = "XHeaderNull"

}

