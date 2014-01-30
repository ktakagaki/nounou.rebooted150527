package nounou.data.traits

import scala.Vector

/**
 * Created by Kenta on 12/15/13.
 */
trait XChannels {

  /**Get the name of a given channel.*/
  def channelNames: Vector[String]
  /**Get number of channels.*/
  def channelCount = channelNames.length

  /** Is this channel valid?
    */
  final def isValidChannel(channel: Int) = (0 <= channel && channel < channelCount)

}

trait XChannelsImmutable extends XChannels {

  override val channelNames: Vector[String]
  override final lazy val channelCount = channelNames.length

}
