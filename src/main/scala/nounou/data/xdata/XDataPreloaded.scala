package nounou.data

import nounou._
import nounou.data.traits.XConcatenatable
import breeze.linalg.{DenseMatrix => DM, DenseVector => DV}

/**xdata class with internal representation as data array
 * Created with IntelliJ IDEA.
 * User: takagaki
 * Date: 13.09.13
 * Time: 18:50
 * To change this template use File | Settings | File Templates.
 */
class XDataPreloaded(  val data: Array[DM[Int]],
                       override val xBits: Int,
                       override val absGain: Double,
                       override val absOffset: Double,
                       override val absUnit: String,
                       override val channelNames: Vector[String], // = Vector.tabulate[String](data.length)(i => "no channel name")
                       override val segmentStartTSs: Vector[Long],
                       override val sampleRate: Double,
                       override val layout: XLayout = XLayoutNull
                      )
  extends XDataImmutable with XConcatenatable {

    override val segmentLengths = data.map( (p: DM[Int]) => p.rows ).toVector


    require(channelCount == data(0).rows,
      "number of channels " + channelCount + " does not match data.length " + data.length + "!")

    //reading
    override def readPointImpl(channel: Int, frame: Int, segment: Int) = data(segment)(channel, frame)

    override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int) = {
      data(segment)( channel, range ).toDenseVector
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
                                channelNames = oriThis.channelNames ++ t.channelNames,
                                segmentStartTSs = oriThis.segmentStartTSs,
                                sampleRate = oriThis.sampleRate,
                                layout = oriThis.layout
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

class XDataPreloadedSingleSegment(
                    data: DM[Int],
                    xBits: Int,
                    absGain: Double,
                    absOffset: Double,
                    absUnit: String,
                    channelNames: Vector[String], // = Vector.tabulate[String](data.length)(i => "no channel name")
                    segmentStartTS: Long,
                    sampleRate: Double,
                    layout: XLayout = XLayoutNull
                    )
  extends XDataPreloaded( Array(data), xBits, absGain, absOffset, absUnit, channelNames, Vector[Long](segmentStartTS), sampleRate, layout)
