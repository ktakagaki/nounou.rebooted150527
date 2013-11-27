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

  def segmentCount(): Int

  def dat(seg: Int = 0): XData
  def aux(seg: Int = 0): XData
  def eve(seg: Int = 0): XEvents
  def spk(seg: Int = 0): XSpikes
  def lay(seg: Int = 0): XLayout
  //ToDo: 3 video?

//  override def isCompatible(that: Source): Boolean = {
//    data.isCompatible(that) && events.isCompatible(that) && spikes.isCompatible(that)
//  }

}
