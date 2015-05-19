package nounou.analysis.continuous

import breeze.linalg.{DenseVector, randomInt}
import breeze.stats.median
import breeze.numerics._
import nounou.NN._
import nounou.elements.data.NNData
import nounou.elements.ranges.SampleRangeSpecifier
import nounou.util.LoggingExt

/**Calculates the median-based standard deviation estimate for input data.
 * This estimate is less biased by transient extreme values such as spiking events in neurons.
 */
object MedianSD extends LoggingExt {

  def apply(data: NNData, channel: Int, frameRange: SampleRangeSpecifier, sampleLength: Int ): Int ={

    if(sampleLength < 3200) throw loggerError("optTraceSDReadLength must be 3200 or larger!")

    val vfr = frameRange.getSampleRangeValid(data)
    if( vfr.length <= sampleLength ){
      //if the data range is short enough, take the median estimate from the whole data range
      (median( abs(  data.readTraceDV(channel, frameRange)  ) ).toDouble / 0.6745).intValue
    } else {
      //if the data range is long, take random samples for cutoff SD estimate
      val seg = frameRange.getRealSegment(data)
      val sampleSeg =  sampleLength/100
      val samp = randomInt( 100, (0, data.timing.segmentLengths( seg )-1-sampleSeg ) ).toArray.sorted.map(
        (p: Int) => median(abs(data.readTraceDV( channel, SampleRange(p, p + sampleSeg - 1, 1, seg)) ))
      )
      (median( DenseVector(samp) ).toDouble / 0.6745).intValue
    }
  }

  //  def apply(data: NNData, channel: Int, frameRange: SampleRangeSpecifier): Int =
  //    apply(data: NNData, channel: Int, frameRange: SampleRangeSpecifier, 6400)


  //ToDo2: better options handling?
  //    for( opt <- opts ) opt match {
  //      case OptTraceSDReadLengthFr( fr ) => sampleLength = fr
  //      case _ => {}
  //    }

}
