package nounou.elements.traits

import breeze.linalg.DenseVector
import nounou.elements.NNElement

/**
 * Created by ktakagaki on 15/03/12.
 */
trait NNDataScaleElement extends NNElement {

  private var _scale: NNDataScale = null //  NNDataScale.raw

  /**'''[NNDataScaleElement]''' Alias for [[nounou.elements.traits.NNDataScaleElement.getScale]].*/
  final def scale(): NNDataScale = getScale()
  /**'''[NNDataScaleElement]''' Get physical scaling information for data.
    * This is not made final, because it will be overriden by some filters which pass through
    * upstream scale information.
    */
  def getScale(): NNDataScale = {
    if( _scale == null ) throw loggerError(
      s"Cannot use timing-related functions in ${this.getClass.getCanonicalName} without first calling setTiming()")
    else _scale
  }
  /**'''[NNDataScaleElement]''' Set physical scaling information for data.*/
  def setScale(scale: NNDataScale) = {
    _scale = scale

    //ToDo 2: child change hierarchy in NNElement
    logger.trace("child hierarchy update has not been implemented yet!")
  }

  override def isCompatible(x: NNElement) = x match {
    case x: NNDataScaleElement => x.getScale().isCompatible(this.getScale())
    case _ => false
  }

//  /** See redirected implementation in [[nounou.elements.traits.NNDataScale]]
//    */
//  final def convertIntToAbsolute(data: Int): Double = scale().convertIntToAbsolute(data)
//  /** See redirected implementation in [[nounou.elements.traits.NNDataScale]]
//    */
//  final def convertIntToAbsolute(data: DenseVector[Int]): DenseVector[Double] = scale().convertIntToAbsolute(data)
//  /** See redirected implementation in [[nounou.elements.traits.NNDataScale]]
//    */
//  final def convertIntToAbsolute(data: Array[Int]): Array[Double] = scale().convertIntToAbsolute(data)
//
//
//  /** See redirected implementation in [[nounou.elements.traits.NNDataScale]]
//    */
//  final def convertAbsoluteToInt(dataAbs: Double): Int = scale().convertAbsoluteToInt(dataAbs)
//  /** See redirected implementation in [[nounou.elements.traits.NNDataScale]]
//    */
//  final def convertAbsoluteToInt(dataAbs: DenseVector[Double]): DenseVector[Int] = scale().convertAbsoluteToInt(dataAbs)
//  /** See redirected implementation in [[nounou.elements.traits.NNDataScale]]
//    */
//  final def convertAbsoluteToInt(dataAbs: Array[Double]): Array[Int]= scale().convertAbsoluteToInt(dataAbs)
//
//  override def isCompatible(that: NNElement): Boolean = _scale.isCompatible(that)


}
