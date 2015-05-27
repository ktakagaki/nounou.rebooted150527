package nounou.elements.traits

import nounou.elements.NNElement

/**
 * Created by ktakagaki on 15/03/12.
 */
trait NNDataTimingElement extends NNElement {

  private var _timing: NNDataTiming = null

  /**  '''[NNDataTimingElement]''' Alias for [[nounou.elements.traits.NNDataTimingElement.getTiming]].*/
  final def timing(): NNDataTiming = getTiming()
  /**'''[NNDataTimingElement]''' */
  def getTiming(): NNDataTiming = {
    if( _timing == null ) throw loggerError(
      s"Cannot use timing-related functions in ${this.getClass.getCanonicalName} without first calling setTiming()")
    else _timing
  }
  /**'''[NNDataTimingElement]''' */
  def setTiming(timing: NNDataTiming) = {
    _timing= timing

    //ToDo 2: child change hierarchy in NNElement
    logger.trace("child hierarchy update has not been implemented yet!")
  }

  override def isCompatible(x: NNElement) = x match {
    case x: NNDataTimingElement => x.getTiming().isCompatible(this.getTiming())
    case _ => false
  }


}
