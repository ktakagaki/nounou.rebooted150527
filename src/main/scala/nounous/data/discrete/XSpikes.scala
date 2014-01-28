package nounous.data.discrete

import nounous.data.X
import scala.collection.immutable.TreeMap

//import nounous.data.X

/**
  * Created with IntelliJ IDEA.
  * User: takagaki
  * Date: 23.09.13
  * Time: 13:14
  * To change this template use File | Settings | File Templates.
  */
class XSpikes(override val events: TreeMap[Long, XSpike],
              override val name: String,
              val waveFormLength: Int ) extends XEvents(events, name) {



}
