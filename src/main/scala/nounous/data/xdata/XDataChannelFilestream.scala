package nounous.data.xdata

import nounous.util._
import nounous.data.Span
import java.io.{DataInput, File}
import breeze.io.RandomAccessFileLE
import nounous.data.traits.XAbsoluteImmutable

/**
 * Created by Kenta on 12/15/13.
 */
abstract class XDataChannelFilestream extends XDataChannel with XAbsoluteImmutable {

  val fileHandle: DataInput
  override val absGain: Double
  override val absOffset: Double
  override val absUnit: String
  override val segments: Int
  override val length: Vector[Int]
  override val startTimestamp: Vector[Long]
  override val sampleRate: Double
  override val channelName: String

  def readPointImpl(segment: Int, frame: Int): Int
  override def readTraceImpl(segment: Int) = readTraceImpl(segment, Span.All)
  override def readTraceImpl(segment: Int, span:Span): Vector[Int]

}
