package nounous.data.sources

import nounous.data.x._
import nounous.data.xdata.XData
import nounous.data.x.xlayout.{XLayoutNull, XLayout}
import nounous.data.x.xdiscrete.XEvents

/**
 * Created with IntelliJ IDEA.
 * User: takagaki
 * Date: 18.09.13
 * Time: 19:03
 * To change this template use File | Settings | File Templates.
 */
abstract class DataSource {

  /**Header object covering the whole block.*/
  var head: XHeader
  /**Main data output.*/
  var dat: XData
  /**Auxiliary data, for instance, analog signals recorded with an optical trace.*/
  var datA: XData
  /**Layout of data*/
  var datL: XLayout
  /**Mask.*/
  var datM: XEvents

  /**Events.*/
  var eve: Vector[XEvents]

//  /**Spikes.*/
//  var spk: XSpikes   //ToDo: 1 spikes


  //ToDo: 3 video?


}
