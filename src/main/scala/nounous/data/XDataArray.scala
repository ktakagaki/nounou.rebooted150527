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
 abstract class XDataArray(val data: Vector[Vector[Int]]) extends XData{

  override val length = data(0).length

  override def readPointImpl(ch: Int, fr: Int) = data(ch)(fr)
  override def readTraceImpl(ch: Int) = data(ch)

  override def :::(that: X): X = {
    that match {
      case t: XDataArray => {
        if(this.isCompatible(that)){
          val oriThis = this
          new XDataArray( this.data ++ t.data ){
            override val absGain = oriThis.absGain
            override val absOffset = oriThis.absOffset
            override val absUnit = oriThis.absUnit

            override val sampleRate = oriThis.sampleRate
            override val startFrames = oriThis.startFrames
            override val startTimestamps = oriThis.startTimestamps

            override val xBits = oriThis.xBits

            override val channelNames = oriThis.channelNames ++ t.channelNames
            override val channelCount = oriThis.channelCount + t.channelCount
          }
        }else{
          throw new IllegalArgumentException("the two XDataArray types are not compatible, and cannot be concatenated.")
        }
      }
      case _ => throw new IllegalArgumentException("the two X types are not compatible, and cannot be concatenated.")
    }
  }



}
