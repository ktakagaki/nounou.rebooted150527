package nounou.data

/**An immutable class to encapsulate a single event mark in a neurophysiological recording.
  */
class XSpike(val waveform: Vector[Int],
             val trodeNumber: Int = -1,
             val unitNumber: Int = 0) {

  override def toString = "XSpike( unitNumber=" + unitNumber+", trodeNumber="+ trodeNumber +", waveform.length="+ waveform.length+" )"


 }
