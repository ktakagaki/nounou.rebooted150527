package nounou.data.discrete

/**An immutable class to encapsulate a single event mark in a neurophysiological recording.
  */
class XSpike(override val duration: Long,
             override val eventCode: Int,
             override val string: String,
             val waveform: Vector[Int],
             val unitNumber: Int) extends XEvent(duration, eventCode, string) {



 }
