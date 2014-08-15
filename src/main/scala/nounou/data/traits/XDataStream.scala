package nounou.data.traits

import breeze.linalg.DenseVector
import nounou.data.XData
import nounou.data.filters.XDataFilter
import nounou.data.ranges.RangeFrSpecifier

/**This trait encapsulates the ability to read and process data in time chunks in parallel.
  * If an [[XData]] object implements this trait, the standard "readTrace" functions
  * will be overridden to read in chunks, and use [[scala.concurrent.Future]] to
  * parallelize the computation.
  *
  * @author ktakagaki
  * @date 08/15/2014.
  */
trait XDataFilterParallel extends XDataFilter {

  def chunkSize: Int
  def lastChunk: Int

  override def readTraceImpl(channel: Int, range: Range.Inclusive): DenseVector[Int] = ???

}

/**This trait further overrides [[XDataFilterParallel]] to anticipate the next
  * chunk of data when consecutive ranges are called in consecutive requests to
  * readTrace.
 *
 */
trait XDataFilterParallelAnticipate extends XDataFilterParallel {

  override def readTraceImpl(channel: Int, range: Range.Inclusive): DenseVector[Int] = ???

}