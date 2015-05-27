//package nounou
//
//import java.io.File
//
//import com.typesafe.scalalogging.slf4j.LazyLogging
//import nounou.elements._
//import nounou.elements.data.{NNDataChannelArray, NNDataChannel}
//import nounou.io.{FileAdapterNEV, FileAdapterNCS}
//import nounou.elements.data.filters._
//import scala.beans.BeanProperty
//import nounou.elements.headers.{NNHeaderNull$$, NNHeader}
//
//
//object NNDataReader extends LazyLogging {
//
//  //ToDo 1: java.lang.IllegalStateException: Not on FX application thread
////  def load(): Array[X]  =  {
////    val fileChooser = new scalafx.stage.FileChooser
////    //val extFilter = new ExtensionFilter
////    val window = new scalafx.stage.Popup
////    fileChooser.showOpenDialog(window)
////
////    val filesChosen: List[File] = fileChooser.showOpenMultipleDialog(null).toList
////    loadPostProcess(filesChosen.flatMap( load(_) ).toArray)
////  }
//
//  def load(files: Array[String]): Array[NNElement] = loadPostProcess(files.flatMap( (file: String) => load( new File( file ) ) ))
//
//  def load(file: File): Array[NNElement] = {
//    loadPostProcess(
//    (file.getName.toLowerCase match {
//      //case n: String if n.endsWith(".nex") => FileAdapterNEX.load( file )
//      case n: String if n.endsWith(".ncs") => FileAdapterNCS.load( file )
//      case n: String if n.endsWith(".nev") => FileAdapterNEV.load( file )
//      //case n: String if (n.endsWith(".gsd") || n.endsWith(".gsh")) => FileAdapterGSDGSH.load( file )
//      case n => throw new IllegalArgumentException("File format for " + n + " is not supported yet.")
//    }).toArray )
//  }
//
//  def load(string: String): Array[NNElement] = load( new File(string) )
//
//  private def loadPostProcess(x: Array[NNElement]): Array[NNElement] = {
//    val tempretXDC = x.filter(_.isInstanceOf[NNDataChannel]).map(_.asInstanceOf[NNDataChannel])
//    if(tempretXDC.length > 1 && tempretXDC.head.isCompatible(tempretXDC.tail)){
//      Array( new NNDataChannelArray(tempretXDC) ) ++ x.filter(!_.isInstanceOf[NNDataChannel])
//    } else {
//      x
//    }
//  }
//
//}
//
///**
//* @author ktakagaki
//*/
//class NNDataReader extends LazyLogging {
//}

//  /**Header object covering the whole block.*/
//  var header: XHeader = XHeaderNull
//
//  /**Main data output.*/
//  def data(): XData = dataFIR
//  val dataORI: XDataFilterHolder = new XDataFilterHolder()
//  /**Auxiliary data, for instance, analog signals recorded with an optical trace.*/
//  def dataAux(): XDataAux = dataAuxORI //temporarily set to mirror
//  var dataAuxORI: XDataFilterHolder = new XDataFilterHolder()
//  //insert downsample block, filter block, buffer block
////Data layout has been encapsulated into XData
////  /**Layout of data*/
////  var layout: XLayout = XLayoutNull
//  /**Time masks (by timestamp) to deactivate segments of data--for example, to mark artifact areas*/
//  @BeanProperty
//  var mask: XMask = new XMask
//
//  /**Events*/
//  @BeanProperty
//  var events: XEvents = new XEvents //XEventsNull
//
//  /**Spikes*/
//  @BeanProperty
//  var spikes: XSpikes = XSpikesNull
//  def initializeSpikes(xData: XData, xTrodes: XTrodes, waveformLength: Int): Unit = {
//    spikes = XSpikes.initialize(xData, xTrodes, waveformLength)
//  }
//  // <editor-fold defaultstate="collapsed" desc=" filters, setting data/dataORI and dataAux/dataAuxORI ">
//
//  val dataDecimate: XDataFilterDecimate = new XDataFilterDecimate( dataORI )
//    val dataDecimateBuf: XDataFilterBuffer = new XDataFilterBuffer( dataDecimate )
//      val dataFIR: XDataFilterFIR = new XDataFilterFIR( dataDecimateBuf )
//      val dataStats: XDataFilterStatistics = new XDataFilterStatistics( dataDecimateBuf )
////      val dataRMSFIR: XDataFilterFIR = new XDataFilterFIR( dataDecimateBuf )
////        val dataRMS: XDataFilterRMS = new XDataFilterRMS( dataRMSFIR ) //new XDataFilterDownsample( dataRMSFIR, 5 ) )
////      val dataMAX: XDataFilterMinMaxAbs = new XDataFilterMinMaxAbs( dataDecimateBuf )
//
//  def setData(x: XData): Unit = {
//    dataORI.heldData = x
//    if(dataORI.sampleRate > 2000 && dataORI.segmentLength.reduce(_+_) > 200000) dataDecimate.factor_=(math.min(16, (dataORI.sampleRate / 2000).toInt))
////    val halfWindow = (dataRMSFIR.sampleRate * 0.05).toInt //50 ms
////    dataRMS.setHalfWindow(halfWindow)
////    dataMAX.setHalfWindow(halfWindow)
//  }
//  def setDataAux(x: XData): Unit = {
//    dataAuxORI.heldData = x
//  }
//  // </editor-fold>
//
//  // <editor-fold defaultstate="collapsed" desc=" artifact masking ">
//
//  def maskMovementArtifacts(f0: Double, f1: Double, rmsAbsThreshold: Double, absAbsThreshold: Double, stepSize: Int, maskHalfWindow: Long): Unit = {
//
//    mask.clear()
//
////    val stepSize = (dataRMSFIR.sampleRate * 0.05).toInt //100 ms
////    val maskHalfWindow = 150L * 1000L // 150 ms
////    dataRMSFIR.setTaps(64)
////    dataRMSFIR.setFilterHz(f0, f1)
////
////    val rmsThreshold = dataRMS.convertABStoINT( rmsAbsThreshold )
////    val absThreshold = dataRMS.convertABStoINT( absAbsThreshold )
////
////    for(seg <- 0 until dataRMS.segmentCount)
////    for(frame <- 0 until dataRMS.segmentLength(seg) by stepSize ) {
////      if( mean( (for(ch <- (0 until dataMAX.channelCount) ) yield dataMAX.readPoint(ch, frame, seg)).toVector ) >= absThreshold ||
////            mean( (for(ch <- (0 until dataRMS.channelCount) ) yield dataRMS.readPoint(ch, frame, seg)).toVector ) >= rmsThreshold ){
////        val frameTs = dataRMS.frToTs(frame)//frsgToTs(frame, seg)
////        mask.mask( frameTs - maskHalfWindow, frameTs + maskHalfWindow )
////      }
////    }
////
////    mask.eliminateOverlapping()
//
//  }
//
//  private def mean(vect: Vector[Int]): Int ={
//    var sum = 0
//    for(cnt <- 0 until vect.length){
//      sum += vect(cnt)
//    }
//    sum/vect.length
//  }
//
//  // </editor-fold>
//
//  // <editor-fold defaultstate="collapsed" desc=" Java convenience accessors (filtering, decimation, fourier) ">
//
//  def setFilterHz(f0: Double, f1: Double) = dataFIR.setFilterHz(f0, f1)
//  def setFilterOff() = dataFIR.setFilterOff()
//  def getFilterHz() = dataFIR.getFilterHz().toArray
//  def setDecimate(factor: Int) = dataDecimate.factor_=( factor )
//  def getDecimate() = dataDecimate.factor
//  // </editor-fold>
//
//
////  // <editor-fold defaultstate="collapsed" desc=" load/reload ">
////
////  //ToDo 1: java.lang.IllegalStateException: Not on FX application thread
////  def load(): Unit   =  {
////    val fileChooser = new scalafx.stage.FileChooser
////    //val extFilter = new ExtensionFilter
////    val window = new scalafx.stage.Popup
////    fileChooser.showOpenDialog(window)
////
////    val filesChosen: List[File] = fileChooser.showOpenMultipleDialog(null).toList
////    filesChosen.map(load(_))
////  }
////
////  def load(files: Array[String]): Unit = files.foreach( (fStr: String) => load( new File(fStr) ) )
////
////  def load(file: File, reload: Boolean = false): Unit = {
////    val list = file.getName.toLowerCase match {
////      case n: String if n.endsWith(".nex") => FileAdapterNEX.load( file )
////      case n: String if n.endsWith(".ncs") => FileAdapterNCS.load( file )
////      case n: String if n.endsWith(".nev") => FileAdapterNEV.load( file )
////      case n: String if (n.endsWith(".gsd") || n.endsWith(".gsh")) => FileAdapterGSDGSH.load( file )
////      case n => throw new IllegalArgumentException("File format for " + n + " is not supported yet.")
////    }
////    if(reload){
////      logger.info("Reloading file {}, obtained {} objects from FileAdapter.", file.getName, list.length.toString)
////    } else {
////      logger.info("Loading file: {}, obtained {} objects from FileAdapter.", file.getName, list.length.toString)
////    }
////    list.foreach( loadImpl(_) )
////  }
////
////  def load(x: X): Unit = loadImpl(x)
////
////  def load(string: String): Unit = load( new File(string) )
////
////  // </editor-fold>
////  // <editor-fold defaultstate="collapsed" desc=" reloadFlagXXX/reload ">
////
////  /** 0=load(not reload) 1=marked for reload 2=reloading/cleared
////    */
////  private var reloadFlagHead, reloadFlagData, reloadFlagDataAux,  reloadFlagMask, reloadFlagEvents, reloadFlagSpikes = 0    //Layout has been encapsulated into XData // reloadFlagLayout
////  private def setReloadFlags(value: Int): Unit = {
////    reloadFlagHead = value
////    reloadFlagData = value; reloadFlagDataAux = value
////    //reloadFlagLayout = value;
////    reloadFlagMask = value; reloadFlagEvents = value; reloadFlagSpikes = value
////  }
////  def reload(files: Array[File]): Unit = {
////    setReloadFlags(1)
////    files.map(load(_))
////    setReloadFlags(0)
////  }
////  def reload(file: File): Unit = {
////    setReloadFlags(1)
////    load(file)
////    setReloadFlags(0)
////  }
////
////  def reload(x: X): Unit = {
////    setReloadFlags(1)
////    loadImpl(x)
////    setReloadFlags(0)
////  }
////
////  def reload(): Unit = {
////    setReloadFlags(1)
////    load()
////    setReloadFlags(0)
////  }
////
////  def reload(string: String) = {
////    setReloadFlags(1)
////    load( new File(string) )
////    setReloadFlags(0)
////  }
////
////  // </editor-fold>
//  // <editor-fold defaultstate="collapsed" desc=" loadImpl ">
//
////  private def loadImpl(x: X): Unit = {
////    x match {
////      case x0: XHeader => {
////        if( header == XHeaderNull || reloadFlagHead == 1 ) header = x0
////        else if( reloadFlagHead == 0 ) {
////          if( header.isCompatible(x0) ) {
////            //ToDo 3: implement concatenating/comparing header?
////            logger.warn("Compatible header already loaded, ignoring new header {}. Use clearHead first or reload() instead of load(), if this is unintended.", x0)
////          } else {
////            logger.warn("Incompatible header already loaded, ignoring new header {}. Use clearHead first or reload() instead of load(), if this is unintended.", x0)
////          }
////        }
////      }
////      //handle XDataAux before XData, as it is a subtype trait of XData
////      //     0=load(not reload) 1=marked for reload 2=reloading/cleared
////      case x0: XDataAux => {
////        if( reloadFlagDataAux == 1 ) {
////          //reload dataAux regardless of what is already there, if the flag is 1
////          setDataAux(x0)
////          reloadFlagDataAux = 2
////        } else if ( dataAuxORI.heldData == XDataNull /*reloadFlagDataAux == 0, 1*/ ) {
////          //both load and reload data if dataAuxORI holds a null
////          setDataAux(x0)
////          reloadFlagDataAux = 2
////        } else if ( dataAuxORI.isCompatible(x0)  /*reloadFlagDataAux == 0, 2*/) {
////          //if flag is NOT 0 and if what is already loaded is compatible
////          setDataAux(dataAuxORI.heldData ::: x0)
////          reloadFlagDataAux = 2
////        } else { //not compatible with dataAux
////          logger.warn("Incompatible data already loaded in dataAux, ignoring new data {}. Use clearDataAux first or reload() instead of load(), if this is unintended.", x0)
////        }
////      }
////      case x0: XData => {
////        if( reloadFlagData == 1 ) {
////          //reload data regardless of what is already there, if the flag is 1
////          setData(x0)
////          reloadFlagData = 2
////        } else if( dataORI.heldData == XDataNull /*reloadFlagData == 0, 1*/) {
////          //both load and reload data if dataORI holds a null
////          setData(x0)
////          logger.info("A new XData object was loaded onto dataORI.heldData: {}", x0.toString())
////        } else if ( dataORI.isCompatible(x0)  /*reloadFlagData == 0, 2*/) {
////          //if flag is NOT 0 and if what is already loaded is compatible
////          setData(dataORI.heldData ::: x0)
////          logger.info("A new XData object was appended onto dataORI.heldData: {}", x0.toString())
////          reloadFlagData = 2
////        } else { //not compatible with data, try dataAux
////          logger.warn("Incompatible data already loaded in data and dataAux, ignoring new data {}. Use clearData/clearDataAux first or reload() instead of load(), if this is unintended.", x0)
////        }
////      }
////      case x0: XDataChannel => {
////        dataORI.heldData match {
////              case XDataNull => {
////                //if null, reload to regardless
////                setData( new XDataChannelArray( Vector[XDataChannel]( x0 ) ) )
////                logger.info("A new XDataChannel object was loaded onto dataORI.heldData: {}", x0.toString())
////                reloadFlagData = 2
////              }
////              case data0: XDataChannelArray => {
////                if( reloadFlagData == 1 ) {
////                  //overwrite and load to data regardless, if the reload flag is 1
////                  setData( new XDataChannelArray( Vector[XDataChannel]( x0 ) ) )
////                  logger.info("A new XDataChannel object was reloaded onto dataORI.heldData: {}", x0.toString())
////                  reloadFlagData = 2
////                } else if ( data0(0).isCompatible(x0)  /*reloadFlagData == 0, 2*/) {
////                  setData( data0 ::: x0 )
////                  logger.info("A new XDataChannel object was appended onto dataORI.heldData: {}", x0.toString())
////                  reloadFlagData = 2
////                } else { //not compatible with data, try dataAux
////                  logger.warn("Incompatible data already loaded in data, ignoring new data channel {}}. Use clearData first or reload() instead of load(), if this is unintended.", x0)
////                }
////              }
////              case data0:XData => { //if XDataChannel
////                if( reloadFlagData == 1 ) {
////                  //overwrite and load to data regardless, if the reload flag is 1
////                  setData( new XDataChannelArray( Vector[XDataChannel]( x0 ) ) )
////                  reloadFlagData = 2
////                } else if ( data0.isCompatible(x0)  /*reloadFlagData == 0, 2*/) {
////                  setData( data0 ::: x0 )
////                  reloadFlagData = 2
////                } else { //not compatible with data, try dataAux
////                  logger.warn("Incompatible data already loaded in data, ignoring new data channel {}}. Use clearData first or reload() instead of load(), if this is unintended.", x0)
////                }
////              }
////        }
////      }
////      case x0: XMask => sys.error("ToDo: have not implemented mask loading yet!")
////      case x0: XEvents => {
//////          if( events.length == 0 ) {
//////            events = x0
//////            if(reloadFlagEvents == 1) reloadFlagEvents = 2
//////          } else {
//////            reloadFlagEvents match {
//////              case 0 => { //not reloading, but concatenating
//////                if( events.isCompatible(x0) ) events = events ::: x0
//////                else logger.warn("Incompatible event objects already loaded, ignoring new XEvent {}. Use clearEvents first or reload() instead of load(), if this is unintended.", x0)
//////              }
//////              case 1 => { //reloading, not cleared previous data yet
//////                events = x0
//////                reloadFlagEvents = 2
//////              }
//////              case 2 => { //reloading, has already cleared
//////                if( events.isCompatible(x0) ) events = events ::: x0
//////                else logger.warn("Trying to load incompatible event objects, ignoring further new XEvent {}.", x0)
//////              }
//////            }
//////          }
////      }
////      case x0: XSpikes => {
////        if( spikes == XSpikesNull ) {
////          spikes = x0
////          if(reloadFlagEvents == 1) reloadFlagEvents = 2
////        } else {
////          reloadFlagEvents match {
////            case 0 => { //not reloading, but concatenating
////              if( spikes.isCompatible(x0) ) spikes = spikes ::: x0
////              else logger.warn("Incompatible spike objects already loaded, ignoring new XSpikes {}. Use clearSpikes first or reload() instead of load(), if this is unintended.", x0)
////            }
////            case 1 => { //reloading, not cleared previous data yet
////              spikes = x0
////              reloadFlagEvents = 2
////            }
////            case 2 => { //reloading, has already cleared
////              if( spikes.isCompatible(x0) ) spikes = spikes ::: x0
////              else logger.warn("Trying to load incompatible spike objects, ignoring further new XSpike {}.", x0)
////            }
////          }
////        }
////      }
////      case x0: X => logger.warn("Loading of this type of {} has not been implemented!", x0)
////    }
////  }
//
//    // </editor-fold>
//  // <editor-fold defaultstate="collapsed" desc=" clearing ">
//
//  def clearHead: Unit = {header = XHeaderNull}
//  def clearData: Unit = {setData(XDataNull)}
//  def clearDataAux: Unit = {setDataAux(XDataNull) }
//  //Layout has been encapsulated into XData
//  //def clearLayout: Unit = {layout = XLayoutNull}
//  def clearMask: Unit = {mask = new XMask}
//  def clearEvents: Unit = {events = new XEvents}
//  def clearSpikes: Unit = {spikes = XSpikesNull}
//
//  def clearAll: Unit = {
//    clearHead
//    clearData
//    clearDataAux
//    //Layout has been encapsulated into XData
//    //clearLayout
//    clearMask
//    clearEvents
//    clearSpikes
//  }
//
//  // </editor-fold>
//
//  override def toString() = "NNDataReader( data ch:" + dataORI.channelCount + ", dataAux ch:" + dataAuxORI.channelCount +
//                                         //", layout: " + layout +
//                                         ", mask: " + mask + ", events: " + events.lengths + ")"
//
//  def toStringChain(): String = {
//    val tempstr =
//      "[header  ] " + header + "\n" +
//      "[dataORI ] " + dataORI.toStringChain()+ "\n" +
//      "[dataAux ] " + dataAuxORI.toStringChain()+ "\n" +
////      "[layout  ] " + layout+ "\n" +
//      "[mask    ] " + mask+ "\n" +
//      "[events  ] " + events+ "\n" +
//      "[spikes  ] " + spikes
//
//
//      "NNDataReader LOADED DATA SUMMARY\n================================\n" + toString() +"\n" + (tempstr split "\n").flatMap( "\n     " + _).mkString
//  }
//}
