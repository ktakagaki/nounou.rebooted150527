package nounous.data

/** Base class for Sources encoding data as Int arrays
  *
  */
abstract class SourceData extends Source {

  /**Sampling rate of data in Hz
    * */
  var sampling: Double
  /**Buffered inverse of sampling: Double
   */
  private lazy val samplingInv = 1/sampling
  /** Starting time of data (frame 0) in seconds.
    * */
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

  override def isCompatible(that: Source): Boolean = {
    var tempRet: Boolean = true
    
    that match {
      case t:SourceData => {
        if(this.xBits != t.xBits) tempRet=false; println("compatibility check failed, different extraBits!")
        if(this.absGain != t.absGain) tempRet=false; println("compatibility check failed, different absoluteGain!")
        if(this.absOffset != t.absOffset) tempRet=false; println("compatibility check failed, different absoluteOffset!")
        if(this.absGain != t.absUnit) tempRet=false; println("compatibility check failed, different absoluteUnit!")
        if(this.length != t.length) tempRet=false; println("compatibility check failed, different length!")
        if(this.start != t.start) tempRet=false; println("compatibility check failed, different start!")
        if(this.sampling != t.sampling) tempRet=false; println("compatibility check failed, different sampling!")
      }
      case _ => tempRet=false; println("compatibility check failed, different class type!")
    }

    tempRet
  }

  
    
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


  //ToDo expand to frame, trace reading
  /**Reads a single data point in internal integer scaling.
   * @param ch channel (pixel, detector) specification
   * @param fr frame specification
   */
  def readData(ch: Int, fr: Int): Int
  /**Reads a single data trace in internal integer scaling (for one detector),
   * and should return a defensive clone.
   * @param ch channel (pixel, detector) specification
   */
  def readDataTrace(ch: Int): Array[Int] = {
    val res = new Array[Int](length)
    for(fr <- 0 until length) res(fr) = readData(ch, fr)
    res
  }

}