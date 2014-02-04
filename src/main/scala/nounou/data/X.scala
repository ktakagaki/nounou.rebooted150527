package nounou.data

import scala.collection.immutable.Vector

/** Base class for all sources
  *
  */
abstract class X {

  //ToDo consider add: pt info, rec info, rec start time/date, etc
  //ToDo: toString based on name, comments, etc.



//  /** Name of selected channel.*/
//  def channelNames(channel : Int) : String = _channelNames(channel)
//  protected[X] var _channelNames : Array[String]
//
//  /** Channel count.*/
//  val channelCount : Int


  def isCompatible(that: X): Boolean

  protected def vectZeros(length: Int) = Vector.tabulate(length)( (i: Int) => 0 )


}