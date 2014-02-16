package nounou.data.filters

import nounou.data.XData
import nounou.FrameRange
import breeze.math.Complex
import breeze.linalg.{mean, DenseVector}
import breeze.signal.fourierTr

/**
 * @author ktakagaki
 * @date 2/16/14.
 */
class XDataFilterFourierTr( override val upstream: XData ) extends XDataFilter( upstream ) {

  def readTraceFourierTr( ch: Int, range: FrameRange, segment: Int ): DenseVector[Complex] = {
    val tempTrace = readTraceAbs( ch, range, segment )
    fourierTr( DenseVector( tempTrace.toArray ) )
  }

  def readTraceFourierTr( ch: Int, range: FrameRange, segment: Int, fourierRange: Range ): Complex = {
    val tempTrace = readTraceAbs( ch, range, segment )
    mean( fourierTr( DenseVector( tempTrace.toArray ), fourierRange ) )
  }

}
