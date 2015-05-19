//package nounou.elements.data.traits
//
//import breeze.linalg.DenseVector
//
///**This trait encapsulates the ability to read and process data in time chunks in parallel.
//  * If an [[nounou.obj.data.NNData]] object implements this trait, the standard "readTrace" functions
//  * will be overridden to read in chunks, and use [[scala.concurrent.Future]] to
//  * parallelize the computation.
//  *
//  * @author ktakagaki
//  * //@date 08/15/2014.
//  */
//trait NNDataFilterParallel extends NNDataFilter {
//
//  def chunkSize: Int
//  def lastChunk: Int
//
//  override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): DenseVector[Int] = ???
//
//}
//
///**This trait further overrides [[NNDataFilterParallel]] to anticipate the next
//  * chunk of data when consecutive ranges are called in consecutive requests to
//  * readTrace.
//*
//*/
//trait NNDataFilterParallelAnticipate extends NNDataFilterParallel {
//
//  override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): DenseVector[Int] = ???
//
//}