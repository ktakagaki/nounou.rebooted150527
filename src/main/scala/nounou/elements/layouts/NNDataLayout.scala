package nounou.elements.layouts

import java.util

import nounou.elements.NNElement
import nounou.elements.traits.NNChannelsElement

import scala.collection.mutable

/**
 * Created by ktakagaki on 15/03/15.
 */
abstract class NNDataLayout extends NNChannelsElement {

  /** Specifies if any channels are masked.
    * This uses java.util collections for successful serialization with gson.*/
  val masked: util.HashSet[Int] = new util.HashSet[Int]()
  final def isMasked(ch: Int) = {
    requireValidChannel(ch)
    masked.contains(ch)
  }
  final def mask(ch: Int*): Unit   = ch.foreach( mask(_) )
  final def mask(ch: Int): Unit    = masked.add(ch)
  final def unmask(ch: Int*): Unit = ch.foreach( unmask(_) )
  final def unmask(ch: Int): Unit  = masked.remove( ch )

}
