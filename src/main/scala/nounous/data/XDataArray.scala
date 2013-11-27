package nounous.data

//import nounous.data.X
//import nounous.data.XData

/**XData class with internal representation as data array
 * Created with IntelliJ IDEA.
 * User: takagaki
 * Date: 13.09.13
 * Time: 18:50
 * To change this template use File | Settings | File Templates.
 */
class XDataArray(
    val sampling: Double,
    val start: Double,
    val length: Int,
    val xBits: Int,

    val absGain: Double,
    val absOffset: Double,
    val absUnit: String,

    val data: Array[Array[Int]],
    val channelNames: Array[String],
    val channelCount: Int
) extends XData{


  override def readPointImpl(seg: Int, ch: Int, fr: Int) = data(ch)(fr)
  override def readTraceImpl(seg: Int, ch: Int) = data(ch)


  override def :::(that: X): X = {
    if(this.isCompatible(that)){
      val t = that.asInstanceOf[this.type]
      val oriThis = this
      new XDataArray(
        sampling = oriThis.sampling,
        start = oriThis.start,
        length = oriThis.length,
        xBits = oriThis.xBits,

        absGain = oriThis.absGain,
        absOffset = oriThis.absOffset,
        absUnit = oriThis.absUnit,

        data =  oriThis.data ++ t.data,
        channelNames = oriThis.channelNames ++ t.channelNames,
        channelCount = oriThis.channelCount + t.channelCount
      )
    } else {
      this
    }
  }



}
