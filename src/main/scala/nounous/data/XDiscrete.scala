package nounous.data

import nounous.data.traits.XConcatenatable

//import nounous.data.X

/**
 * Created with IntelliJ IDEA.
 * User: takagaki
 * Date: 23.09.13
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */
abstract class XDiscrete extends X with XConcatenatable {

  override def isCompatible(that: X): Boolean = {
    var tempRet: Boolean = true

    if(this.getClass == that.getClass ) {
      tempRet=false; println("XEvents incompatible, different class type!")
    }
    else {
      val t = that.asInstanceOf[this.type]
      //if(this.xBits != t.xBits)         tempRet=false; println("xdata incompatible, different extraBits!")
    }

    tempRet
  }

  override def :::(that: X): X = {
    if(this.isCompatible(that)){
      val t = that.asInstanceOf[this.type]
      val oriThis = this
      new XEvents(
        //sampling = oriThis.sampling,
        //data =  oriThis.data ++ t.data,
        //channelNames = oriThis.channelNames ++ t.channelNames,
        //channelCount = oriThis.channelCount + t.channelCount
      )
    } else {
      this
    }
  }
}
