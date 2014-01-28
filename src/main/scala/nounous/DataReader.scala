package nounous

import java.io.File
import nounous.data.xdata.{XDataNull, XData}
import nounous.data.xlayout.{XLayout, XLayoutNull}
import nounous.reader.FileLoaderNEX$$
import nounous.data._
import nounous.data.discrete.{XEventsNull, XEvents}


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


  def load(file: File): Unit = load( Array(file) )
  def load(files: Array[File]): Unit = files.flatMap( loadImpl(_) )
  def loadImpl(file: File): List[X] = {
    file.getName.toLowerCase match {
      case n: String if n.endsWith(".nex") => FileLoaderNEX$$.load( file )
      case n => throw new IllegalArgumentException("File format for " + n + " is not supported yet.")
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
