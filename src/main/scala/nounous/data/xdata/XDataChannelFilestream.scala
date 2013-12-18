package nounous.data.xdata

import nounous.util._
import nounous.data.Span
import java.io.{DataInput, File}
import breeze.io.RandomAccessFileLE

/**
 * Created by Kenta on 12/15/13.
 */
abstract class XDataChannelFilestream extends XDataChannel {

  val fileHandle: DataInput
  def dataByteLocation(frame: Int): Long

  def readPointImpl(segment: Int, frame: Int): Int
  override def readTraceImpl(segment: Int) = readTraceImpl(segment, Span.All)
  override def readTraceImpl(segment: Int, span:Span): Vector[Int]

}
