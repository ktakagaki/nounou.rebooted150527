package nounous.data.sources

import java.io.File
import nounous.data.x._
import nounous.data.xdata.{XDataNull, XData}
import nounous.reader.FileLoaderNEX$$
import nounous.data.x.xdiscrete.{XEventsNull, XEvents}
import nounous.data.x.xlayout.{XLayout, XLayoutNull}


/**
 * @author ktakagaki
 */
class DataSourceFileLoader extends DataSource {

  override var head: XHeader = XHeaderNull
  override var dat: XData = XDataNull
  override var datA: XData = XDataNull
  override var datL: XLayout = XLayoutNull
  override var datM: XEvents = XEventsNull
  override var eve = Vector[XEvents]()
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
