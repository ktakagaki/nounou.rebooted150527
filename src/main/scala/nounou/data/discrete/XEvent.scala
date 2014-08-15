package nounou.data

object XEvent {
  implicit object XEventOrdering extends Ordering[XEvent] {
    override def compare(a: XEvent, b: XEvent) = a.timestamp compare b.timestamp
  }
  def overrideDuration(xEvent: XEvent, duration: Long) = new XEvent(xEvent.timestamp, duration, xEvent.code, xEvent.comment)
  def overrideCode(xEvent: XEvent, code: Int) = new XEvent(xEvent.timestamp, xEvent.duration, code, xEvent.comment)
}

/**An immutable class to encapsulate a single event in a neurophysiological recording.
 */
class XEvent(val timestamp: Long, val duration: Long, val code: Int, val comment: String) {

  override def toString = "XEvent(" + timestamp +", "+ duration +", "+ code+", "+ comment + ")"

}
