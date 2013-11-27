package nounous.data

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
  val start : Double

  /**
   * Timestamp of frame (in seconds).
   */
  def frameToTime(frame:Int): Double = frame.toDouble*samplingInv + start
  /**Closest frame to the given timestamp.
    * @param time in seconds
    * */
  def timeToFrame(time: Double): Int = scala.math.round( (time - start) * sampling ).toInt // /1000D
  /**
   * Total number of frames contained.
   */
  val length: Int


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


  /**Reads a single data point in internal integer scaling.
   * @param ch channel (pixel, detector) specification
   * @param fr frame specification
   */
  def readPoint(ch: Int, fr: Int): Int
  final def readPointAbs(ch: Int, fr: Int) = toAbs(readPoint(ch,fr))

  //readTrace, readTraceAbs, readTraceImpl
  /**Reads a single data trace in internal integer scaling (for one detector),
   * and should return a defensive clone.
   * @param ch channel (pixel, detector) specification
   */
  def readTrace(ch: Int): Array[Int] = {
    val res = new Array[Int](length)
    for(fr <- 0 until length) res(fr) = readPoint(ch, fr)
    res
  }
  final def readTraceAbs(ch: Int) = toAbs(readTrace(ch))
  def readTrace(ch: Int, range: (Int, Int) ): Array[Int] = {
    require( rangeCheck(range) )
    readTraceImpl(ch, range)
  }
  def readTrace(ch: Int, range: (Int, Int), skip: Int ): Array[Int] = {
    require( rangeCheck(range) )
    readTraceImpl(ch, range, skip)
  }
  final def readTraceAbs(ch: Int, range: (Int, Int)) = toAbs(readTrace(ch, range))
  final def readTraceAbs(ch: Int, range: (Int, Int), skip: Int) = toAbs(readTrace(ch, range, skip))
  def rangeCheck(range: (Int, Int)) = (0 <= range._1 && range._1 <= range._2 && range._2 <= length)

  private def readTraceImpl(ch: Int, range: (Int, Int) ): Array[Int] = {
    val res = new Array[Int](range._2 - range._1 +1)
    for(fr <- range._1 until range._2 + 1) res(fr - range._1) = readPoint(ch, fr)
    res
  }
  private def readTraceImpl(ch: Int, range: (Int, Int), skip: Int ): Array[Int] = {
    val res = new Array[Int]((range._2 - range._1)/skip + 1)
    for(fr <- range._1 until range._2 + 1 by skip) res(fr - range._1) = readPoint(ch, fr)
    res
  }

  def readFrame(fr: Int): Array[Int] = {
    val res = new Array[Int](channelCount)
    for(ch <- 0 until channelCount) res(ch) = readPoint(ch, fr)
    res
  }

  def read(): Array[Array[Int]] = {
    val res = new Array[Array[Int]](channelCount)
    for(ch <- 0 until channelCount) res(ch) = readTrace(ch)
    res
  }

  override def isCompatible(that: X): Boolean = {
    var tempRet: Boolean = true

    if(this.getClass == that.getClass ) {
      tempRet=false; println("XData incompatible, different class type!")
    }
    else {
      val t = that.asInstanceOf[this.type]
      if(this.xBits != t.xBits)         tempRet=false; println("XData incompatible, different extraBits!")
      if(this.length != t.length)       tempRet=false; println("XData incompatible, different length!")
      if(this.start != t.start)         tempRet=false; println("XData incompatible, different start!")
      if(this.sampling != t.sampling)   tempRet=false; println("XData incompatible, different sampling!")
      if(this.absGain != t.absGain)     tempRet=false; println("XData incompatible, different absoluteGain!")
      if(this.absOffset != t.absOffset) tempRet=false; println("XData incompatible, different absoluteOffset!")
      if(this.absUnit != t.absUnit)     tempRet=false; println("XData incompatible, different absoluteUnit!")
    }

    tempRet
  }

}
