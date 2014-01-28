package nounous.data.xdata

import scala.Vector

/**
 * Created by Kenta on 12/15/13.
 */
trait XFramesImmutable extends XFrames {

  //segments, length, and isValidFrame
  /** Number of segments.
    */
  override val segments: Int

  /**Total number of frames in each segment.
    */
  override val length: Vector[Int]

  //timestamps
  /** OVERRIDE: List of starting timestamps for each segment.
    */
  override val startTimestamp: Vector[Long]
  /** OVERRIDE: End timestamp for each segment. Implement by overriding _endTimestamp
    */
  override final lazy val endTimestamp: Vector[Long] = {
    ( for(seg <- 0 until segments) yield startTimestamp(seg) + ((length(seg)-1)*timestampsPerFrame).toLong ).toVector
  }

  //sampling rate information
  /**OVERRIDE: Sampling rate of frame data in Hz
    */
  override val sampleRate: Double
  /**Buffered inverse of sampling, in seconds: Double
    */
  override final lazy val sampleInterval = 1.0/sampleRate
  /**Buffered timestamps (microseconds) between frames.
    */
  override final lazy val timestampsPerFrame = sampleInterval * 1000000D
  /**Buffered frames between timestamps (microseconds).
    */
  override final lazy val framesPerTimestamp = 1D/timestampsPerFrame


}
