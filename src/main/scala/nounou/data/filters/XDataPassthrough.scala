package nounou.data.filters

import nounou.data.{X, Span, XData}
import scala.collection.immutable.Vector

/** A passthrough object, which is inherited with various XDataFilter traits to create a filter block.
  */
class XDataPassthrough( var upstream: XData ) extends XData {

  override def isCompatible(target: X) = false

  override def :::(target: X): XData = {
    throw new IllegalArgumentException("cannot append an XDataRepeater!")
  }
}
