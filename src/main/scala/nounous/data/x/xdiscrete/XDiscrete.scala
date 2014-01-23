package nounous.data.x.xdiscrete

import nounous.data.traits.XConcatenatable
import nounous.data.x.X

/**Immutable base class to represent discrete data types (ie events, spikes).
  *
 * @author ktakagaki
 */
abstract class XDiscrete extends X with XConcatenatable {

  /** Number of records within this object. */
  val length: Int
  /** Starting timestamp of each record, must be monotonically increasing. */
  val timeStamps: Vector[Long]


  override def isCompatible(that: X): Boolean = {
    var tempRet: Boolean = true

    if(this.getClass == that.getClass ) {
      tempRet=false; println("XDiscrete incompatible, different class type!")
    }
    else {
      val t = that.asInstanceOf[this.type]
      //if(this.xBits != t.xBits)         tempRet=false; println("xdata incompatible, different extraBits!")
    }

    tempRet
  }


}
