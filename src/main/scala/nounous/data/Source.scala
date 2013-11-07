package nounous.data

//import nounous.data.XData
//import nounous.data.XLayout

/**
 * Created with IntelliJ IDEA.
 * User: takagaki
 * Date: 18.09.13
 * Time: 19:03
 * To change this template use File | Settings | File Templates.
 */
abstract class Source{

  def dat(): XData
  def aux(): XData
  def eve(): XEvents
  def spk(): XSpikes
  def lay(): XLayout

//  override def isCompatible(that: Source): Boolean = {
//    data.isCompatible(that) && events.isCompatible(that) && spikes.isCompatible(that)
//  }

}
