package nounou.data

import scala.collection.immutable.TreeMap

/**
 * @author ktakagaki
 */
class XHeader(val header: TreeMap[String, HeaderValue]) extends X {

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

  // </editor-fold>

}


object XHeaderNull extends XHeader(TreeMap[String, HeaderValue]()){

  override def toString() = "XHeaderNull"

}

