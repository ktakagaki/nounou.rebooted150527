package nounou.data

/**
 * @author ktakagaki
 */
class XHeader(val formatName: String, val header: Vector[HeaderElements]) extends X {

  // <editor-fold desc="XConcatenatable">

  override def isCompatible(that: X): Boolean = {
    that match {
      case t: XHeader => this.formatName == t.formatName && this.header == t.header
      case _ => false
    }
  }

  // </editor-fold>

}

object XHeaderNull extends XHeader("Null Header", Vector[HeaderElements]())
