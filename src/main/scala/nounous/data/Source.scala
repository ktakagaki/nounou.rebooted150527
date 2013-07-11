package nounous.data

abstract class Source {

  //ToDo consider add: pt info, rec info, rec start time/date, etc

  /** Name of selected channel.*/
  def channelName(channel : Int) : String = _channelNames(channel)
  protected[Source] var _channelNames : Array[String]

  /** Channel count.*/
  val channelCount : Int

  def isCompatible(that: Source): Boolean 

}