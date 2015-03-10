//package nounou.analysis.units
//
//import breeze.linalg.{DenseVector, randomInt}
//import breeze.stats.median
//import breeze.numerics._
//import nounou.NN._
//import _root_.nounou.obj.ranges.SampleRangeSpecifier
//import nounou.util.LoggingExt
////import nounou.{OptSegment, Opt, OptNull}
//import nounou.obj.NNData
////import nounou.data.ranges.{FrRange$, SampleRangeSpecifier}
//
///**
// * Created by Kenta on 04.11.2014.
// */
//object TraceMedianSD extends LoggingExt {
//
//  def apply(data: NNData, channel: Int, frameRange: SampleRangeSpecifier): Int =
//    apply(data: NNData, channel: Int, frameRange: SampleRangeSpecifier, 6400)
//
//  def apply(data: NNData, channel: Int, frameRange: SampleRangeSpecifier, sampleLength: Int ): Int ={
//
//    var sampleLength = 6400
//    // <editor-fold defaultstate="collapsed" desc=" Handle options ">
//
////    for( opt <- opts ) opt match {
////      case OptTraceSDReadLengthFr( fr ) => sampleLength = fr
////      case _ => {}
////    }
//
//    if(sampleLength < 3200) throw loggerError("optTraceSDReadLength must be 3200 or larger!")
//
//    val vfr = frameRange.getSampleRangeValid(data)
//    if( vfr.length <= sampleLength ){
//      //if the data range is short enough, take the median estimate from the whole data range
//      (median( abs(  data.readTraceDV(channel, frameRange)  ) ).toDouble / 0.6745).intValue
//    } else {
//      //if the data range is long, take random samples for cutoff SD estimate
//      val seg = frameRange.getRealSegment(data)
//      val sampleSeg =  sampleLength/100
//      val samp = randomInt( 100, (0, data.segmentLength( seg )-1-sampleSeg ) ).toArray.sorted.map(
//        (p: Int) => median(abs(data.readTraceDV( channel, SampleRange(p, p + sampleSeg - 1, 1, seg)) ))
//      )
//      (median( DenseVector(samp) ).toDouble / 0.6745).intValue
//    }
//  }
//
//}
