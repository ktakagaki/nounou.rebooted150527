package nounou.data

/**An immutable class to encapsulate a single spike (or n-trode spike), with its waveform, in a neurophysiological recording.
  */
class XSpike(val waveform: Array[Array[Int]],
             val channel: Int = -1,
             val trode: Int = -1,
             val unit: Int = 0) {

  override def toString = "XSpike( channel="+channel+"unit=" + unit+", trode="+ trode +", waveform.length="+ waveform.length+" )"


 }
