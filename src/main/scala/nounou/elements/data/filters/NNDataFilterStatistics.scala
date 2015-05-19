//package nounou.obj.data.filters
//
//import nounou.obj.NNData
//import breeze.math.Complex
//import breeze.linalg.{DenseVector}
//import breeze.signal.{rootMeanSquare, fourierTr}
//import nounou.obj.ranges.FrRange$
//
///**
// * @author ktakagaki
// * //@date 2/16/14.
// */
//class NNDataFilterStatistics( private var _parent: NNData ) extends NNDataFilter( _parent ) {
//
//  override def toString() =  "XDataFilterStatistics: "
//
//  def readTraceAbsFourier( ch: Int, range: FrRange ): DenseVector[Complex] = {
//    val tempTrace = readTraceAbsDV( ch, range )
//    fourierTr( DenseVector( tempTrace.toArray ) )
//  }
//
////  def readTraceAbsFourier( ch: Int, range: RangeFr, segment: Int, fourierRange: Range ): Complex = {
////    val tempTrace = readTraceAbs( ch, range, segment )
////    mean( fourierTr( DenseVector( tempTrace.toArray ), fourierRange ) )
////  }
//
//  def readTraceAbsRMS( ch: Int, range: FrRange ): Double = {
//    val tempTrace = readTraceAbsDV( ch, range )
//    rootMeanSquare( tempTrace )
//  }
//
//  private def rms(vect: Vector[Double]): Double = {
//    var tempsum = 0d
//    var tempcount = 0
//    for( cnt <- 0 until vect.length){
//      tempsum += ( vect(cnt) * vect(cnt) )
//      tempcount += 1
//    }
//    tempsum / tempcount
//  }
//
//}
