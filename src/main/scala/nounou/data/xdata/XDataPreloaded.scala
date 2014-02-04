package nounou.data

import nounou._
import nounou.data.traits.XConcatenatable

/**xdata class with internal representation as data array
 * Created with IntelliJ IDEA.
 * User: takagaki
 * Date: 13.09.13
 * Time: 18:50
 * To change this template use File | Settings | File Templates.
 */
class XDataPreloaded(  val data: Vector[Vector[Vector[Int]]],
                       override val xBits: Int,
                       override val absGain: Double,
                       override val absOffset: Double,
                       override val absUnit: String,
                       override val channelNames: Vector[String], // = Vector.tabulate[String](data.length)(i => "no channel name")
                       override val segmentStartTSs: Vector[Long] = Vector(0L),
                       override val sampleRate: Double = 1d
                      )
  extends XDataImmutable with XConcatenatable {

    override lazy val segmentLengths: Vector[Int] = data(0).map(_.length)

    require(channelCount == data.length,
      "number of channels " + channelCount + " does not match data.length " + data.length + "!")

    //reading
    override def readPointImpl(channel: Int, frame: Int, segment: Int) = data(channel)(frame)(segment)

    override def readTraceImpl(channel: Int, range: FrameRange, segment: Int) = {
//      range match {
//        case Span.All => data(channel)(segment)
//        case _ => {
//          val slice = span.getStartEndIndexes( segmentLengths(segment) )
          data(channel)(segment).slice(range.start, range.last)
//        }
//      }
    }

  // <editor-fold desc="XConcatenatable">

    override def :::(that: X): XData = {
      that match {
        case t: XDataPreloaded => {
          if(this.isCompatible(that)){

            val oriThis = this
            new XDataPreloaded( data = oriThis.data ++ t.data,
                                xBits = oriThis.xBits,
                                absGain = oriThis.absGain,
                                absOffset = oriThis.absOffset,
                                absUnit = oriThis.absUnit,
                                segmentStartTSs = oriThis.segmentStartTSs,
                                sampleRate = oriThis.sampleRate,
                                channelNames = oriThis.channelNames ++ t.channelNames
            )
          } else {
            throw new IllegalArgumentException("the two XDataPreloaded types are not compatible, and cannot be concatenated.")
          }
        }
        case _ => throw new IllegalArgumentException("the two X types are not compatible, and cannot be concatenated.")
      }
    }

  // </editor-fold>


}
