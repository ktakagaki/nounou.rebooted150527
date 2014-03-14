package nounou.data.traits

import scala.Vector

/**This trait of XData objects encapsulates the channel names and count information for
  * electrophysiological and imaging recordings.
  *
  * Created by Kenta on 12/15/13.
 */
trait XChannels {

  /**Get the name of a given channel.*/
  def channelNames: Vector[String]
  def channelNamesA = channelNames.toArray
  /**Get number of channels.*/
  def channelCount = channelNames.length

  /** Is this channel valid?
    */
  final def isValidChannel(channel: Int) = (0 <= channel && channel < channelCount)

}

trait XChannelsImmutable extends XChannels {

  //override val channelNames: Vector[String]
  override final lazy val channelCount = channelNames.length

}
