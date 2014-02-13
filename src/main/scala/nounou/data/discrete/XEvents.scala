package nounou.data.discrete

import nounou.data.X
import scala.collection.immutable.TreeMap
import nounou.data.traits.XConcatenatable

/**Immutable class to encapsulate marked events in data recording, with onset time and duration.
 *
 * @author ktakagaki
 */
class XEvents(val events: TreeMap[Long, XEvent], val name: String ) extends  X with XConcatenatable {

  lazy val length: Int = events.size
  lazy val maxDuration: Long = events.map( _._2.duration).max
  lazy val uniqueEventCodes = events.map(_._2.eventCode).toList.distinct.toVector
  lazy val sortedEvents = new Array[TreeMap[Long,XEvent]]( uniqueEventCodes.length )

  def getEventsByCode( eventCode: Int ): TreeMap[Long, XEvent] = {
    val codeIndex = uniqueEventCodes.indexOf( eventCode )
    if( sortedEvents(codeIndex) == null ){
      sortedEvents(codeIndex) = events.filter( _._2.eventCode == eventCode )
    }
    sortedEvents(codeIndex)
  }

  def apply(timeStamp: Long) = events.apply(timeStamp)
  //def nextEvent(timeStamp: Long): XEvent
  //def nextEvent(timeStamp: Long, eventCode: Int): XEvent
  //def previousEvent(timeStamp: Long): XEvent
  //def getEvents(timeStamp0: Long, timeStamp1: Long): Vector[XEvent]
  //def getEvents(timeStamp: Long): Vector[XEvent]
  //def getEventList: Vector[XEvent]
  //def containsEvent(timeStamp1: Long, timeStamp2: Long): Boolean

  // <editor-fold desc="XConcatenatable">

  override def :::(that: X): XEvents = {
    that match {
      case x: XEvents => {
        val newName = if(name == x.name) name else name + " and " + x.name
        new XEvents( this.events ++ x.events, newName )
      }
      case _ => {
        require(false, "cannot concatenate different types!")
        this
      }
    }
  }

  override def isCompatible(that: X): Boolean =
    that match {
      case x: XEvents => true
//      {
//        (super[XDiscrete].isCompatible(x) && super[XDiscrete].isCompatible(x))
//      }
      case _ => false
    }

  // </editor-fold>

  override def toString() = {
    "XEvents( " + length + " events total, name=" + name + " )"
  }

}

object XEventsNull extends XEvents(TreeMap[Long, XEvent](), "Null events!")