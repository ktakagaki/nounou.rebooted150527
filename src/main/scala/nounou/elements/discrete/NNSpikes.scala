package nounou.elements

import scala.collection.immutable.TreeMap
import nounou.elements.traits.NNConcatenatable
import nounou.elements.data.{NNData, NNDataNull}
import nounou.util.LoggingExt
import breeze.linalg.DenseVector

object NNSpikes extends LoggingExt {
  def initialize(xData: NNData, trodeLayout: NNTrodes, waveformLength: Int): NNSpikes = {
    val newTL = trodeLayout match {
      case NNTrodesNull => {
        logger.info("Will automatically initiate XTrodes layout with XTrodesIndividual object, assuming each channel is individual with no trode interdependencies.")
        new NNTrodesIndividual(xData.channelCount)
      }
      case xTr: NNTrodes => {
        loggerRequire( xTr.channelCount == xData.channelCount, "XTrode layout has more or less channels {} than given XData {}!",
          xTr.channelCount.toString, xData.channelCount.toString)
        xTr
      }
    }

    new NNSpikes( waveformLength, newTL )
  }
}

/** A database of [[XSpikeWaveform]] objects for display and processing. Also encapsulates
  * a trode layout compatible with the spike data, and a copy of the XData upon which the
  * waveforms were based.
    *
    */
class NNSpikes(val waveformLength: Int, private var xtr: NNTrodes, private var xdat: NNData ) extends NNElement with NNConcatenatable {

  var channelCount = scala.math.max(xtr.channelCount, xdat.channelCount)

  def this(waveformLength: Int) = this(waveformLength, NNTrodesNull, NNDataNull)
  def this(waveformLength: Int, xtr: NNTrodes) = this(waveformLength, xtr, NNDataNull)
  def this(waveformLength: Int, xdat: NNData) = this(waveformLength, NNTrodesNull, xdat)
  def this(waveformLength: Int, xdat: NNData, xtr: NNTrodes) = this(waveformLength, xtr, xdat)

  private var spikes: Array[TreeMap[Long, NNSpike]] = new Array[TreeMap[Long, NNSpike]](0)
  xTrodes_=(xtr)
  xData_=(xdat)


  def isValidTrode(trode: Int) = trode >=0 && trode < spikes.length

//  def checkDataCompatibility(xData: XData): Unit = {
//
//    xTrodes match {
//      case XTrodesNull => {
//        logger.info("Will automatically initiate XTrodes layout with XTrodesIndividual object, assuming each channel is individual with no trode interdependencies.")
//        xTrodes = new XTrodesIndividual(xData.channelCount)
//      }
//      case xTr: XTrodes => {
//        if( xTr.channelCount < xData.channelCount ){
//          logger.warn("Previously loaded XTrode layout has less channels than the data given. Hopefully, this is intended...")
//        }
//        loggerRequire( xTr.channelCount == xData.channelCount, "Previously loaded XTrode layout has more channels than data given!")
//      }
//    }
//
//    spikes.length match {
//      case 0 => spikes = Array.tabulate(xTrodes.channelCount)( p => TreeMap[Long, XSpikeWaveform]())
//      case x: Int => {
//        loggerRequire(x == xTrodes.channelCount,
//          "Previously loaded spike data has different channel count ({} )from the XTrode layout ({}). Must be corrected!",
//          x.toString, xTrodes.channelCount.toString)
//      }
//    }
//  }

  // <editor-fold defaultstate="collapsed" desc=" set/getXTrodes ">

  def xTrodes_=( xTrodes: NNTrodes ): Unit = {
    if( xtr == NNTrodesNull) {
      if( xdat != NNDataNull ) {
        loggerRequire( xTrodes.channelCount == channelCount,
          "You tried to load an XTrode object with channel count {}, when an XData object with channel count {} is already loaded!",
          xTrodes.channelCount.toString, xdat.channelCount.toString
        )
      } else {
        channelCount = xTrodes.channelCount
      }
      xtr = xTrodes
    }
  }
  def xTrodes(): NNTrodes = xtr
  def setXTrodes( xTrodes: NNTrodes ): Unit = { xTrodes_=( xTrodes ) }
  def getXTrodes(): NNTrodes = xTrodes()

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" set/getXData ">

  def xData_=( xData: NNData ): Unit = {
    if( xdat == NNDataNull) {
      if( xtr != NNTrodesNull ) {
        loggerRequire( xData.channelCount == channelCount,
          "You tried to load an XData object with channel count {}, when a XTrode object with channel count {} is already loaded!",
          xData.channelCount.toString, xtr.channelCount.toString
        )
      } else {
        channelCount = xData.channelCount
      }
      xdat = xData
    }
  }
  def xData(): NNData = xdat
  def setXData( xData: NNData ): Unit = { xData_=( xData ) }
  def getXData(): NNData = xData()

  // </editor-fold>


  // <editor-fold desc="addSpike/addSpikes">

//  def addSpike(trode: Int, ts: Long, xSpikeWf: XSpikeWaveform) = {
//    loggerRequire(xSpikeWf.length == waveformLength, "tried to add a spike {} which does not have waveformLength {}!", xSpikeWf.toString, waveformLength.toString)
//    loggerRequire(isValidTrode(trode), "trode={} is invalid. spikes.length={}", trode.toString, spikes.length.toString)
//    spikes(trode) = spikes(trode) + (ts -> xSpikeWf)
//  }
//
//  def addSpikes(trode: Int, ts: Array[Long], xData: XData, wfPre: Int, wfPost: Int) = {
//    loggerRequire(isValidTrode(trode), "trode={} is invalid. spikes.length={}", trode.toString, spikes.length.toString)
//    loggerRequire(wfPre+wfPost+1 == waveformLength, "wfPre={} and wfPost={} are incompatible with waveformLength={}",
//      wfPre.toString, wfPost.toString, waveformLength.toString)
//    spikes(trode) = spikes(trode) ++ ts.map( t => {
//        t -> new XSpikeWaveform(
//          Array.tabulate(xTrodes.trodeSize(trode))(
//            trCh => xData.readTraceA(xTrodes.trodeChannels(trode)(trCh), RangeTsEvent(t, wfPre, wfPost, 1))
//          )
//        )
//      }
//    ).filter(p => p._2.length == waveformLength)
//  }

  // </editor-fold>

  // <editor-fold desc="readSpikes/readSpikeTimes">

//  def readSpikes(trode: Int): Array[Array[Array[Int]]] = {
//    loggerRequire(isValidTrode(trode), "trode={} is invalid. spikes.length={}", trode.toString, spikes.length.toString)
////    val tempret = new ArrayBuffer[Array[Array[Int]]]()
////    tempret.sizeHint( spikeCount(trode ))
//    spikes(trode).map( p => p._2.waveform ).toArray
//  }

  def readSpikeTimes(trode: Int): Array[Long] = {
    loggerRequire(isValidTrode(trode), "trode={} is invalid. spikes.length={}", trode.toString, spikes.length.toString)
    spikes(trode).map( p => p._1 ).toArray
  }

  // </editor-fold>

  // <editor-fold desc="spikeCount/spikeCounts">

  def spikeCount(trode: Int) = {
    loggerRequire(isValidTrode(trode), "trode={} is invalid. spikes.length={}", trode.toString, spikes.length.toString)
    spikes(trode).size
  }
  def spikeCounts() = spikes.map(p=> p.size)

  // </editor-fold>

  // <editor-fold desc="XConcatenatable">

  override def :::(that: NNElement): NNSpikes = {
    that match {
      case x: NNSpikes => {
        if( this.isCompatible(x) ) {
          val temp = new NNSpikes( waveformLength, x.xTrodes, x.xData )
          temp.spikes ++: this.spikes
          temp.spikes ++: x.spikes
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

  override def isCompatible(that: NNElement): Boolean =
    that match {
      case x: NNSpikes => {
        this.waveformLength == x.waveformLength && this.xTrodes.isCompatible(x.xTrodes) && this.xData.channelCount == x.xData.channelCount
      }
      case _ => false
    }

  // </editor-fold>

  override def toString() = "XSpikes( " + (DenseVector( spikeCounts() )).toString +
    " spikes, waveFormLength=" + waveformLength.toString + ", xTrodes=" + xTrodes.toString()+")"

}

object NNSpikesNull$$ extends NNSpikes(0) {

  override def toString() = "XSpikesNull"

}