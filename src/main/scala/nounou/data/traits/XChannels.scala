package nounou.data.traits

/**This trait of XData objects encapsulates the channel names and count information for
  * electrophysiological and imaging recordings. Channel names/count may be immutable
  * for XDataFilter objects which conduct binning
  *
  * Created by Kenta on 12/15/13.
 */
trait XChannels {

  /**Get the name of a given channel.*/
  def channelName(channel: Int): String
  def channelCount: Int

  /** Is this channel valid?
    */
  final def isValidChannel(channel: Int) = (0 <= channel && channel < channelCount)

}