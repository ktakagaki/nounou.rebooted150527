package nounous.data.traits

import scala.Vector
import nounous.data.x.X

/**Encapsulates segment, frame, and sampling information for xdata and XDataChannel.
 */
trait XFrames extends X {

  //segments, length, and isValidFrame
  /** Number of segments.
    */
  def segments: Int

  /**Total number of frames in each segment.
    */
  def length: Vector[Int]

  /** Is this frame valid?
    */
  final def isValidFrame(segment: Int, frame: Int) = (0 <= frame && frame < length(segment))

  //timestamps
  /** OVERRIDE: List of starting timestamps for each segment.
    */
  def startTimestamp: Vector[Long]
  /** OVERRIDE: End timestamp for each segment. Implement by overriding _endTimestamp
    */
  def endTimestamp: Vector[Long]

  //sampling rate information
  /**OVERRIDE: Sampling rate of frame data in Hz
    */
  def sampleRate: Double
  /**Buffered inverse of sampling, in seconds: Double
    */
  def sampleInterval = 1.0/sampleRate
  /**Buffered timestamps (microseconds) between frames.
    */
  def timestampsPerFrame = sampleInterval * 1000000D
  /**Buffered frames between timestamps (microseconds).
    */
  def framesPerTimestamp = 1D/timestampsPerFrame


  //frameToTimestamp, timestampToFrame
  /** Timestamp of the given data frame index (in microseconds).
    */
  final def frameToTimestamp(segment: Int, frame:Int): Long = {
    require( isValidFrame(segment, frame) )
    startTimestamp(segment) + (frame.toDouble * timestampsPerFrame).toLong
  }
  /** Closest frame index to the given timestamp. Will give beginning or end frames, if timestamp is
    * out of range.
    */
  final def timestampToFrame(timestamp: Long): (Int, Int) = {
    if(timestamp <= startTimestamp(0) ){
      (0, 0)
    } else {
      var tempret = (0, 0)
      var seg = 0
      while(seg < segments - 1 && tempret == (0, 0)){
        if( timestamp < endTimestamp(seg) ){
          tempret = (seg, ((timestamp-startTimestamp(seg)) * framesPerTimestamp).toInt)
        } else if(timestamp < startTimestamp(seg+1)) {
          tempret = if(timestamp - endTimestamp(seg) < startTimestamp(seg+1) - timestamp) (seg, length(seg)-1) else (seg + 1, 0)
        } else {
          seg += 1
        }
      }
      if(tempret == (0, 0)){
        if(timestamp <= endTimestamp(segments -1)){
          tempret = ( segments - 1, ((timestamp - startTimestamp(segments-1)) * framesPerTimestamp).toInt )
        } else {
          tempret = ( segments -1, length(segments-1)-1)
        }
      }
      tempret
    }
  }

  override def isCompatible(that: X): Boolean = {
    that match {
      case x: XFrames => {
        (this.segments == x.segments) &&(this.length == x.length) &&
          (this.startTimestamp == x.startTimestamp) &&
          (this.sampleRate == x.sampleRate)
      }
      case _ => false
    }
  }


}
