package nounous.data.traits

import nounous.data.x.X

/**
 * Created by Kenta on 12/15/13.
 */
trait XAbsolute extends X {

  /**The number (eg 1024) multiplied to original raw data from the recording instrument
    *(usu 14-16 bit) to obtain internal Int representation.
    */
  def xBits = 1024
  /**(xBits:Int).toDouble
    */
  def xBitsD = xBits.toDouble

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
  final def toAbs(data: Int) = data.toDouble * absGain + absOffset
  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
    * absUnit (e.g. "mV")
    */
  final def toAbs(data: Vector[Int]): Vector[Double] = data.map( toAbs _ )
  //ToDo 3: toAbs erasure
  //  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
  //   * absUnit (e.g. "mV")
  //   */
  //  final def toAbs(data: Vector[Vector[Int]]): Vector[Vector[Double]] = data.map( toAbs _ )
  //  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
  //    * absUnit (e.g. "mV")
  //    */
  //  final def toAbs(data: Vector[Vector[Vector[Int]]]): Vector[Vector[Vector[Double]]] = data.map( toAbs _ )

  override def isCompatible(that: X): Boolean = {
    that match {
      case x: XAbsolute => {
        (this.xBits == x.xBits) && (this.absGain == x.absGain) && (this.absOffset == x.absOffset) && (this.absUnit == x.absUnit)
      }
      case _ => false
    }
  }
}

trait XAbsoluteImmutable extends XAbsolute {

  override val xBits = 1024
  /**(xBits:Int).toDouble buffered, since it will be used often.
    */
  override lazy val xBitsD = xBits.toDouble

  override val absGain: Double
  override val absOffset: Double
  override val absUnit: String

}
