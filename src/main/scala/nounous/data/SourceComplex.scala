package data

import nounous.data.Source
import nounous.data.SourceData

/**
 * Created with IntelliJ IDEA.
 * User: takagaki
 * Date: 18.09.13
 * Time: 19:03
 * To change this template use File | Settings | File Templates.
 */
abstract class SourceComplex extends Source {

  val data: SourceData
  val events: SourceEvents
  val spikes: SourceSpikes

  override def isCompatible(that: Source): Boolean = {
    data.isCompatible(that) && events.isCompatible(that) && spikes.isCompatible(that)
  }

}
