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

  private def warnIfMultipleSegments(func:String, altFunc:String): Unit = {
    if(segmentCount != 1) logger.warn("{}} should not be used if the file has more than one segment. Use {} instead", func, altFunc)
  }

  // <editor-fold desc="segment related: segmentCount, segmentLength/length ">
  
  /** Number of segments in data.
    */
  def segmentCount: Int

  /**Total number of frames in each segment.
    */
  def segmentLength: Vector[Int]
  final def segmentLengthA = segmentLength.toArray
  final lazy val length: Long = {
    warnIfMultipleSegments("length", "segmentLength(segment: Int)")
    segmentLength(0)
  }

  // </editor-fold>
  // <editor-fold desc="segment timestamps: segmentStartTs/startTS/segmentEndTs/endTS ">

  /** OVERRIDE: List of starting timestamps for each segment.
    */
  def segmentStartTs: Vector[Long]
  final def startTs: Long = {
    warnIfMultipleSegments("startTs", "segmentStartTS(segment: Int)")
    segmentStartTs(0)
  }
  final def segmentStartTsA = segmentStartTs.toArray
  /** OVERRIDE: End timestamp for each segment. Implement by overriding _endTimestamp
    */
  def segmentEndTs: Vector[Long]
  final def EndTs: Long = {
    warnIfMultipleSegments("endTs", "segmentEndTS(segment: Int)")
    segmentEndTs(0)
  }
  final def segmentEndTsA = segmentEndTs.toArray

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="isValidFrame">

  /** Is this frame valid?
    */
  final def isValidFrame(frame: Int, segment: Int): Boolean = (0 <= frame && frame < segmentLength(segment))
  /** Is this frame valid in the current segment?
    */
  final def isValidFrame(frame: Int): Boolean = isValidFrame(frame, 0)//currentSegment)

  final def isRealisticFrame(frame: Int, segment: Int): Boolean = (-100000 <= frame && frame < segmentLength(segment) + 100000)
  final def isRealisticFrameRange(range: RangeFr, segment: Int): Boolean = (-100000 <= range.start && range.endMarker < segmentLength(segment) + 100000)

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
  def framesPerTs = 1D/tsPerFrame

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="Time specification: conversion between frame/segment and TS">

  /** Absolute timestamp of the given data frame index (in microseconds).
    */
  final def frsgToTs(frame:Int, segment: Int): Long = {
    require( isValidFrame(frame, segment) )
    segmentStartTs(segment) + (frame.toDouble * tsPerFrame).toLong
  }
  final def frToTs(frame:Int): Long = {
    warnIfMultipleSegments("length", "segmentLength(segment: Int)")
    frsgToTs(frame, 0)
  }
  final def frToTs(frameSegment:(Int, Int)): Long = frsgToTs(frameSegment._1, frameSegment._2)
//  final def tsToFrameSegment(timestamp: Long): (Int, Int) = tsToFrameSegment(timestamp, false)

  /** Closest frame/segment index to the given absolute timestamp. Will give frames which are out of range (i.e. negative, etc)
    * if necessary.
    *
    * @param timestamp in Long
//    * @param negativeIfOOB If true, will give a frame stamp as negative or larger than data length. Useful for overhangs. If False, will throw error.
    * @return
    */
  final def tsToFrsg(timestamp: Long): (Int, Int) = {

    var tempret: (Int, Int) = (0 , 0)
    var changed = false

    //timestamp is before the start of the first segment
    if( timestamp <= segmentStartTs(0) ){
      tempret = ( ((timestamp-segmentStartTs(0)) * framesPerTs).toInt, 0)
    } else {
      //loop through segments to find appropriate segment which (contains) given timestamp
      var seg = 0
      while(seg < segmentCount - 1 && !changed ){
        if( timestamp <= segmentEndTs(seg) ){
          // if the timestamp is smaller than the end of the current segment, it fits in the current segment
          tempret = ( ((timestamp-segmentStartTs(seg)) * framesPerTs).toInt, seg)
          changed = true
        } else if( timestamp < segmentStartTs(seg+1) ) {
          //The timestamp is between the end of the current segment and the beginning of the next segment...
          if( timestamp - segmentEndTs(seg) < segmentStartTs(seg+1) - timestamp){
            //  ...timestamp is closer to end of current segment than beginning of next segment
            tempret = (((timestamp-segmentEndTs(seg)) * framesPerTs).toInt, seg)
            changed = true
          } else {
            //  ...timestamp is closer to beginning of next segment than end of current segment
            tempret = (((timestamp-segmentStartTs(seg + 1)) * framesPerTs).toInt, seg + 1)
            changed = true
          }
        } else {
          //go on to next segment
          seg += 1
        }
      }

      //deal with the last segment separately
      if( !changed ){
        if(timestamp <= segmentEndTs(segmentCount -1)){
          // if the timestamp is smaller than the end of the current segment, it fits in the current segment
          tempret = ( ((timestamp - segmentStartTs(segmentCount-1)) * framesPerTs).toInt, segmentCount - 1 )
        } else {
          // if the timestamp is larger than the end of the last segment
          tempret = ( ((timestamp - segmentEndTs(segmentCount-1)) * framesPerTs).toInt, segmentCount - 1 )
        }
      }

    }

    tempret

  }
  final def tsToFrameSegmentA(timestamp: Long): Array[Int] = {
    val tempret = tsToFrsg(timestamp)//, false)
    Array[Int]( tempret._1, tempret._2 )
  }

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc="Time specification: conversion between frame/segment and ms">

  /** Time of the given data frame and segment (in milliseconds, with t=0 being the time for frame 0 within the segment).
    */
  final def frToMs(frame: Int): Double = {
    frame.toDouble * sampleInterval * 1000d
    //(frameSegmentToTS(frame, segment)-frameSegmentToTS(0, segment)).toDouble / 1000d
  }
  final def frToMs(frame: Double): Double = frToMs(round(frame).toInt)

  /** Closest frame/segment index to the given timestamp in ms (frame 0 within segment being time 0). Will give beginning or last frames, if timestamp is
    * out of range.
    */
  final def msToFr(ms: Double): Int = {
    //val tempret =
      (ms*sampleRate*0.001).toInt
    //require(tempret>=0, "frame index must be >0, not checking upper range. Input ms=" + ms + ", calculated output=" + tempret)
    //tempret
    //tsToFrameSegment( (ms*1000).toLong + frameSegmentToTS(0, 0), negativeIfOOB )
  }

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc="Time specification: conversion between ts and ms">

  final def tsToMs(timestamp: Long): Double = frToMs( tsToFrsg(timestamp)._1 )

  // </editor-fold>



  /** Closest segment index to the given timestamp.
    */
  final def tsToClosestSg(timestamp: Long): Int = {
    if(timestamp <= segmentStartTs(0) ){
      0
    } else {
      var tempret = -1
      var seg = 0
      while(seg < segmentCount - 1 && tempret == -1){
        if( timestamp < segmentEndTs(seg) ){
          tempret = seg
        } else if(timestamp < segmentStartTs(seg+1)) {
          tempret = if(timestamp - segmentEndTs(seg) < segmentStartTs(seg+1) - timestamp) seg else seg + 1
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
        (this.segmentCount == x.segmentCount) &&(this.segmentLength == x.segmentLength) &&
          (this.segmentStartTs == x.segmentStartTs) &&
          (this.sampleRate == x.sampleRate)
      }
      case _ => false
    }
  }

  // </editor-fold>


  def timingSummary(): String = {
    var tempout = "Timing Summary: fs=" + sampleRate + ", segmentCount=" + segmentCount + ""
    for( seg <- 0 until segmentCount) {
      tempout += "\n               Seg " + seg + ": length=" + segmentLength(seg)+", ms=[0, " +
        (segmentLength(seg).toDouble/sampleRate*1000).toString + "], segmentStartTs=" + segmentStartTs(seg)
    }
    tempout
  }


}

trait XFramesImmutable extends XFrames {

  /** Number of segmentCount.
    */
  final override lazy val segmentCount: Int = segmentLength.length

  /**Total number of frames in each segment.
    */
  override val segmentLength: Vector[Int]

  /**Cumulative frame numbers for segment starts.
    */
  final lazy val segmentStartFrames: Vector[Int] = {
    var sum = 0
    ( for(seg <- 0 until segmentLength.length) yield {sum += segmentLength(seg); sum} ).toVector.+:(0).dropRight(1)
  }
  //=  DenseVector( accumulate(DenseVector(length.toArray)).toArray.map( _ + 1 ).+:(0).take(length.length) ).toArray.toVector

  /** OVERRIDE: List of starting timestamps for each segment.
    */
  override val segmentStartTs: Vector[Long]
  /** OVERRIDE: End timestamp for each segment. Implement by overriding _endTimestamp
    */
  override final lazy val segmentEndTs: Vector[Long] = {
    ( for(seg <- 0 until segmentCount) yield segmentStartTs(seg) + ((segmentLength(seg)-1)*tsPerFrame).toLong ).toVector
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
  override final lazy val framesPerTs = 1D/tsPerFrame


}
