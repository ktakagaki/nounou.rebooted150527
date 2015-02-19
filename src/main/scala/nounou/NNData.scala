//package nounou
//
//import java.io.File
//
//import nounou.data._
//import nounou.data.filters._
//import nounou.data.headers.XHeader
//
///** A static class which encapsulates nounou data objects,
//  * designed for easy access from Mathematica/MatLab/Java
//  *
//  * @author ktakagaki
//  * @date 08/14/2014.
//  */
//class NNData {
//
//  // <editor-fold defaultstate="collapsed" desc=" variable definitions: header/raw/lfp/highpass/auxRaw/aux/spikes/mask/events ">
//
//  private var _header: XHeader = null
//  def header: XHeader = if(_header == null ){
//    throw loggerError("Data containing spike information has not been loaded or created yet! ")
//  } else {
//    _header
//  }
//
//  val rawData: XDataFilterHolder = new XDataFilterHolder()
//
////  val dataLfp: XDataFilterLFP = new XDataFilterLFP( dataRaw )
//  def dataHp: XData = ???
//
//  def auxRaw: XData = ???
//  def auxFilt: XData = ???
//
//  private var _spikes: XSpikes = null
//  def spikes: XSpikes = if(_spikes == null ){
//      throw loggerError("Data containing spike information has not been loaded or created yet! ")
//    } else {
//      _spikes
//    }
//
//  private var _mask: XSampleMask = null
//  def mask: XSampleMask = if(_mask == null ){
//    throw loggerError("Data containing mask information has not been loaded or created yet! ")
//  } else {
//    _mask
//  }
//
//  private var _events: XEvents = null
//  def events: XEvents = if(_events == null ){
//    throw loggerError("Data containing event information has not been loaded or created yet! ")
//  } else {
//    _events
//  }
//
//  // </editor-fold>
//
//  // <editor-fold defaultstate="collapsed" desc=" reloadTo ">
//
//  def reloadTo(xs: Array[X]): Unit = {
//
//    val xHeader  = xs.filter( _.isInstanceOf[XHeader] ).map( _.asInstanceOf[XHeader] )
//    if(xHeader.length > 0) {
//      _header = null
//      loadToHeader(xHeader)
//    }
//    val xData   = xs.filter( _.isInstanceOf[XData] ).filter( !_.isInstanceOf[XDataAux]).map( _.asInstanceOf[XData] )
//    if(xData.length != 0) {
//      rawData.setHeldData(XDataNull)
//      loadToData(xData)
//    }
//    val xDataChannel   = xs.filter( _.isInstanceOf[XDataChannel] ).map( _.asInstanceOf[XDataChannel] )
//    val xAux    = xs.filter( _.isInstanceOf[XDataAux]).map( _.asInstanceOf[XDataAux] )
//    val xSpikes = xs.filter( _.isInstanceOf[XSpikes] ).map( _.asInstanceOf[XSpikes] )
//    val xMask   = xs.filter( _.isInstanceOf[XSampleMask]   ).map( _.asInstanceOf[XSampleMask] )
//    val xEvents = xs.filter( _.isInstanceOf[XEvents] ).map( _.asInstanceOf[XEvents] )
//
//  }
//
//  def reloadTo(file: File): Unit = reloadTo( Array(file) )
//  def reloadTo(files: Array[File]): Unit = {
//    files.flatMap( NNDataReader.load(_) )
//  }
//  def reloadTo(string: String): Unit =  reloadTo( new File(string) )
//  def reloadTo(strings: Array[String]): Unit =  reloadTo( strings.map(new File(_)) )
//
//  // </editor-fold>
//
//  // <editor-fold defaultstate="collapsed" desc=" loadToXXX methods ">
//
//  def loadToHeader(xHeaders: Array[XHeader]): Unit = {
//    if( _header == null ){
//      if( xHeaders.length > 1 ) _header = xHeaders.reduceLeft( (a, b) => a.:::(b) )
//      else if( xHeaders.length == 1 ) _header = xHeaders(0)
//    } else if(xHeaders.length > 0) {
//      _header = xHeaders.foldLeft( _header )( (a, b) => a.:::(b) )
//    }
//  }
//
//  def loadToData(xData: Array[XData]): Unit = {
//    loggerRequire( xData != null && xData.length > 0, "loadToData must be called with non-null, non-empty array.")
//    rawData.setHeldData( xData.foldLeft( rawData.getHeldData )( (a, b) => a.:::(b) ) )
//  }
//
//  def loadToData(xDataChannel: Array[XDataChannel]): Unit = {
//    loggerRequire( xDataChannel != null && xDataChannel.length > 0, "loadToData must be called with non-null, non-empty array.")
//    rawData.setHeldData( xDataChannel.foldLeft( rawData.getHeldData )( (a, b) => a.:::(b) ) )
//  }
//
//  // </editor-fold>
//
//}
