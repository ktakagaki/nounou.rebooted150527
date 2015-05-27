package nounou.elements.headers

import nounou.elements.NNElement
import scala.collection.immutable.TreeMap

//ToDo 3: find example of header needed, and complete this class

/**Parent class for all header information
  *
 * @author ktakagaki
 */
class NNHeader(val header: TreeMap[String, HeaderValue]) extends NNElement {

  override def toString =
    this.getClass.getName + s"($gitHeadShort)"
  override def toStringFull = toString

  def apply(key: String) = header(key)

  override def isCompatible(that: NNElement): Boolean = that match {
    case x: NNHeader => true
    case _ => false
  }

}
