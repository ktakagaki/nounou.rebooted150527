package nounou.elements.discrete

import nounou.elements.NNElement
import nounou.util.LoggingExt
import breeze.linalg.{DenseVector, DenseMatrix}
import nounou.NN._
import nounou.elements.data.NNData

object NNSpike extends LoggingExt {

//  def toArray(xSpike : XSpike): Array[Array[Int]] = xSpike.toArray
//  def toArray(xSpikes: Array[XSpike]): Array[Array[Array[Int]]] = xSpikes.map( _.toArray )
  def readSpikeFrames(xData: NNData, channels: Array[Int], xFrames: Array[Int], length: Int, trigger: Int) = {
    xFrames.map( readSpikeFrame(xData, channels, _, length, trigger))
  }
  def readSpikeFrame(xData: NNData, channels: Array[Int], frame: Int, segment: Int, length: Int): NNSpike = {
    loggerRequire( frame > 0, s"frame must be >0, not ${frame}")
    loggerRequire( length > 0, s"length must be >0, not ${length}")

    val tempWF = channels.map( ch => xData.readTrace( ch, SampleRangeReal(frame, frame+length-1, step=1, segment) ))
    new NNSpikeFrame( frame, tempWF, frame, segment)
  }

}

/**Encapsulates a single spike waveform which will be accumulated into
  * [[nounou.elements.discrete.NNSpikes]] database of spikes.
* @author ktakagaki
* //@date 07/14/2014.
*/
abstract class NNSpike(val time: Long, val waveform: Array[Array[Int]], var unitNo: Int = 0)
  extends NNElement {

  val channels = waveform.length
  val length = waveform(0).length
  def getUnitNo() = unitNo
  def setUnitNo(unitNo: Int) = {this.unitNo = unitNo}

  loggerRequire( channels >= 1, s"Waveform must have at least one channel, $channels is invalid!")
  loggerRequire( length >= 0, "Waveform must have some samples, $length is invalid!")
  loggerRequire( nounou.util.isMatrix(waveform), "Waveform is not non-null and non-ragged")

  override def toString = s"XSpike(time=${time}, channels=${channels}, length=${length}, unitNo=${unitNo}} )"


//  def toArray() = Array.tabulate(channels)(p => waveform( :: , p ).toArray )

  override def isCompatible(that: NNElement) = false
}

class NNSpikeFrame(override val time : Long,
                  override val waveform: Array[Array[Int]],
                  unitNo: Int = 0,
                  val segment: Int)
  extends NNSpike(time, waveform, unitNo){

    lazy val frame = time.toInt

}

