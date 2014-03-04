package nounou.data

/**An immutable class to encapsulate a single event mark in a neurophysiological recording.
 */
class XEvent(val duration: Long, val eventCode: Int, val string: String) {

  override def toString = "XEvent(" + duration +","+ eventCode+","+ string+")"


}
