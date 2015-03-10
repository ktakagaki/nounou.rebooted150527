package nounou.elements

object NNEvent {
  implicit object XEventOrdering extends Ordering[NNEvent] {
    override def compare(a: NNEvent, b: NNEvent) = a.timestamp compare b.timestamp
  }
  def overrideDuration(xEvent: NNEvent, duration: Long) = new NNEvent(xEvent.timestamp, duration, xEvent.code, xEvent.comment)
  def overrideCode(xEvent: NNEvent, code: Int) = new NNEvent(xEvent.timestamp, xEvent.duration, code, xEvent.comment)
}

/**An immutable class to encapsulate a single event in a neurophysiological recording.
 */
class NNEvent(val timestamp: Long, val duration: Long, val code: Int, val comment: String) {

  override def toString = "XEvent(" + timestamp +", "+ duration +", "+ code+", "+ comment + ")"

}
