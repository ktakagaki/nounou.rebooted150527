package nounou.data.discrete

import nounou.data.X

/**
   * @author ktakagaki
   * @date 1/30/14.
   */
  class XMask() extends X {

  //ToDo 3: implement
  override def isCompatible(that: X): Boolean = false

  override def toString() = "XMask.. not details programmed in yet"
}

  object XMaskNull extends XMask {

    override def toString() = "XMaskNull"
  }
