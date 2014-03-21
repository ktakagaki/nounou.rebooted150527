package nounou.data

/**An immutable class to encapsulate a single spike (or n-trode spike), with its waveform, in a neurophysiological recording.
  */
class XSpike(val waveform: Array[Array[Int]], val sortedUnit: Int = 0) {

  override def toString = "XSpike( sortedUnit=" + sortedUnit + ", trode channels="+ waveform.length +", waveform.length="+ waveform(0).length+" )"


 }
