package nounou.data

/**An immutable class to encapsulate a single spike (or n-trode spike), with its waveform, in a neurophysiological recording.
  */
class XSpike(val waveform: Array[Array[Int]], val sortedUnit: Int = 0) {

  lazy val length = waveform(0).length
  lazy val trodes = waveform.length

  override def toString = "XSpike( sortedUnit=" + sortedUnit + ", trode channels="+ trodes +", waveform length="+ length+" )"

  def sort(unit: Int) = new XSpike(waveform, unit)


 }
