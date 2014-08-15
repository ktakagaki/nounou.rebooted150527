package nounou

import java.io.File

import nounou.data._
import nounou.data.headers.XHeader

/** A static class which encapsulates nounou data objects,
  * for easy access from Mathematica/MatLab
  *
  * @author ktakagaki
  * @date 08/14/2014.
  */
object NNData {

  // <editor-fold defaultstate="collapsed" desc=" variable definitions: header/raw/lfp/highpass/auxRaw/aux/spikes/mask/events ">

  private var _header: XHeader = null
  def header: XHeader = if(_header == null ){
    throw loggerError("Data containing spike information has not been loaded or created yet! ")
  } else {
    _header
  }

  def raw: XData = ???
  def lfp: XData = ???
  def highpass: XData = ???

  def auxRaw: XData = ???
  def aux: XData = ???

  private var _spikes: XSpikes = null
  def spikes: XSpikes = if(_spikes == null ){
      throw loggerError("Data containing spike information has not been loaded or created yet! ")
    } else {
      _spikes
    }

  private var _mask: XMask = null
  def mask: XMask = if(_mask == null ){
    throw loggerError("Data containing mask information has not been loaded or created yet! ")
  } else {
    _mask
  }

  private var _events: XEvents = null
  def events: XEvents = if(_events == null ){
    throw loggerError("Data containing event information has not been loaded or created yet! ")
  } else {
    _events
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" reloadTo ">

  def reloadTo(xs: Array[X]): Unit = {

    val xHeader  = xs.filter( _.isInstanceOf[XHeader] ).map( _.asInstanceOf[XHeader] )
    if(xHeader.length > 0) {
      _header = null
      loadToHeader(xHeader)
    }

    val xDatas  = xs.filter( _.isInstanceOf[XData] ).map( _.asInstanceOf[XData] )
      if(xDatas.length != 0) raw
    val xAux    = xs.filter( _.isInstanceOf[XDataAux])
    val xSpikes = xs.filter( _.isInstanceOf[XSpikes] )
    val xMask   = xs.filter( _.isInstanceOf[XMask] )
    val xEvents = xs.filter( _.isInstanceOf[XEvents] )

  }

  def reloadTo(file: File): Unit = reloadTo( Array(file) )
  def reloadTo(files: Array[File]): Unit = {
    files.flatMap( DataReader.load(_) )
  }
  def reloadTo(string: String): Unit =  reloadTo( new File(string) )
  def reloadTo(strings: Array[String]): Unit =  reloadTo( strings.map(new File(_)) )

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" loadToXXX methods ">

  def loadToHeader(xHeaders: Array[XHeader]): Unit = {
    if( _header == null ){
      if( xHeaders.length > 1 ) _header = xHeaders.tail.foldLeft(xHeaders(0))( (a, b) => a.:::(b) )
      else if( xHeaders.length == 1 ) _header = xHeaders(0)
    } else if(xHeaders.length > 0) {
      _header = xHeaders.foldLeft( _header )( (a, b) => a.:::(b) )
    }
  }


  // </editor-fold>

}
