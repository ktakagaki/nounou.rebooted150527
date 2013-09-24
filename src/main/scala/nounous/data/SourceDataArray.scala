package data

import nounous.data.Source
import nounous.data.SourceData

/**SourceData class with internal representation as data array
 * Created with IntelliJ IDEA.
 * User: takagaki
 * Date: 13.09.13
 * Time: 18:50
 * To change this template use File | Settings | File Templates.
 */
abstract class SourceDataArray extends SourceData{

  val data: Array[Array[Int]]

  override def readData(ch: Int, fr: Int) = data(ch)(fr)
  override def :::(that: SourceDataArray){
    if(isCompatible(that)){
      val channelNames = this.channelNames ::: that.channelNames
      val data: Array[Array[Int]] = this.data ::: that.data
    } else {
      println(":::, Sources Not compatible!")
      this
    }
  }

  override def isCompatible(that: Source): Boolean = {
    super.isCompatible(that) &&
    (that match {
    case t: SourceDataArray => true
    case _ => {
      println("Second element to combine is not a SourceDataArray!")
      false
      }
    })
  }

}
