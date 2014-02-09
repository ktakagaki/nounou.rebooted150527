package nounou.data.traits

import nounou.data.X
import scala.Vector

/**Encapsulates segment, frame, and sampling information for xdata and XDataChannel.
 */
trait XFrames extends X {

  // <editor-fold desc="segment related: segmentCount, segmentLengths, currentSegment ">
  
  /** Number of segments in data.
    */
  def segmentCount: Int

  /**Total number of frames in each segment.
    */
  def segmentLengths: Vector[Int]

  private var _currentSegment = 0

  /** Which segment is currently active? (initially 0) This variable allows syntax such as
    * <code>readPoint(channel: Int, frame: Int)</code>, leaving out an explicitly specified segment.
    * This is useful, for example, for file formats which only feature one segment.
    */
  def currentSegment = _currentSegment
  /** currentSegment getter, Scala style.*/
  def currentSegment_=(segment: Int) : Int = {
    if( _currentSegment != segment )
      if( 0 <= segment && segment < segmentCount) _currentSegment = segment
      else require(false, "new segment out of bounds!")
    _currentSegment
  }
  
  // </editor-fold>
  // <editor-fold desc="segment timestamps: segmentStartTSs/segmentEndTSs ">

  /** OVERRIDE: List of starting timestamps for each segment.
    */
  def segmentStartTSs: Vector[Long]
  /** OVERRIDE: End timestamp for each segment. Implement by overriding _endTimestamp
    */
  def segmentEndTSs: Vector[Long]

  // </editor-fold>

  // <editor-fold desc="isValidFrame">

  /** Is this frame valid?
    */
  final def isValidFrame(frame: Int, segment: Int): Boolean = (0 <= frame && frame < segmentLengths(segment))
  /** Is this frame valid in the current segment?
    */
  final def isValidFrame(frame: Int): Boolean = isValidFrame(frame, currentSegment)

  // </editor-fold>

  // <editor-fold desc="Sample Rate: sampleRate/sampleInterval/tsPerFrame/framesPerTS">

  /**OVERRIDE: Sampling rate of frame data in Hz
    */
  def sampleRate: Double
  /**Buffered inverse of sampling, in seconds: Double
    */
  def sampleInterval = 1.0/sampleRate
  /**Buffered timestamps (microseconds) between frames.
    */
  def tsPerFrame = sampleInterval * 1000000D
  /**Buffered frames between timestamps (microseconds).
    */
  def framesPerTS = 1D/tsPerFrame

  // </editor-fold>
  // <editor-fold desc="Sample Rate: frameToTS/tsToFrame">

  /** Timestamp of the given data frame index (in microseconds).
    */
  final def frameToTS(frame:Int, segment: Int): Long = {
    require( isValidFrame(frame, segment) )
    segmentStartTSs(segment) + (frame.toDouble * tsPerFrame).toLong
  }
  /** Closest frame/segment index to the given timestamp. Will give beginning or last frames, if timestamp is
    * out of range.
    */
  final def tsToFrame(timestamp: Long, negativeIfOOB: Boolean = false): (Int, Int) = {
    if(timestamp <= segmentStartTSs(0) ){
      (0, 0)
    } else {
      var tempret = (0, 0)
      var seg = 0
      while(seg < segmentCount - 1 && tempret == (0, 0)){
        if( timestamp < segmentEndTSs(seg) ){
          tempret = (((timestamp-segmentStartTSs(seg)) * framesPerTS).toInt, seg)
        } else if(timestamp < segmentStartTSs(seg+1)) {
          tempret = if(timestamp - segmentEndTSs(seg) < segmentStartTSs(seg+1) - timestamp) (segmentLengths(seg)-1, seg) else (0, seg + 1)
        } else {
          seg += 1
        }
      }
      if(tempret == (0, 0)){
        if(timestamp <= segmentEndTSs(segmentCount -1)){
          tempret = ( ((timestamp - segmentStartTSs(segmentCount-1)) * framesPerTS).toInt, segmentCount - 1 )
        } else {
          tempret = ( segmentLengths(segmentCount-1)-1, segmentCount -1)
        }
      }
      tempret
    }
  }
  // </editor-fold>

  // <editor-fold desc="XConcatenatable">

  override def isCompatible(that: X): Boolean = {
    that match {
      case x: XFrames => {
        (this.segmentCount == x.segmentCount) &&(this.segmentLengths == x.segmentLengths) &&
          (this.segmentStartTSs == x.segmentStartTSs) &&
          (this.sampleRate == x.sampleRate)
      }
      case _ => false
    }
  }

  // </editor-fold>

}

trait XFramesImmutable extends XFrames {

  /** Number of segmentCount.
    */
  final override lazy val segmentCount: Int = segmentLengths.length

  /**Total number of frames in each segment.
    */
  override val segmentLengths: Vector[Int]

  /**Cumulative frame numbers for segment starts.
    */
  final lazy val segmentStartFrames: Vector[Int] = {
    var sum = 0
    ( for(seg <- 0 until segmentLengths.length) yield {sum += segmentLengths(seg); sum} ).toVector.+:(0).dropRight(1)
  }
  //=  DenseVector( accumulate(DenseVector(length.toArray)).toArray.map( _ + 1 ).+:(0).take(length.length) ).toArray.toVector

  /** OVERRIDE: List of starting timestamps for each segment.
    */
  override val segmentStartTSs: Vector[Long]
  /** OVERRIDE: End timestamp for each segment. Implement by overriding _endTimestamp
    */
  override final lazy val segmentEndTSs: Vector[Long] = {
    ( for(seg <- 0 until segmentCount) yield segmentStartTSs(seg) + ((segmentLengths(seg)-1)*tsPerFrame).toLong ).toVector
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
  override final lazy val tsPerFrame = sampleInterval * 1000000D
  /**Buffered frames between timestamps (microseconds).
    */
  override final lazy val framesPerTS = 1D/tsPerFrame


}
