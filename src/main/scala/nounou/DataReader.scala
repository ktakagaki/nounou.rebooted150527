package nounou

import java.io.File
import nounou.data.{XDataChannelArray, XDataChannel, XDataNull, XData}
import nounou.data.{XLayout, XLayoutNull}
import nounou.data.loaders.{FileLoaderNEV, FileLoaderNCS, FileLoaderNEX}
import nounou.data._
import nounou.data.discrete.{XMaskNull, XMask, XEventsNull, XEvents}


/**
 * @author ktakagaki
 */
class DataReader {

  /**Header object covering the whole block.*/
  var header: XHeader = XHeaderNull
  /**Main data output.*/
  var data: XData = XDataNull
  //insert downsample block, filter block, buffer block
  /**Auxiliary data, for instance, analog signals recorded with an optical trace.*/
  var dataAux: XData = XDataNull
  //insert downsample block, filter block, buffer block
  /**Layout of data*/
  var layout: XLayout = XLayoutNull
  /**Mask*/
  var mask: XMask = XMaskNull
  /**Events*/
  var events: XEvents = XEventsNull
  //  override var spk: XSpikes

  // <editor-fold defaultstate="collapsed" desc=" Java accessors ">

  def dataReadTrace(channel: Int): Array[Int] = data.readTrace(channel).toArray
  def dataReadTrace(channel: Int, span: Span): Array[Int] = data.readTrace(channel, span).toArray
  def dataReadTrace(channel: Int, span: Span, segment: Int): Array[Int] = data.readTrace(channel, span, segment).toArray

  def dataReadTraceAbs(channel: Int): Array[Double] = data.readTraceAbs(channel).toArray
  def dataReadTraceAbs(channel: Int, span: Span): Array[Double] = data.readTraceAbs(channel, span).toArray
  def dataReadTraceAbs(channel: Int, span: Span, segment: Int): Array[Double] = data.readTraceAbs(channel, span, segment).toArray

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" data loading ">

  //ToDo 1: java.lang.IllegalStateException: Not on FX application thread
  def load(): Unit   =  {
    val fileChooser = new scalafx.stage.FileChooser
    //val extFilter = new ExtensionFilter
    val window = new scalafx.stage.Popup
    fileChooser.showOpenDialog(window)

    val filesChosen: List[File] = fileChooser.showOpenMultipleDialog(null).toList
    load(filesChosen.toArray)
  }

  def load(files: Array[File]): Unit = files.map(load(_))

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
    load(files)
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

  // <editor-fold defaultstate="collapsed" desc=" loadImpl ">

    //ToDo 2: reorganize, refactor, rethink!
  private def loadImpl(x: X): Unit = {
    x match {
      case x0: XHeader => {
        if( header == XHeaderNull || reloadFlagHead == 1 ) header = x0
        else if( reloadFlagHead == 0 ) {
          if( header.isCompatible(x0) ) {
            //ToDo 3: implement concatenating/comparing header?
            println("Compatible header already loaded, ignoring new header " + x0 + ". Use clearHead first or reload() instead of load(), if this is unintended.")
          } else {
            println("Incompatible header already loaded, ignoring new header " + x0 + ". Use clearHead first or reload() instead of load(), if this is unintended.")
          }
        }
      }
      case x0: XData => {
        if( reloadFlagData == 1 ) {
          data = x0
          dataAux = XDataNull
          reloadFlagDataAux = 1
          reloadFlagData = 2
        } else if( data == XDataNull /*reloadFlagData == 0*/) {
          data = x0
        } else if ( data.isCompatible(x0)  /*reloadFlagData == 2*/) {
          data = data ::: x0
          //reloadFlagData = 2
        } else { //not compatible with data, try dataAux
            if( reloadFlagDataAux == 1 ) {
              dataAux = x0
              reloadFlagDataAux = 2
            } else if ( dataAux == XDataNull  /*reloadFlagDataAux == 0*/ ) {
              dataAux = x0
            } else if ( dataAux.isCompatible(x0) /*reloadFlagDataAux == 0 or 2*/ ) {
              dataAux = dataAux ::: x0
            } else {
              println("Incompatible data already loaded in data and dataAux, ignoring new data " + x0 + ". Use clearData/clearDataAux first or reload() instead of load(), if this is unintended.")
            }
        }
      }
      case x0: XDataChannel => {
        data match {
              case XDataNull => {
                data = new XDataChannelArray( Vector[XDataChannel]( x0 ) )
                if( reloadFlagData == 1 ) {
                  dataAux = XDataNull
                  reloadFlagDataAux = 1
                  reloadFlagData = 2
                }
              }
              case data0: XDataChannelArray => {
                if( reloadFlagData == 1 ) {
                  data = new XDataChannelArray( Vector[XDataChannel]( x0 ) )
                  dataAux = XDataNull
                  reloadFlagDataAux = 1
                  reloadFlagData = 2
                } else if ( data0(0).isCompatible(x0)  /*reloadFlagData == 0, 2*/) {
                  data = data0 ::: x0
                  //reloadFlagData = 2
                } else { //not compatible with data, try dataAux
                  if( reloadFlagDataAux == 1 ) {
                    dataAux = new XDataChannelArray( Vector[XDataChannel]( x0 ) )
                    reloadFlagDataAux = 2
                  } else {
                      dataAux match {
                        case dataAux0: XDataChannelArray => {
                          if ( dataAux0.isCompatible(x0) /*reloadFlagDataAux == 0 or 2*/ ) {
                            dataAux = dataAux0 ::: x0
                          } else {
                            println("Incompatible data already loaded in data and dataAux, ignoring new data " + x0 + ". Use clearData/clearDataAux first or reload() instead of load(), if this is unintended.")
                          }
                        }
                        case _ => {
                          println("Incompatible data already loaded in data and dataAux, ignoring new data " + x0 + ". Use clearData/clearDataAux first or reload() instead of load(), if this is unintended.")
                        }
                      }
                  }
                  }
              }
              case _ => {
                if( reloadFlagDataAux == 1 ) {
                  dataAux = new XDataChannelArray( Vector[XDataChannel]( x0 ) )
                  reloadFlagDataAux = 2
                } else {
                  dataAux match {
                    case dataAux0: XDataChannelArray => {
                      if ( dataAux0.isCompatible(x0) /*reloadFlagDataAux == 0 or 2*/ ) {
                        dataAux = dataAux0 ::: x0
                      } else {
                        println("Incompatible data already loaded in data and dataAux, ignoring new data " + x0 + ". Use clearData/clearDataAux first or reload() instead of load(), if this is unintended.")
                      }
                    }
                    case _ => {
                      println("Incompatible data already loaded in data and dataAux, ignoring new data " + x0 + ". Use clearData/clearDataAux first or reload() instead of load(), if this is unintended.")
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
                else println("Incompatible event objects already loaded, ignoring new XEvent " + x0 + ". Use clearEvents first or reload() instead of load(), if this is unintended.")
              }
              case 1 => { //reloading, not cleared previous data yet
                events = x0
                reloadFlagEvents = 2
              }
              case 2 => { //reloading, has already cleared
                if( events.isCompatible(x0) ) events = events ::: x0
                else println("Trying to load incompatible event objects, ignoring further new XEvent " + x0 + ".")
              }
            }
          }
      }
      case x0: X => sys.error("Loading of this type of " + x0 + " has not been implemented!")
    }
  }


    // </editor-fold>

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" clearing ">

  def clearHead: Unit = {header = XHeaderNull}
  def clearData: Unit = {data = XDataNull}
  def clearDataAux: Unit = {dataAux = XDataNull}
  def clearLayout: Unit = {layout = XLayoutNull}
  def clearMask: Unit = {mask = XMaskNull}
  def clearEvents: Unit = {events = XEventsNull}

  def clearAll: Unit = {
    clearHead
    clearData
    clearDataAux
    clearLayout
    clearMask
    clearEvents
  //    spk  = null
  }

  // </editor-fold>

}
