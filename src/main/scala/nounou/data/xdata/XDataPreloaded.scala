package nounou.data

import nounou._
import nounou.data.traits.{XFramesImmutable, XConcatenatable}
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
                       override val scaleMax: Int,
                       override val scaleMin: Int,
                       //override val channelNames: Vector[String], // = Vector.tabulate[String](data.length)(i => "no channel name")
                       override val segmentStartTs: Array[Long],
              override val segmentLength: Array[Int],
                       override val sampleRate: Double,
                       override val layout: XLayout = XLayoutNull
                      )
  extends XData with XConcatenatable with XFramesImmutable {

//    override final lazy val segmentLength = data.map( (p: DM[Int]) => p.rows ).toVector


    loggerRequire(channelCount == data(0).cols,
      "number of channels " + channelCount + " does not match the data columns" + data(0).cols + "!")
    loggerRequire(data.length == segmentStartTs.length && data.length == segmentLength.length,
      "Given data, segmentStartTs, and segmentLength must all have the same length. Actual lengths were {}, {}, {}.",
       data.length.toString, segmentStartTs.length.toString, segmentLength.length.toString)

    //reading
    override def readPointImpl(channel: Int, frame: Int, segment: Int) = data(segment)(channel, frame)

    override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int) = {
      data(segment)( range, channel ).toDenseVector
    }

  // <editor-fold desc="XConcatenatable">

    override def :::(that: X): XData = {
      that match {
        case t: XDataPreloaded => {
          if(this.isCompatible(that)){

            val oriThis = this
            new XDataPreloaded( data = oriThis.data.zip(t.data).map( (tup: (DM[Int], DM[Int])) => DM.horzcat(tup._1, tup._2) ).toArray,
                                xBits = oriThis.xBits,
                                absGain = oriThis.absGain,
                                absOffset = oriThis.absOffset,
                                absUnit = oriThis.absUnit,
                                scaleMax = oriThis.scaleMax,
                                scaleMin = oriThis.scaleMin,
                                //channelNames = oriThis.channelNames ++ t.channelNames,
                                segmentStartTs = oriThis.segmentStartTs,
                    segmentLength = oriThis.segmentLength,
                                sampleRate = oriThis.sampleRate,
                                layout = oriThis.layout ::: t.layout
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
                    scaleMax: Int,
                    scaleMin: Int,
                    //channelNames: Vector[String], // = Vector.tabulate[String](data.length)(i => "no channel name")
                    segmentStartTs: Long,
                    sampleRate: Double,
                    layout: XLayout = XLayoutNull
                    )
  extends XDataPreloaded( Array(data),
                          xBits,
                          absGain, absOffset, absUnit,
                          scaleMax, scaleMin, /*channelNames,*/ Array[Long](segmentStartTs),
    Array[Int](data.rows),
                          sampleRate, layout){

}
