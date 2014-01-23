package nounous.data.x.xdiscrete

/**Class to encapsulate marked events in data recording, with onset time and duration.
 *
 * @author ktakagaki
 */
class XEvents(val timeStamps: Vector[Long],
              /** Time duration of each record in timestamp units. */
              val durations: Vector[Long]) extends XDiscrete {

  lazy val length: Int = timeStamps.length


  //def nextEvent(timeStamp: Long): Int
  //def getEvent(timeStamp: Long): (Long, Long)
  //def containsEvent(timeStamp1: Long, timeStamp2: Long): Boolean


//  override def :::(that: X): XEvents = {
//    if(this.isCompatible(that)){
//      val sortedZipped =
//        (this.timeStamps ::: that.timeStamps) zip (this.durations ::: that.durations)
//      new XEvents(
//      )
//  } else {
//
//  }
//
//  override def isCompatible(that: X): Boolean =
//    that match {
//      case x: XEvents => {
//        (super[XDiscrete].isCompatible(x) && super[XDiscrete].isCompatible(x))
//      }
//      case _ => false
//    }

}

object XEventsNull extends XEvents(Vector[Long](), Vector[Long]())