package nounou.data

/**An immutable class to encapsulate a single spike (or n-trode spike), with its waveform, in a neurophysiological recording.
  */
class XSpikeWaveform(val waveform: Array[Array[Int]], val sortedUnit: Int = -1) {

  lazy val length = waveform(0).length
  lazy val trodeCount = waveform.length

  override def toString = "XSpikeWaveform( sortedUnit=" + sortedUnit + ", trodeCount="+ trodeCount +", waveform length="+ length+" )"

  def sort(unit: Int) = new XSpikeWaveform(waveform, unit)


 }
