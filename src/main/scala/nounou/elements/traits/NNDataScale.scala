package nounou.elements.traits

import breeze.linalg.DenseVector
import nounou.elements.NNElement


/**This trait of XData and XDataChannel objects encapsulates scaling and unit information for
 * electrophysiological and imaging recordings.
  *
 * Created by Kenta on 12/15/13.
 */
class NNDataScale(
                   /**The minimum extent down to which the data runs*/
                   val minValue: Int,
                   /**The maximum extent up to which the data runs */
                   val maxValue: Int,
                   /**Used to calculate the absolute value (mV, etc) based on internal representation.<p>
                     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(absolute value)=(internal value)*dataAbsoluteGain + dataAbsoluteOffset
                     * absoluteGain must take into account the extra bits used to pad Int values.
                     */
                   val absGain: Double,
                   /**Used to calculate the absolute value (mV, etc) based on internal representation.<p>
                     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(absolute value)=(internal value)*dataAbsoluteGain + dataAbsoluteOffset
                     */
                   val absOffset: Double,
                   /**The name of the absolute units, as a String (eg mv).
                     */
                   val absUnit: String,
                   /**The number (eg 1024) multiplied to original raw data from the recording instrument
                     *(usu 14-16 bit) to obtain internal Int representation.
                     */
                   val xBits: Int = 1024) extends NNElement {

  /**(xBits:Int).toDouble
    */
  final lazy val xBitsD = xBits.toDouble


  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
    * absUnit (e.g. "mV")
    */
  final def convertIntToAbsolute(data: Int) = data.toDouble * absGain + absOffset
  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
    * absUnit (e.g. "mV")
    */
  final def convertIntToAbsolute(data: DenseVector[Int]): DenseVector[Double] = data.map( convertIntToAbsolute _ )
  final def convertIntToAbsolute(data: Array[Int]): Array[Double] = convertIntToAbsolute(DenseVector(data)).toArray
  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
    * absUnit (e.g. "mV")
    */
  final def convertAbsoluteToInt(dataAbs: Double) = ((dataAbs - absOffset) / absGain).toInt //ToDo 4: change to multiply?
  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
    * absUnit (e.g. "mV")
    */
  final def convertAbsoluteToInt(dataAbs: DenseVector[Double]): DenseVector[Int] = dataAbs.map( convertAbsoluteToInt _ )
  final def convertAbsoluteToInt(dataAbs: Array[Double]): Array[Int] = dataAbs.map( convertAbsoluteToInt _ )

  override def isCompatible(that: NNElement): Boolean = {
    that match {
      case x: NNDataScale => {
        (this.xBits == x.xBits) && (this.absGain == x.absGain) && (this.absOffset == x.absOffset) && (this.absUnit == x.absUnit) &&
          (this.maxValue == x.maxValue) && (this.minValue == x.minValue)
      }
      case _ => false
    }
  }

}

object NNDataScale {
  val raw: NNDataScale = new NNDataScale( Int.MinValue, Int.MaxValue, 1d, 0d, "Raw scaling")
  def apply(minValue: Int, maxValue: Int, absGain: Double, absOffset: Double,  absUnit: String): NNDataScale =
    new NNDataScale( minValue, maxValue, absGain, absOffset, absUnit)
}