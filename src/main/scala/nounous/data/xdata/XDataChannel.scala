package nounous.data.xdata

import nounous.data.traits.{XAbsoluteImmutable, XFramesImmutable}
import nounous.data.{Span, X}
import nounous.util._
import scala.Vector

/**
 * Created by Kenta on 12/14/13.
 */
abstract class XDataChannel extends X with XFramesImmutable with XAbsoluteImmutable {

  /**MUST OVERRIDE: name of the given channel.*/
  val channelName: String

  //<editor-fold desc="reading a point">

  /** Read a single point from the data, in internal integer scaling, after checking values.
    * Implement via readPointImpl.
    */
  final def readPoint(segment: Int, frame: Int): Int = {
    require(isValidFrame(segment, frame), "Invalid segment/frame: " + (segment, frame).toString)
    readPointImpl(segment, frame)
  }
  //</editor-fold>
  /** MUST OVERRIDE: Read a single point from the data, in internal integer scaling.
    */
  def readPointImpl(segment: Int, frame: Int): Int

  //<editor-fold desc="reading a trace">
  /** Read a single trace from the data, in internal integer scaling.
    */
  final def readTrace(segment: Int): Vector[Int] = {
    readTraceImpl(segment)
  }
  /** Read a single trace (within the span) from the data, in internal integer scaling.
    */
  final def readTrace(segment: Int, span: Span): Vector[Int] = {
    span match {
      case Span.All => readTraceImpl(segment)
      case _ => readTraceImpl(segment, span)
      }
  }



  /** CAN OVERRIDE: Read a single data trace from the data, in internal integer scaling.
    * Should return a defensive clone.
    */
  def readTraceImpl(segment: Int): Vector[Int] = readTraceImpl(segment, Span.All)

  /** CAN OVERRIDE: Read a single data trace from the data, in internal integer scaling.
    * Should return a defensive clone.
    */
  def readTraceImpl(segment: Int, span:Span): Vector[Int] = {
    val range = span.getRange( length(segment) )
    val res = new Array[Int]( range.length )
    forJava(range.start, range.end, range.step, (c: Int) => (res(c) = readPointImpl(segment, c)))
    res.toVector
  }


  override def isCompatible(that: X): Boolean = {
    that match {
      case x: XDataChannel => {
        (super[XFramesImmutable].isCompatible(x) && super[XAbsoluteImmutable].isCompatible(x))
      }
      case _ => false
    }
  }

}
