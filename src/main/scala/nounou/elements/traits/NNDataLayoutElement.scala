package nounou.elements.traits

import nounou.elements.NNElement
import nounou.elements.layouts.NNDataLayout

/**
 * Created by ktakagaki on 15/03/12.
 */
trait NNDataLayoutElement extends NNElement {

  private var _layout: NNDataLayout = null

  final def layout(): NNDataLayout = getLayout()
  def getLayout(): NNDataLayout = {
    if( _layout == null ) throw loggerError(
      s"Cannot use timing-related functions in ${this.getClass.getCanonicalName} without first calling setTiming()")
    else _layout
  }
  def setLayout(layout: NNDataLayout) = {
    _layout= layout
    //ToDo 2: child change hierarchy in NNElement
  }

  override def isCompatible(x: NNElement) = x match {
    case x: NNDataLayoutElement => x.getLayout().isCompatible(this.getLayout())
    case _ => false
  }


}
