package nounou.elements.data.traits

import breeze.linalg.DenseVector
import nounou.elements.NNElement

/**This trait of XData and XDataChannel objects encapsulates scaling and unit information for
 * electrophysiological and imaging recordings.
  *
 * Created by Kenta on 12/15/13.
 */
trait NNDataScale extends NNElement {

  /**The number (eg 1024) multiplied to original raw data from the recording instrument
    *(usu 14-16 bit) to obtain internal Int representation.
    */
  def xBits = 1024
  /**(xBits:Int).toDouble
    */
  def xBitsD = xBits.toDouble
  /**The maximum extent up to which the data runs
    */
  def scaleMax: Int
  /**The maximum extent down to which the data runs
    */
  def scaleMin: Int

  /**Used to calculate the absolute value (mV, etc) based on internal representation.<p>
    * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(absolute value)=(internal value)*dataAbsoluteGain + dataAbsoluteOffset
    * absoluteGain must take into account the extra bits used to pad Int values.
    */
  def absGain: Double

  /**Used to calculate the absolute value (mV, etc) based on internal representation.<p>
    * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(absolute value)=(internal value)*dataAbsoluteGain + dataAbsoluteOffset
    */
  def absOffset: Double

  /**The name of the absolute units, as a String (eg mv).
    */
  def absUnit: String

  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
    * absUnit (e.g. "mV")
    */
  final def convertINTtoABS(data: Int) = data.toDouble * absGain + absOffset
  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
    * absUnit (e.g. "mV")
    */
  final def convertINTtoABS(data: DenseVector[Int]): DenseVector[Double] = data.map( convertINTtoABS _ )
  final def convertINTtoABSA(data: Array[Int]): Array[Double] = convertINTtoABS(DenseVector(data)).toArray
  //ToDo 3: convertINTtoABS erasure
  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
    * absUnit (e.g. "mV")
    */
  final def convertABStoINT(dataAbs: Double) = ((dataAbs - absOffset) / absGain).toInt //ToDo 4: change to multiply?
  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
    * absUnit (e.g. "mV")
    */
  final def convertABStoINT(dataAbs: DenseVector[Double]): DenseVector[Int] = dataAbs.map( convertABStoINT _ )
  final def convertABStoINTA(dataAbs: Array[Double]): Array[Int] = dataAbs.map( convertABStoINT _ )

  override def isCompatible(that: NNElement): Boolean = {
    that match {
      case x: NNDataScale => {
        (this.xBits == x.xBits) && (this.absGain == x.absGain) && (this.absOffset == x.absOffset) && (this.absUnit == x.absUnit) &&
          (this.scaleMax == x.scaleMax) && (this.scaleMin == x.scaleMin)
      }
      case _ => false
    }
  }
}

//trait XAbsoluteImmutable extends XAbsolute {
//
//  override val xBits = 1024
//  /**(xBits:Int).toDouble buffered, since it will be used often.
//    */
//  override lazy val xBitsD = xBits.toDouble
//
//  override val absGain: Double
//  override val absOffset: Double
//  override val absUnit: String
//
//}
