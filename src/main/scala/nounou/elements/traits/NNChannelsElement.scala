package nounou.elements.traits

import nounou.elements.NNElement
import nounou.util.LoggingExt

/**This subclass of NNElement objects encapsulates the channel count information for
  * electrophysiological and imaging data. Envisioned uses are for [[nounou.elements.data.NNData]],
  * [[nounou.elements.layouts.NNDataLayout]].
  * Channel names/count are intentionally mutable for [[nounou.elements.data.filters.NNDataFilter]]
  * objects which conduct binning and therefore may change dynamically.
 */
trait NNChannelsElement extends NNElement {

  /** '''[NNChannelsElement]''' Number of data channels in this layout.
    */
  def getChannelCount: Int

  /** '''[NNChannelsElement]''' Alias for [[nounou.elements.traits.NNChannelsElement!.getChannelCount]].
    */
  final def channelCount(): Int = getChannelCount

  /** '''[NNChannelsElement]''' Get the name of a given channel.
    * Throws error if channel out of range and not valid*/
  final def channelName(channel: Int): String = {
    requireValidChannel(channel)
    channelNameImpl(channel)
  }

  /** '''__SHOULD OVERRIDE__''' '''[NNChannelsElement]''' Get the name of a given channel.
    */
  def channelNameImpl(channel: Int): String = s"Class ${this.getClass}, channel #${channel}"

  /** '''[NNChannelsElement]''' Is this channel valid?
    */
  final def isValidChannel(channel: Int) = (0 <= channel && channel < channelCount)
  /** '''[NNChannelsElement]''' Log error if !isValidChannel(channel)
    */
  final def requireValidChannel(channel: Int) =
    loggerRequire(isValidChannel(channel), s"Incorrect channel: ${channel} (channelCount = ${channelCount})")

}