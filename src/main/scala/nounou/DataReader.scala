package nounou

import java.io.File

import com.typesafe.scalalogging.slf4j.Logging
import nounou.data._
import nounou.data.formats.{FileLoaderNEV, FileLoaderNCS, FileLoaderNEX}
import nounou.data.discrete._
import nounou.data.filters.{XDataFilterHolder, XDataFilterDecimate, XDataFilterFIR}


/**
 * @author ktakagaki
 */
class DataReader extends Logging {

  /**Header object covering the whole block.*/
  var header: XHeader = XHeaderNull
  /**Main data output.*/
  def data(): XData = dataFIR
  val dataORI: XDataFilterHolder = new XDataFilterHolder()
  //insert downsample block, filter block, buffer block
  /**Auxiliary data, for instance, analog signals recorded with an optical trace.*/
  def dataAux: XData = dataAuxORI //temporarily set to mirror
  var dataAuxORI: XData = XDataNull
  //insert downsample block, filter block, buffer block
  /**Layout of data*/
  var layout: XLayout = XLayoutNull
  /**Mask*/
  var mask: XMask = XMaskNull
  /**Events*/
  var events: XEvents = XEventsNull
  /**Spikes*/
  var spikes: XSpikes = XSpikesNull

  // <editor-fold defaultstate="collapsed" desc=" filters, setting data/dataORI and dataAux/dataAuxORI ">
  var dataDecimate: XDataFilterDecimate = new XDataFilterDecimate( dataORI )
  var dataFIR: XDataFilterFIR = new XDataFilterFIR( dataDecimate )
  def setData(x: XData): Unit = {
    dataORI.realData = x
    if(dataORI.sampleRate > 2000) dataDecimate.factor = math.min(16, (dataORI.sampleRate / 2000).toInt)
  }


  def setFilterHz(f0: Double, f1: Double) = dataFIR.setFilterHz(f0, f1)
  def setFilterOff() = dataFIR.setFilterOff()

  // </editor-fold>


  // <editor-fold defaultstate="collapsed" desc=" Java accessors ">

  def readTrace(channel: Int): Array[Int] = data.readTrace(channel).toArray
  def readTrace(channel: Int, range: FrameRange): Array[Int] = data.readTrace(channel, range).toArray
  def readTrace(channel: Int, range: FrameRange, segment: Int): Array[Int] = data.readTrace(channel, range, segment).toArray

  def readTraceAbs(channel: Int): Array[Double] = data.readTraceAbs(channel).toArray
  def readTraceAbs(channel: Int, range: FrameRange): Array[Double] = data.readTraceAbs(channel, range).toArray
  def readTraceAbs(channel: Int, range: FrameRange, segment: Int): Array[Double] = data.readTraceAbs(channel, range, segment).toArray

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" load/reload ">

  //ToDo 1: java.lang.IllegalStateException: Not on FX application thread
  def load(): Unit   =  {
    val fileChooser = new scalafx.stage.FileChooser
    //val extFilter = new ExtensionFilter
    val window = new scalafx.stage.Popup
    fileChooser.showOpenDialog(window)

    val filesChosen: List[File] = fileChooser.showOpenMultipleDialog(null).toList
    filesChosen.map(load(_))
  }

  def load(files: Array[String]): Unit = files.map( (fStr: String) => load( new File(fStr) ) )

  def load(file: File, reload: Boolean = false): Unit = {
    val list = file.getName.toLowerCase match {
      case n: String if n.endsWith(".nex") => FileLoaderNEX.load( file )
      case n: String if n.endsWith(".ncs") => FileLoaderNCS.load( file )
      case n: String if n.endsWith(".nev") => FileLoaderNEV.load( file )
      case n => throw new IllegalArgumentException("File format for " + n + " is not supported yet.")
    }
    list.map( loadImpl(_) )
  }

  def load(x: X): Unit = loadImpl(x)

  def load(string: String): Unit = load( new File(string) )

  /** 0=load(not reload) 1=marked for reload 2=reloading/cleared
    */
  private var reloadFlagHead, reloadFlagData, reloadFlagDataAux, reloadFlagLayout, reloadFlagMask, reloadFlagEvents = 0
  private def setReloadFlags(value: Int): Unit = {
    reloadFlagHead = value
    reloadFlagData = value; reloadFlagDataAux = value
    reloadFlagLayout = value; reloadFlagMask = value; reloadFlagEvents = value
  }
  def reload(files: Array[File]): Unit = {
    setReloadFlags(1)
    files.map(load(_))
    setReloadFlags(0)
  }
  def reload(file: File): Unit = {
    setReloadFlags(1)
    load(file)
    setReloadFlags(0)
  }

  def reload(x: X): Unit = {
    setReloadFlags(1)
    loadImpl(x)
    setReloadFlags(0)
  }

  def reload(): Unit = {
    setReloadFlags(1)
    load()
    setReloadFlags(0)
  }

  def reload(string: String) = {
    setReloadFlags(1)
    load( new File(string) )
    setReloadFlags(0)
  }

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" loadImpl ">

    //ToDo 2: reorganize, refactor, rethink!
  private def loadImpl(x: X): Unit = {
    x match {
      case x0: XHeader => {
        if( header == XHeaderNull || reloadFlagHead == 1 ) header = x0
        else if( reloadFlagHead == 0 ) {
          if( header.isCompatible(x0) ) {
            //ToDo 3: implement concatenating/comparing header?
            logger.warn("Compatible header already loaded, ignoring new header {}. Use clearHead first or reload() instead of load(), if this is unintended.", x0)
          } else {
            logger.warn("Incompatible header already loaded, ignoring new header {}. Use clearHead first or reload() instead of load(), if this is unintended.", x0)
          }
        }
      }
      case x0: XData => {
        if( reloadFlagData == 1 ) {
          setData(x0)
          dataAuxORI = XDataNull
          reloadFlagDataAux = 1
          reloadFlagData = 2
        } else if( dataORI.realData == XDataNull /*reloadFlagData == 0*/) {
          setData(x0)
        } else if ( dataORI.isCompatible(x0)  /*reloadFlagData == 2*/) {
          setData(dataORI.realData ::: x0)
          //reloadFlagData = 2
        } else { //not compatible with data, try dataAux
            if( reloadFlagDataAux == 1 ) {
              dataAuxORI = x0
              reloadFlagDataAux = 2
            } else if ( dataAuxORI == XDataNull  /*reloadFlagDataAux == 0*/ ) {
              dataAuxORI = x0
            } else if ( dataAuxORI.isCompatible(x0) /*reloadFlagDataAux == 0 or 2*/ ) {
              dataAuxORI = dataAuxORI ::: x0
            } else {
              logger.warn("Incompatible data already loaded in data and dataAux, ignoring new data {}. Use clearData/clearDataAux first or reload() instead of load(), if this is unintended.", x0)
            }
        }
      }
      case x0: XDataChannel => {
        dataORI.realData match {
              case XDataNull => {
                setData( new XDataChannelArray( Vector[XDataChannel]( x0 ) ) )
                if( reloadFlagData == 1 ) {
                  dataAuxORI = XDataNull
                  reloadFlagDataAux = 1
                  reloadFlagData = 2
                }
              }
              case data0: XDataChannelArray => {
                if( reloadFlagData == 1 ) {
                  setData( new XDataChannelArray( Vector[XDataChannel]( x0 ) ) )
                  dataAuxORI = XDataNull
                  reloadFlagDataAux = 1
                  reloadFlagData = 2
                } else if ( data0(0).isCompatible(x0)  /*reloadFlagData == 0, 2*/) {
                  setData( data0 ::: x0 )
                  //reloadFlagData = 2
                } else { //not compatible with data, try dataAux
                  if( reloadFlagDataAux == 1 ) {
                    dataAuxORI = new XDataChannelArray( Vector[XDataChannel]( x0 ) )
                    reloadFlagDataAux = 2
                  } else {
                      dataAuxORI match {
                        case dataAux0: XDataChannelArray => {
                          if ( dataAux0.isCompatible(x0) /*reloadFlagDataAux == 0 or 2*/ ) {
                            dataAuxORI = dataAux0 ::: x0
                          } else {
                            logger.warn("Incompatible data already loaded in data and dataAux, ignoring new data {}. Use clearData/clearDataAux first or reload() instead of load(), if this is unintended.", x0)
                          }
                        }
                        case _ => {
                          logger.warn("Incompatible data already loaded in data and dataAux, ignoring new data {}}. Use clearData/clearDataAux first or reload() instead of load(), if this is unintended.", x0)
                        }
                      }
                  }
                  }
              }
              case _ => {
                if( reloadFlagDataAux == 1 ) {
                  dataAuxORI = new XDataChannelArray( Vector[XDataChannel]( x0 ) )
                  reloadFlagDataAux = 2
                } else {
                  dataAuxORI match {
                    case dataAux0: XDataChannelArray => {
                      if ( dataAux0.isCompatible(x0) /*reloadFlagDataAux == 0 or 2*/ ) {
                        dataAuxORI = dataAux0 ::: x0
                      } else {
                        logger.warn("Incompatible data already loaded in data and dataAux, ignoring new data {}. Use clearData/clearDataAux first or reload() instead of load(), if this is unintended.", x0)
                      }
                    }
                    case _ => {
                      logger.warn("Incompatible data already loaded in data and dataAux, ignoring new data {}. Use clearData/clearDataAux first or reload() instead of load(), if this is unintended.", x0)
                    }
                  }
                }
              }
        }
        }
      case x0: XMask => sys.error("ToDo: have not implemented mask loading yet!")
      case x0: XEvents => {
          if( events == XEventsNull ) {
            events = x0
            if(reloadFlagEvents == 1) reloadFlagEvents = 2
          } else {
            reloadFlagEvents match {
              case 0 => { //not reloading, but concatenating
                if( events.isCompatible(x0) ) events = events ::: x0
                else logger.warn("Incompatible event objects already loaded, ignoring new XEvent {}. Use clearEvents first or reload() instead of load(), if this is unintended.", x0)
              }
              case 1 => { //reloading, not cleared previous data yet
                events = x0
                reloadFlagEvents = 2
              }
              case 2 => { //reloading, has already cleared
                if( events.isCompatible(x0) ) events = events ::: x0
                else logger.warn("Trying to load incompatible event objects, ignoring further new XEvent {}.", x0)
              }
            }
          }
      }
      case x0: XSpikes => sys.error("ToDo: have not implemented spike loading yet!")
      case x0: X => logger.warn("Loading of this type of {} has not been implemented!", x0)
    }
  }


    // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" clearing ">

  def clearHead: Unit = {header = XHeaderNull}
  def clearData: Unit = {setData(XDataNull)}
  def clearDataAux: Unit = {dataAuxORI = XDataNull/*; dataAux = XDataNull*/ }
  def clearLayout: Unit = {layout = XLayoutNull}
  def clearMask: Unit = {mask = XMaskNull}
  def clearEvents: Unit = {events = XEventsNull}
  def clearSpikes: Unit = {spikes = XSpikesNull}

  def clearAll: Unit = {
    clearHead
    clearData
    clearDataAux
    clearLayout
    clearMask
    clearEvents
    clearSpikes
  }

  // </editor-fold>

  override def toString() = "DataReader( data channels:" + dataORI.channelCount + ", dataAux channels:" + dataAuxORI.channelCount +
                                         ", data layout: " + layout + ", data mask: " + mask + ", events: " + events.length + ")"

  def dataSummary(): String = {
    "DataReader loaded data summary:\n" +
    "     " + "header : " + header + "\n" +
    "     " + "data   : " + dataORI + "\n" +
    "     " + "dataAux: " + dataAuxORI + "\n" +
    "     " + "layout : " + layout + "\n" +
    "     " + "mask   : " + mask + "\n" +
    "     " + "events : " + events + "\n" +
    "     " + "spikes : " + spikes
  }
}
