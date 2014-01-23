package nounous

import nounous.data.sources.DataSourceFileLoader
import nounous.data.x.{XHeaderNull, XHeader}
import nounous.data.xdata.{XDataNull, XData}
import nounous.data.x.xlayout.{XLayout, XLayoutNull}
import nounous.data.x.xdiscrete.{XEvents, XEventsNull}

/**
 * @author ktakagaki
 */
class DataReader extends DataSourceFileLoader {

  val dataSource = new DataSourceFileLoader

  override var head = dataSource.head
  override var dat = dataSource.dat
  //insert downsample block, filter block, buffer block
  override var datA = dataSource.datA
  //insert downsample block, filter block, buffer block
  override var datL = dataSource.datL
  override var datM = dataSource.datM
  override var eve = dataSource.eve
  //  override var spk: XSpikes



  override def clearHead: Unit = {dataSource.clearHead}
  override def clearDat: Unit =  {dataSource.clearDat}
  override def clearDatA: Unit = {dataSource.clearDatA}
  override def clearDatL: Unit = {dataSource.clearDatL}
  override def clearDatM: Unit = {dataSource.clearDatM}
  override def clearEve: Unit = {dataSource.clearEve}

}
