package nounou.elements.traits

import nounou.util.LoggingExt

/**This trait of XData objects encapsulates the channel names and count information for
  * electrophysiological and imaging recordings. Channel names/count may be immutable
  * for XDataFilter objects which conduct binning
  *
  * Created by Kenta on 12/15/13.
 */
trait NNChannels extends LoggingExt {

  /**Get the name of a given channel.
    * Throws error if channel out of range and not valid*/
  final def channelName(channel: Int): String = {
    requireValidChannel(channel)
    channelNameImpl(channel)
  }
  def channelNameImpl(channel: Int): String = s"Class ${this.getClass}, channel #${channel}"
  def channelCount: Int

  /** Is this channel valid?
    */
  final def isValidChannel(channel: Int) = (0 <= channel && channel < channelCount)
  /** Log error if !isValidChannel(channel)
    */
  final def requireValidChannel(channel: Int) =
    loggerRequire(isValidChannel(channel), s"Incorrect channel: ${channel} (channelCount = ${channelCount})")

}