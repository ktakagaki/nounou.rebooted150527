package nounou.elements.data

import nounou.elements.ranges.{SampleRangeValid}
import nounou.elements.traits.{NNDataTiming, NNDataScale}
import breeze.linalg.{DenseMatrix => DM, DenseVector => DV}

/**NNData class with internal representation as data array.
 */
class NNDataPreloaded( val data: Array[DV[Int]], timingEntry: NNDataTiming, scaleEntry: NNDataScale)
  extends NNData  {

    setScale(scaleEntry)
    setTiming(timingEntry)

    override def getChannelCount: Int = data.length

    override def readPointImpl(channel: Int, frame: Int, segment: Int) =
      data(channel)(timing.segmentStartFrame(segment) + frame)

    override def readTraceDVImpl(channel: Int, rangeFrValid: SampleRangeValid) = {
      data( channel )(
              rangeFrValid.toRangeInclusive( timing.segmentStartFrame( rangeFrValid.segment ))
      )

    }

}

//class NNDataPreloadedSingleSegment( data: Array[DV[Int]], scale: NNDataScale, timing: NNDataTiming,
////                    xBits: Int,
////                    absGain: Double,
////                    absOffset: Double,
////                    absUnit: String,
////                    scaleMax: Int,
////                    scaleMin: Int,
////                    //channelNames: Vector[String], // = Vector.tabulate[String](data.length)(i => "no channel name")
//                    segmentStartTs: Long,
//                    sampleRate: Double/*,
//                    layout: NNLayout = NNLayoutNull$$*/
//                    )
//  extends NNDataPreloaded( Array(data),
//                          xBits,
//                          absGain, absOffset, absUnit,
//                          scaleMax, scaleMin, /*channelNames,*/ Array[Long](segmentStartTs),
//                          Array[Int](data.rows),
//                          sampleRate/*, layout*/){
//  override lazy val segmentCount = 1
//
//}
