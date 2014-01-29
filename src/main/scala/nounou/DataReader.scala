package nounou

import java.io.File
import nounou.data.xdata.{XDataNull, XData}
import nounou.data.xlayout.{XLayout, XLayoutNull}
import nounou.data.loaders.FileLoaderNEX
import nounou.data._
import nounou.data.discrete.{XEventsNull, XEvents}


/**
 * @author ktakagaki
 */
class DataReader {

  /**Header object covering the whole block.*/
  var head: XHeader = XHeaderNull
  /**Main data output.*/
  var dat: XData = XDataNull
  //insert downsample block, filter block, buffer block
  /**Auxiliary data, for instance, analog signals recorded with an optical trace.*/
  var datA: XData = XDataNull
  //insert downsample block, filter block, buffer block
  /**Layout of data*/
  var datL: XLayout = XLayoutNull
  /**Mask*/
  var datM: XEvents = XEventsNull
  /**Events*/
  var eve = Vector[XEvents]()
//  override var spk: XSpikes

  def load(): Unit   =  {
    val fileChooser = new scalafx.stage.FileChooser
    //val extFilter = new ExtensionFilter
    val window = new scalafx.stage.Popup
    fileChooser.showOpenDialog(window)

    val filesChosen: List[File] = fileChooser.showOpenMultipleDialog(null).toList
    load(filesChosen.toArray)
  }

  def load(files: Array[File]): Unit = files.map(load(_))

  def load(file: File): Unit = {
    val list = file.getName.toLowerCase match {
      case n: String if n.endsWith(".nex") => FileLoaderNEX.load( file )
      case n => throw new IllegalArgumentException("File format for " + n + " is not supported yet.")
    }
    list.map( loadImpl(_) )
  }

  def loadImpl(data: X): Unit = {
    data match {
      case x: XHeader => head = x
      case x: XData => {
        if( dat == XDataNull ) dat = x
        else if (dat.isCompatible(x)) dat = (dat ::: x).asInstanceOf[XData]
        else if ( datA == XDataNull ) datA = x
        else if (datA.isCompatible(x)) datA = (datA ::: x).asInstanceOf[XData]
        else require(false, "new data " + x + "is not compatible with previously loaded data!")
      }
    }
  }


  def clearHead: Unit = {head = XHeaderNull}
  def clearDat: Unit = {dat = XDataNull}
  def clearDatA: Unit = {datA = XDataNull}
  def clearDatL: Unit = {datL = XLayoutNull}
  def clearDatM: Unit = {datM = XEventsNull}
  def clearEve: Unit = {eve = Vector[XEvents]()}

  def clearAll: Unit = {
    clearHead
    clearDat
    clearDatA
    clearDatL
    clearDatM
    clearEve
  //    spk  = null
  }

 }
