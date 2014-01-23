package nounous.data.xdata

import nounous.data.traits.XConcatenatable
import nounous.data.x.X

/**xdata class with internal representation as data array
 * Created with IntelliJ IDEA.
 * User: takagaki
 * Date: 13.09.13
 * Time: 18:50
 * To change this template use File | Settings | File Templates.
 */
 abstract class XDataPreloaded extends XDataImmutable with XConcatenatable {

//   //To override
   val data: Vector[Vector[Vector[Int]]]
//   override val startTimestamp: Vector[Long],
//   override val sampleRate: Double,
//   override val channelName: Vector[String],
//   override val xBits: Int,
//   override val absGain: Double,
//   override val absOffset: Double,
//   override val absUnit: String

    //segments, length
    override lazy val length: Vector[Int] = data(0).map(_.length)
    override lazy val segments = data(0).length


    //channel information
    require(channelName.length == data.length,
      "number of _channelName elements " + channelName.length + " does not match data.length " + data.length + "!")

    //reading
    override def readPointImpl(segment: Int, channel: Int, frame: Int) = data(segment)(channel)(frame)
    override def readTraceImpl(segment: Int, channel: Int) = data(segment)(channel)

    override def :::(that: X): X = {
      that match {
        case t: XDataPreloaded => {
          if(this.isCompatible(that)){
            val oriThis = this
            new XDataPreloaded {
              override val data = oriThis.data ++ t.data
              override val startTimestamp = oriThis.startTimestamp
              override val sampleRate = oriThis.sampleRate
              override val channelName = oriThis.channelName ++ t.channelName
              override val xBits = oriThis.xBits
              override val absGain = oriThis.absGain
              override val absOffset = oriThis.absOffset
              override val absUnit = oriThis.absUnit
            }

          } else {
            throw new IllegalArgumentException("the two XDataPreloaded types are not compatible, and cannot be concatenated.")
          }
        }
        case _ => throw new IllegalArgumentException("the two X types are not compatible, and cannot be concatenated.")
      }
    }



}
