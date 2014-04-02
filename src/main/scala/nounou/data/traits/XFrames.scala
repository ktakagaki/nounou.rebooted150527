package nounou.data.traits

import nounou.data.X
import scala.Vector
import nounou.LoggingExt
import breeze.numerics.round
import nounou.ranges.RangeFr

/**This trait of XData and XDataChannel objects encapsulates segment,
  * frame, and sampling information for electrophysiological and imaging recordings..
  */
trait XFrames extends X with LoggingExt {

  // <editor-fold desc="segment related: segmentCount, segmentLengths, currentSegment ">
  
  /** Number of segments in data.
    */
  def segmentCount: Int

  /**Total number of frames in each segment.
    */
  def segmentLengths: Vector[Int]
  def segmentLengthsA = segmentLengths.toArray

//  private var _currentSegment = 0
//
//  /** Which segment is currently active? (initially 0) This variable allows syntax such as
//    * <code>readPoint(channel: Int, frame: Int)</code>, leaving out an explicitly specified segment.
//    * This is useful, for example, for file io which only feature one segment.
//    */
//  def currentSegment = _currentSegment
//  /** currentSegment getter, Scala style.*/
//  def currentSegment_=(segment: Int) : Int = {
//    if( _currentSegment != segment )
//      if( 0 <= segment && segment < segmentCount) _currentSegment = segment
//      else require(false, "new segment out of bounds!")
//    _currentSegment
//  }
  
  // </editor-fold>
  // <editor-fold desc="segment timestamps: segmentStartTSs/segmentEndTSs ">

  /** OVERRIDE: List of starting timestamps for each segment.
    */
  def segmentStartTSs: Vector[Long]
  def segmentStartTSsA = segmentStartTSs.toArray
  /** OVERRIDE: End timestamp for each segment. Implement by overriding _endTimestamp
    */
  def segmentEndTSs: Vector[Long]
  def segmentEndTSsA = segmentEndTSs.toArray

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="isValidFrame">

  /** Is this frame valid?
    */
  final def isValidFrame(frame: Int, segment: Int): Boolean = (0 <= frame && frame < segmentLengths(segment))
  /** Is this frame valid in the current segment?
    */
  final def isValidFrame(frame: Int): Boolean = isValidFrame(frame, 0)//currentSegment)

  final def isRealisticFrame(frame: Int, segment: Int): Boolean = (-100000 <= frame && frame < segmentLengths(segment) + 100000)
  final def isRealisticFrameRange(range: RangeFr, segment: Int): Boolean = (-100000 <= range.start && range.endMarker < segmentLengths(segment) + 100000)

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="Sample Rate: sampleRate/sampleInterval/tsPerFrame/framesPerTS">

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

  // <editor-fold defaultstate="collapsed" desc="Time specification: conversion between frame/segment and TS">

  /** Absolute timestamp of the given data frame index (in microseconds).
    */
  final def frameSegmentToTS(frame:Int, segment: Int): Long = {
    require( isValidFrame(frame, segment) )
    segmentStartTSs(segment) + (frame.toDouble * tsPerFrame).toLong
  }
//  final def tsToFrameSegment(timestamp: Long): (Int, Int) = tsToFrameSegment(timestamp, false)

  final def tsToFrameSegmentA(timestamp: Long): Array[Int] = {
    val tempret = tsToFrameSegment(timestamp)//, false)
    Array[Int]( tempret._1, tempret._2 )
  }
//  final def tsToFrameSegmentA(timestamp: Long, negativeIfOOB: Boolean): Array[Int] = {
//    val tempret = tsToFrameSegment(timestamp, negativeIfOOB)
//    Array[Int]( tempret._1, tempret._2 )
//  }

  /** Closest frame/segment index to the given absolute timestamp. Will give frames which are out of range (i.e. negative, etc)
    * if necessary.
    *
    * @param timestamp in Long
//    * @param negativeIfOOB If true, will give a frame stamp as negative or larger than data length. Useful for overhangs. If False, will throw error.
    * @return
    */
  final def tsToFrameSegment(timestamp: Long): (Int, Int) = {

    var tempret: (Int, Int) = (0 , 0)
    var changed = false

    //timestamp is before the start of the first segment
    if( timestamp <= segmentStartTSs(0) ){
      tempret = ( ((timestamp-segmentStartTSs(0)) * framesPerTS).toInt, 0)
    } else {
      //loop through segments to find appropriate segment which (contains) given timestamp
      var seg = 0
      while(seg < segmentCount - 1 && !changed ){
        if( timestamp <= segmentEndTSs(seg) ){
          // if the timestamp is smaller than the end of the current segment, it fits in the current segment
          tempret = ( ((timestamp-segmentStartTSs(seg)) * framesPerTS).toInt, seg)
          changed = true
        } else if( timestamp < segmentStartTSs(seg+1) ) {
          //The timestamp is between the end of the current segment and the beginning of the next segment...
          if( timestamp - segmentEndTSs(seg) < segmentStartTSs(seg+1) - timestamp){
            //  ...timestamp is closer to end of current segment than beginning of next segment
            tempret = (((timestamp-segmentEndTSs(seg)) * framesPerTS).toInt, seg)
            changed = true
          } else {
            //  ...timestamp is closer to beginning of next segment than end of current segment
            tempret = (((timestamp-segmentStartTSs(seg + 1)) * framesPerTS).toInt, seg + 1)
            changed = true
          }
        } else {
          //go on to next segment
          seg += 1
        }
      }

      //deal with the last segment separately
      if( !changed ){
        if(timestamp <= segmentEndTSs(segmentCount -1)){
          // if the timestamp is smaller than the end of the current segment, it fits in the current segment
          tempret = ( ((timestamp - segmentStartTSs(segmentCount-1)) * framesPerTS).toInt, segmentCount - 1 )
        } else {
          // if the timestamp is larger than the end of the last segment
          tempret = ( ((timestamp - segmentEndTSs(segmentCount-1)) * framesPerTS).toInt, segmentCount - 1 )
        }
      }

    }

    tempret

  }
//  final def tsToFrameSegment(timestamp: Long, negativeIfOOB: Boolean): (Int, Int) = {
//    var tempret: (Int, Int) = (0 , 0 )
//    var changed = false
//
//    if( /* TS cond */ timestamp <= segmentStartTSs(0) ){
//      if( negativeIfOOB )  tempret = ( ((timestamp-segmentStartTSs(0)) * framesPerTS).toInt, 0)
//      else {
//        logger.error("timestamp {} is smaller than first frame of first segment!", timestamp.toString)
//        throw new IllegalArgumentException("text")
//      }//tempret = (0, 0)
//    } else {
//      var seg = 0
//      while(seg < segmentCount - 1 && !changed ){
//        if(        /* TS cond */  timestamp <= segmentEndTSs(seg) ){
//          tempret = ( ((timestamp-segmentStartTSs(seg)) * framesPerTS).toInt, seg)
//          changed = true
//        } else if( /* TS cond */ timestamp < segmentStartTSs(seg+1) ) {
//          if(    /* TS cond */ timestamp - segmentEndTSs(seg) < segmentStartTSs(seg+1) - timestamp){
//            if( negativeIfOOB )  tempret = (((timestamp-segmentStartTSs(seg)) * framesPerTS).toInt, seg)
//            else {
//              logger.error("timestamp is in the gap between segments {} and {}!", seg.toString, (seg+1).toString)
//              throw new IllegalArgumentException//tempret = ( segmentLengths(seg) - 1, seg )
//            }
//            changed = true
//          } else {
//            if( negativeIfOOB )  tempret = (((timestamp-segmentStartTSs(seg + 1)) * framesPerTS).toInt, seg + 1)
//            else {
//              logger.error("timestamp is in the gap between segments "+seg+" and "+(seg+1)+"!")
//              throw new IllegalArgumentException()
//            }//tempret = (0, seg + 1)
//            changed = true
//          }
//        } else {
//          seg += 1
//        }
//      }
//      if( !changed ){
//        if(timestamp <= segmentEndTSs(segmentCount -1)){
//          tempret = ( ((timestamp - segmentStartTSs(segmentCount-1)) * framesPerTS).toInt, segmentCount - 1 )
//        } else {
//          if( negativeIfOOB )  tempret = ( ((timestamp - segmentStartTSs(segmentCount-1)) * framesPerTS).toInt, segmentCount - 1 )
//          else {
//            loggerError("timestamp {} is larger than last frame {} of last segment {}!",
//              timestamp.toString, (segmentLengths.last - 1).toString, (segmentCount - 1).toString )
//          }//tempret = ( segmentLengths(segmentCount-1)-1, segmentCount -1)
//        }
//      }
//    }
//
//    //if( !negativeIfOOB )  logger.error("this must be a bug in tsToFrameSegment!", new IllegalArgumentException )//require( tempret._1 >= 0 && tempret._1 < segmentLengths( tempret._2 ), "This must be a bug!")
//
//    tempret
//
//  }

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc="Time specification: conversion between frame/segment and ms">

  /** Time of the given data frame and segment (in milliseconds, with t=0 being the time for frame 0 within the segment).
    */
  final def frameToMs(frame:Int): Double = {
    frame.toDouble * sampleInterval * 1000d
    //(frameSegmentToTS(frame, segment)-frameSegmentToTS(0, segment)).toDouble / 1000d
  }
  final def frameToMs(frame:Double): Double = frameToMs(round(frame).toInt)

  /** Closest frame/segment index to the given timestamp in ms (frame 0 within segment being time 0). Will give beginning or last frames, if timestamp is
    * out of range.
    */
  final def msToFrame(ms: Double): Int = {
    //val tempret =
      (ms*sampleRate*0.001).toInt
    //require(tempret>=0, "frame index must be >0, not checking upper range. Input ms=" + ms + ", calculated output=" + tempret)
    //tempret
    //tsToFrameSegment( (ms*1000).toLong + frameSegmentToTS(0, 0), negativeIfOOB )
  }

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc="Time specification: conversion between ts and ms">

  final def tsToMs(timestamp: Long): Double = frameToMs( tsToFrameSegment(timestamp)._1 )

  // </editor-fold>



  /** Closest segment index to the given timestamp.
    */
  final def tsToClosestSegment(timestamp: Long): Int = {
    if(timestamp <= segmentStartTSs(0) ){
      0
    } else {
      var tempret = -1
      var seg = 0
      while(seg < segmentCount - 1 && tempret == -1){
        if( timestamp < segmentEndTSs(seg) ){
          tempret = seg
        } else if(timestamp < segmentStartTSs(seg+1)) {
          tempret = if(timestamp - segmentEndTSs(seg) < segmentStartTSs(seg+1) - timestamp) seg else seg + 1
        } else {
          seg += 1
        }
      }
      if(tempret == -1){
        tempret = segmentCount - 1
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


  def timingSummary(): String = {
    var tempout = "Timing Summary: fs=" + sampleRate + ", segmentCount=" + segmentCount + ""
    for( seg <- 0 until segmentCount) {
      tempout += "\n               Seg " + seg + ": length=" + segmentLengths(seg)+", ms=[0, " +
        (segmentLengths(seg).toDouble/sampleRate*1000).toString + "], segmentStartTS=" + segmentStartTSs(seg)
    }
    tempout
  }


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
