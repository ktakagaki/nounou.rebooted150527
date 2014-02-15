package nounou.data

/**
 * @author ktakagaki
 */
class XHeader(val header: Map[String, HeaderValue]) extends X {

  override def toString = header.toString

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


object XHeaderNull extends XHeader(Map[String, HeaderValue]()){

  override def toString() = "XHeaderNull"

}

