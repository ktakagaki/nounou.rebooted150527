package nounou.data

import breeze.linalg.{DenseVector, DenseMatrix}
import nounou.NN._

object XSpike {

  def toArray(xSpike : XSpike): Array[Array[Int]] = xSpike.toArray
  def toArray(xSpikes: Array[XSpike]): Array[Array[Array[Int]]] = xSpikes.map( _.toArray )

  def readSpikes(xData: XData, channels: Array[Int], xFrames: Array[Int], length: Int, trigger: Int) = {
    xFrames.map( readSpike(xData, channels, _, length, trigger))
  }
  def readSpike(xData: XData, channels: Array[Int], frame: Int, length: Int, trigger: Int) = {
    val tempWF = new DenseVector( new Array[Int]( channels.length * length ) )
    for(ch <- 0 until channels.length){
      tempWF(ch*length until (ch+1)*length) :=
        xData.readTrace(ch, SampleRange(frame-trigger, frame-trigger + length - 1, step = 1, segment= xFrame.segment))//, OptSegment(xFrame.segment)))
    }
    new XSpike( new DenseMatrix( rows = length, cols = channels.length, data = tempWF.toArray ),
                frame, trigger, unitNo = 0)
  }

}

/**
* @author ktakagaki
* @date 07/14/2014.
*/
class XSpike(val waveform: DenseMatrix[Int],
             val frame: Int,
             val trigger: Int = -1,
             val unitNo: Int = 0) extends X {

  lazy val channels = waveform.cols
  val length = waveform.rows

  loggerRequire( length != 0, "waveform must have some samples!")
  loggerRequire( trigger < length, "trigger point must be negative or smaller than length!")
  //if(xTrode.channelCount != waveform.cols) throw loggerError("waveform must include same number of channels as xTrode!")

  override def toString = "XSpike( unitNo=" + unitNo + ", channels="+ channels + ", length="+ length + " )"


  def toArray() = Array.tabulate(channels)(p => waveform( :: , p ).toArray )
//  def frame() = xFrame.frame
//  def segment() = xFrame.segment

  def sort(unitNo: Int) = new XSpike(waveform, frame, unitNo, trigger)

  override def isCompatible(that: X) = false
}
