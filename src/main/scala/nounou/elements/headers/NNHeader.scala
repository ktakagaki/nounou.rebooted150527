package nounou.elements.headers

import nounou.elements.NNElement
import nounou.elements.traits.NNConcatenatable
import scala.collection.immutable.TreeMap

//ToDo 3: find example of header needed, and complete this class

/**
 * @author ktakagaki
 */
class NNHeader(val header: TreeMap[String, HeaderValue]) extends NNElement with NNConcatenatable {

  override def toString = "XHeader: " + header.size + " entries"
  def toStringFull = "XHeader: " + header.toString

  def apply(key: String) = header(key)

  // <editor-fold desc="XConcatenatable">

  override def isCompatible(that: NNElement): Boolean = {
    that match {
      case t: NNHeader => this.header == t.header
      case _ => false
    }
  }
  override def :::(target: NNElement): NNHeader = ???

  // </editor-fold>
}


object NNHeaderNull$$ extends NNHeader(TreeMap[String, HeaderValue]()){

  override def toString() = "XHeaderNull"

}

