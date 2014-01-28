package nounous.data.xdata

import scala.Vector

/**
 * Created by Kenta on 12/15/13.
 */
trait XChannels {

  /**Get the name of a given channel.*/
  def channelName: Vector[String]
  def channelCount = channelName.length

  /** Is this channel valid?
    */
  final def isValidChannel(channel: Int) = (0 < channel && channel < channelCount)

}

trait XChannelsImmutable extends XChannels {

  /**Get the name of a given channel.*/
  override val channelName: Vector[String]
  override final lazy val channelCount = channelName.length

}
