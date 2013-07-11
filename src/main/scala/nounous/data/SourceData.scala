package nounous.data

/** Base class for Sources encoding data as Int arrays
  *
  */
abstract class SourceData extends Source {

//ToDo: toString based on name, comments, etc.


  /**Sampling rate of data in Hz*/
  def samplingRate: Double = _samplingRate
  protected var _samplingRate : Double// = 0D

  /** Starting time of data (frame 0) in seconds.*/
  def startTime : Double = _startTime
  protected var _startTime : Double// = 0D

  /**Timestamp of frame index (in seconds).*/
  def frameToTime(frame:Int): Double = frame.toDouble*samplingRateInv + startTime //*1000D
  private lazy val samplingRateInv = 1/samplingRate

  /**Closest frame to the given timestamp (in seconds).*/
  def timeToFrame(time: Double): Int = scala.math.round( (time - startTime) * samplingRate ).toInt // /1000D


//  def layout: DataLayout
  /**The actual data*/
  protected var data: Array[Array[Int]] = null
  /**Number of frames contained.*/
  def length = data(0).length

  /**The number (eg 1024) multiplied to original raw data from the recording instrument
   *(usu 14-16 bit) to obtain internal Int representation. */
  def extraBits : Int = _extraBits
  protected[SourceData] var _extraBits: Int
  
  /**Double of extraBits:Int buffered, since it will be used often.*/
  def extraBitsD: Double = _extraBitsD
  protected[SourceData] var _extraBitsD: Double

  
  /**Used to calculate the absolute value (mV, etc) based on internal representation.<p>
   * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(absolute value)=(internal value)*dataAbsoluteGain + dataAbsoluteOffset
   * absoluteGain must take into account the extra bits used to pad Int values. 
   * */
  def absoluteGain: Double// = _absoluteGain
  //protected[SourceData] var _absoluteGain : Double
  
  /**Used to calculate the absolute value (mV, etc) based on internal representation.<p>
   * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(absolute value)=(internal value)*dataAbsoluteGain + dataAbsoluteOffset */
  def absoluteOffset: Double = _absoluteOffset
  protected[SourceData] var _absoluteOffset: Double
  
  /**The name of the absolute units, as a String (eg mv).*/
  def absoluteUnit: String// = _absoluteUnit
  //protected[SourceData] var _absoluteUnit: String

  override def isCompatible(that: Source): Boolean = {
    var tempret: Boolean = true
    
    that match {
      case t:SourceData => {
        if(this.extraBits != t.extraBits) tempret=false; println("compatibility check failed, different extraBits!")
        if(this.absoluteGain != t.absoluteGain) tempret=false; println("compatibility check failed, different absoluteGain!")
        if(this.absoluteGain != t.absoluteOffset) tempret=false; println("compatibility check failed, different absoluteOffset!")
        if(this.absoluteGain != t.absoluteUnit) tempret=false; println("compatibility check failed, different absoluteUnit!")
        if(this.absoluteGain != t.length) tempret=false; println("compatibility check failed, different length!")
      }
      case _ => tempret=false; println("compatibility check failed, different class type!")
    }
    
    tempret
  }
  
    
  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
   * absoluteUnit (e.g. "mV")
   */
  def toAbsolute(data: Int) = data.toDouble * absoluteGain + absoluteOffset
  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
   * absoluteUnit (e.g. "mV")
   */
  def toAbsolute(data: Array[Int]): Array[Double] = data.map(toAbsolute _ )
  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
   * absoluteUnit (e.g. "mV")
   */
  def toAbsolute(data: Array[Array[Int]]): Array[Array[Double]] = data.map(toAbsolute _ )


  //ToDo expand to frame, trace reading
  /**Reads a single data point in internal integer scaling.
   * @param det detector (pixel, channel) specification
   * @param fr frame specification
   */
  def readData(det: Int, fr: Int): Int = {
    if (det == 0) return data(det)(fr)
    else return 0
  }
   
    
}






//    /**Reads a single data trace in internal integer scaling (for one detector),
//     * and should return a defensive clone.
//     * @param det detector (pixel, channel) specification
//     */
//    def readDataTrace(det: Int): Array[Int] = {
//      val res = new Array[Int](dataRange.length)
//      for(fr <- dataRange) res(fr) = readDataPoint(det, fr)
//      res
//    }
//
//    /**Reads a single data frame (for one time frame); all values masked by NNJDataMask
//     * will be returned as zero values.
//     * @param frame frame specification
//     * @return data frame
//     */
//    def readDataFrame(frame: Int): Array[Int] = {
//      val result = new Array[Int](dataMask.size)
//      for(det <- dataMask) result(det) = readDataPoint(det, frame)
//      result
//    }
//    /**Data mask (region of interest) for this data.
//     * This allows downstream elements to know which detectors to deal with.
//     * @return data mask for this data
//     */
//ToDo    var dataMask: Set[Int]



//    /**Range of interest that is currently set for the data, in frames.*/
//    var dataRange: Range

