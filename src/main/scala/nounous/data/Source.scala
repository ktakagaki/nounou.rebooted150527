package nounous.data

/** Base class for all sources
  *
  */
abstract class Source {

  //ToDo consider add: file name, pt info, rec info, rec start time/date, etc
  //ToDo: toString based on name, comments, etc.



//  /** Name of selected channel.*/
//  def channelName(channel : Int) : String = _channelNames(channel)
//  protected[Source] var _channelNames : Array[String]
//
//  /** Channel count.*/
//  val channelCount : Int


  def isCompatible(that: Source): Boolean
///  def :::(target: Source): Source

}