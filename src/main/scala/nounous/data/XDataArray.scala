package nounous.data

//import nounous.data.X
//import nounous.data.XData

/**XData class with internal representation as data array
 * Created with IntelliJ IDEA.
 * User: takagaki
 * Date: 13.09.13
 * Time: 18:50
 * To change this template use File | Settings | File Templates.
 */
 class XDataArray (
      val data: Vector[Vector[Vector[Int]]],
      override val startTimestamp: Vector[Long],
      override val sampleRate: Double,
      override val channelName: Vector[String],
      override val xBits: Int,
      override val absGain: Double,
      override val absOffset: Double,
      override val absUnit: String                ) extends XData {


  //segments, length
  override lazy val length: Vector[Int] = data(0).map(_.length)
  override lazy val segments = data(0).length


  //timestamps
  override lazy val endTimestamp = ( for(seg <- 0 until segments) yield startTimestamp(seg) + ((length(seg)-1)*timestampsPerFrame).toLong ).toVector

  //sampling rate information
  override lazy val sampleInterval = 1.0/sampleRate
  override lazy val timestampsPerFrame = sampleInterval * 1000000D
  override lazy val framesPerTimestamp = 1D/timestampsPerFrame

  //channel information
  require(channelName.length == data.length,
    "number of _channelName elements " + channelName.length + " does not match data.length " + data.length + "!")
  override lazy val channelCount = channelName.length

  //reading
  override def readPointImpl(segment: Int, channel: Int, frame: Int) = data(segment)(channel)(frame)
  override def readTraceImpl(segment: Int, channel: Int) = data(segment)(channel)

  override def :::(that: X): X = {
    that match {
      case t: XDataArray => {
        if(this.isCompatible(that)){
          val oriThis = this
          new XDataArray(
            oriThis.data ++ t.data,
            oriThis.startTimestamp ++ t.startTimestamp,
            oriThis.sampleRate,
            oriThis.channelName ++ t.channelName,
            oriThis.xBits, oriThis.absGain, oriThis.absOffset, oriThis.absUnit )

        } else {
          throw new IllegalArgumentException("the two XDataArray types are not compatible, and cannot be concatenated.")
        }
      }
      case _ => throw new IllegalArgumentException("the two X types are not compatible, and cannot be concatenated.")
    }
  }



}
