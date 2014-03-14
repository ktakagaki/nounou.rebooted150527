package nounou.data

import scala.collection.immutable.TreeMap
import nounou.data.traits.XConcatenatable


  /** Encapsulates a database of [[XSpike]] objects for display and processing
    *
    * User: takagaki
    * Date: 23.09.13
    * Time: 13:14
    * To change this template use File | Settings | File Templates.
    */
  class XSpikes(val spikes: TreeMap[Long, XSpike], val waveFormLength: Int ) extends X with XConcatenatable {

    // <editor-fold desc="XConcatenatable">

    override def :::(that: X): XSpikes = {
      that match {
        case x: XSpikes => {
          if( this.isCompatible(x) ) new XSpikes( this.spikes ++ x.spikes, waveFormLength )
          else throw new IllegalArgumentException("cannot concatenate spikes with different waveform lengths")
        }
        case _ => {
          require(false, "cannot concatenate different types!")
          this
        }
      }
    }

    override def isCompatible(that: X): Boolean =
      that match {
        case x: XSpikes => if(this.waveFormLength == x.waveFormLength) true else false
        case _ => false
      }

    // </editor-fold>

    override def toString() = "XSpikes( " + spikes.size + " spikes total, waveFormLength=" + waveFormLength + " )"

  }

  object XSpikesNull extends XSpikes( TreeMap[Long, XSpike](), 0 ) {

    override def toString() = "XSpikesNull"

  }
