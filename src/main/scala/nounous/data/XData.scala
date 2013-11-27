package nounous.data

import nounous.util.forJava

/** Base class for Sources encoding data as Int arrays.
  * Each trace of data must share the following variables:
  * sampling, start, length, xBits, absGain, absOffset, absUnit
  *
  */
abstract class XData extends X {

  /**Sampling rate of data in Hz
    * */
  val sampling: Double
  /**Buffered inverse of sampling: Double
   */
  private lazy val samplingInv = 1/sampling

  /** Starting time of data (frame 0) in seconds.
    */
  def start(seg: Int) : Double

  /**
   * Timestamp of frame (in seconds).
   */
  def frameToTime(seg: Int, frame:Int): Double = frame.toDouble*samplingInv + start(seg)
  /**Closest frame to the given timestamp.
    * @param time in seconds
    * */
  def timeToFrame(seg: Int, time: Double): Int = scala.math.round( (time - start(seg)) * sampling ).toInt // /1000D
  /**
   * Total number of frames contained.
   */
  def length(seg: Int): Int


  def channelName(ch: Int): String = channelNames(ch)
  val channelNames: Array[String]
  val channelCount: Int

  /**The number (eg 1024) multiplied to original raw data from the recording instrument
   *(usu 14-16 bit) to obtain internal Int representation.
   */
  val xBits : Int
  /**(xBits:Int).toDouble buffered, since it will be used often.
   */
  lazy val xBitsD: Double = xBits.toDouble

  
  /**Used to calculate the absolute value (mV, etc) based on internal representation.<p>
   * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(absolute value)=(internal value)*dataAbsoluteGain + dataAbsoluteOffset
   * absoluteGain must take into account the extra bits used to pad Int values. 
   */
  val absGain: Double
  
  /**Used to calculate the absolute value (mV, etc) based on internal representation.<p>
   * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(absolute value)=(internal value)*dataAbsoluteGain + dataAbsoluteOffset
   */
  val absOffset: Double
  
  /**The name of the absolute units, as a String (eg mv).
   */
  val absUnit: String


  
    
  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
   * absUnit (e.g. "mV")
   */
  def toAbs(data: Int) = data.toDouble * absGain + absOffset
  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
   * absUnit (e.g. "mV")
   */
  def toAbs(data: Array[Int]): Array[Double] = data.map(toAbs _ )
  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
   * absUnit (e.g. "mV")
   */
  def toAbs(data: Array[Array[Int]]): Array[Array[Double]] = data.map(toAbs _ )


  //reading a point
  final def read(seg: Int, ch: Int, fr: Int): Int = readPointImpl(seg, ch, fr)
  final def readAbs(seg: Int, ch: Int, fr: Int): Double = toAbs(readPointImpl(seg, ch, fr))
  @deprecated
  def readPoint(seg: Int, ch: Int, fr: Int) = readPointImpl(seg, ch, fr)
  def readPointImpl(seg: Int, ch: Int, fr: Int): Int

  /**Reads a single data point in internal integer scaling.
   * @param ch channel (pixel, detector) specification
   * @param fr frame specification
   */
  @deprecated
  final def readPointAbs(seg: Int, ch: Int, fr: Int) = toAbs(readPointImpl(seg, ch, fr))

  //reading a trace
  final def read(seg: Int, ch: Int, span: Span = Span.All): Array[Int] = {
    span match {
      case Span.All => readTraceImpl(seg, ch)
      case _ => readTraceImpl(seg, ch, span)
    }
  }
  final def readAbs(seg: Int, ch: Int, span: Span = Span.All): Array[Double] = toAbs(read(seg, ch, span))
  /**Reads a single data trace in internal integer scaling (for one detector),
   * and should return a defensive clone.
   * @param ch channel (pixel, detector) specification
   */
  def readTraceImpl(seg: Int, ch: Int): Array[Int] = {
    val res = new Array[Int](length(seg))
    forJava(0, res.length, 1, (c: Int) => (res(c) = readPointImpl(seg, ch, c)))
    res
  }
  def readTraceImpl(seg: Int, ch: Int, span:Span): Array[Int] = {
    val range = span.getRange( length(seg) )
    val res = new Array[Int]( range.length )
    forJava(range.start, range.end, range.step, (c: Int) => (res(c) = readPointImpl(seg, ch, c)))
    res
  }
  @deprecated
  final def readTraceAbs(seg: Int, ch: Int) = toAbs( readTraceImpl(seg, ch))
  @deprecated
  final def readTrace(seg: Int, ch: Int, range: (Int, Int), skip: Int = 1 ): Array[Int] = {
    readTraceImpl(seg, ch, new Span(range._1, range._2, skip))
  }
  final def readTraceAbs(seg: Int, ch: Int, range: (Int, Int), skip: Int) = toAbs(readTrace(seg, ch, range, skip))
//  def rangeCheck(range: (Int, Int)) = (0 <= range._1 && range._1 <= range._2 && range._2 <= length)
//  private def readTraceImpl(ch: Int, range: (Int, Int) ): Array[Int] = {
//    val res = new Array[Int](range._2 - range._1 +1)
//    for(fr <- range._1 until range._2 + 1) res(fr - range._1) = readPoint(ch, fr)
//    res
//  }
//  private def readTraceImpl(ch: Int, range: (Int, Int), skip: Int ): Array[Int] = {
//    val res = new Array[Int]((range._2 - range._1)/skip + 1)
//    for(fr <- range._1 until range._2 + 1 by skip) res(fr - range._1) = readPoint(ch, fr)
//    res
//  }

  //reading a frame
  final def read(seg: Int, chSpan: Span, fr: Int): Array[Int] = {
    chSpan match {
      case Span.All => readFrameImpl(seg, fr)
      case _ => readFrameImpl(seg, chSpan, fr)
    }
  }
  def readFrameImpl(seg: Int, fr: Int): Array[Int] = {
    val res = new Array[Int](channelCount)
    forJava(0, channelCount, 1, (ch: Int) => res(ch) = readPointImpl(seg, ch, fr))
    res
  }
  def readFrameImpl(seg: Int, chSpan: Span = Span.All, fr: Int): Array[Int] = {
    val range = chSpan.getRange( channelCount )
    val res = new Array[Int]( range.length)
    forJava(range.start, range.end, range.step, (ch: Int) => res(ch) = readPointImpl(seg, ch, fr))
    res
  }

//  def readFrame(fr: Int): Array[Int] = {
//    val res = new Array[Int](channelCount)
//    for(ch <- 0 until channelCount) res(ch) = readPoint(ch, fr)
//    res
//  }


  override def isCompatible(that: X): Boolean = {
    var tempRet: Boolean = true

    if(this.getClass == that.getClass ) {
      tempRet=false; println("XData incompatible, different class type!")
    }
    else {
      val t = that.asInstanceOf[this.type]
      if(this.xBits != t.xBits)         tempRet=false; println("XData incompatible, different extraBits!")
      //if(this.length != t.length)       tempRet=false; println("XData incompatible, different length!")
      //if(this.start != t.start)         tempRet=false; println("XData incompatible, different start!")
      if(this.sampling != t.sampling)   tempRet=false; println("XData incompatible, different sampling!")
      if(this.absGain != t.absGain)     tempRet=false; println("XData incompatible, different absoluteGain!")
      if(this.absOffset != t.absOffset) tempRet=false; println("XData incompatible, different absoluteOffset!")
      if(this.absUnit != t.absUnit)     tempRet=false; println("XData incompatible, different absoluteUnit!")
    }

    tempRet
  }

}
