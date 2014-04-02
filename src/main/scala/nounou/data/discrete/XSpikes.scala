package nounou.data

import scala.collection.immutable.TreeMap
import nounou.data.traits.XConcatenatable
import scala.beans.BeanProperty

/** A database of [[XSpike]] objects for display and processing
    *
    */
  class XSpikes extends X with XConcatenatable {

    /**Trode layout for spike detection/sorting*/
    @BeanProperty
    var trodeLayout: XTrodes = XTrodesNull

    val spikes: Array[TreeMap[Long, XSpike]] = Array[TreeMap[Long, XSpike]]()

//    def checkDataCompatibility(xData: XData): Unit = {
//
//      trodeLayout match {
//        case XTrodesNull => {
//          logger.info("Will automatically initiate XTrodes layout with XTrodesIndividual object, assuming each channel is individual with no trode interdependencies.")
//          trodeLayout = new XTrodesIndividual(xData.channelCount)
//        }
//        case xTr: XTrodes => {
//          if( xTr.channelCount < xData.channelCount ){
//            logger.warn("Previously loaded XTrode layout has less channels than the data given. Hopefully, this is intended...")
//          }
//          loggerRequire( xTr.channelCount == xData.channelCount, "Previously loaded XTrode layout has more channels than data given!")
//        }
//      }
//
//      spikes.length match {
//        case 0 => spikes = Array.tabulate(trodeLayout.channelCount)( p => TreeMap[Long, XSpike]())
//        case x: Int => {
//          loggerRequire(x == trodeLayout.channelCount,
//            "Previously loaded spike data has different channel count ({} )from the XTrode layout ({}). Must be corrected!",
//            x.toString, trodeLayout.channelCount.toString)
//        }
//      }
//    }


    // <editor-fold desc="XConcatenatable">

    override def :::(that: X): XSpikes = {
      that match {
        case x: XSpikes => {
          if( this.isCompatible(x) ) {
            val temp = new XSpikes( waveFormLength )
            temp.spikes = this.spikes ++ x.spikes
            temp
          }
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

    override def toString() = "XSpikes( " + spikes.size + " spikes total)"//, waveFormLength=" + waveFormLength + " )"

  }

  object XSpikesNull extends XSpikes {

    override def toString() = "XSpikesNull"

  }
