package nounou.data.traits

import nounou.data.X
import breeze.numerics.round
import nounou.data.ranges.{RangeFrSpecifier, RangeFr}

/**This trait of XData and XDataChannel objects encapsulates segment,
  * frame, and sampling information for electrophysiological and imaging recordings..
  */
trait XFrames extends X {

  /** Throws IllegalArgumentException if segmentCount != 1... use as check for functions which assume segment = 0.
    * @param func name of current function/signature called (which assumes segment = 0 )
    * @param altFunc  name of function/signature which should be called instead, with explicit specification of segment = 0
    */
  @throws[IllegalArgumentException]
  private def errorIfMultipleSegments(func:String, altFunc:String): Unit = {
    loggerRequire(segmentCount == 1, func + " should not be used if the file has more than one segment. Use " + altFunc + " instead")
  }

  // <editor-fold defaultstate="collapsed" desc="segment related: segmentCount, segmentLength/length ">
  
  /** Number of segments in data.
    */
  def segmentCount: Int

  /**Total number of frames in each segment.
    */
  def segmentLength: Array[Int]
  /**Return [[segmentLength]] as Array, for easy access from Java/Mathematica/MatLab.
    */
  final def segmentLengthA = segmentLength.toArray

  /**Total length in frames of data. Use [[segmentLength]] instead, for data which has more than one segment.
    */
  final def length: Int = {//segmentLength.foldLeft(0)( _ + _ )
      errorIfMultipleSegments("length", "segmentLength(segment: Int)")
      segmentLength(0)
    }

  /** OVERRIDE: List of starting frames for each segment.
    */
  def segmentStartFr: Array[Int] = segmentLength.scanLeft(0){ _ + _ }.init

  /**Return [[segmentStartFr]] as Array, for easy access from Java/Mathematica/MatLab.
    */
  final def segmentStartFrA = segmentStartFr.toArray

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc="segment timestamps: segmentStartTs/startTs/segmentEndTs/lastTs ">

  /** OVERRIDE: List of starting timestamps for each segment.
    */
  def segmentStartTs: Array[Long]
  final def startTs: Long = {
    errorIfMultipleSegments("startTs", "segmentStartTS(segment: Int)")
    segmentStartTs(0)
  }
  /**Return [[segmentStartTs]] as Array, for easy access from Java/Mathematica/MatLab.
    */
  final def segmentStartTsA = segmentStartTs.toArray
  /** OVERRIDE: End timestamp for each segment. Implement by overriding _endTimestamp
    */
  def segmentEndTs: Array[Long]
  /**Return [[segmentEndTs]] as Array, for easy access from Java/Mathematica/MatLab.
    */
  final def segmentEndTsA = segmentEndTs.toArray
  /**End timestamp for data. Use [[segmentEndTs]] instead, for data which has more than one segment.
    */
  final def EndTs: Long = {
    errorIfMultipleSegments("lastTs", "segmentEndTS(segment: Int)")
    segmentEndTs(0)
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="isValidFr/isRealisticFr">

  /** Is this frame valid?
    */
  final def isValidFrsg(frame: Int, segment: Int): Boolean = (0 <= frame && frame < segmentLength(segment))

  final def isRealisticFrsg(frame: Int, segment: Int): Boolean =
    (-100000 <= frame && frame < segmentLength(segment) + 100000)
//  final def isRealisticFr(range: Range.Inclusive, segment: Int): Boolean =
//    (-100000 <= range.start && range.end < segmentLength(segment) + 100000)
  final def isRealisticRange(range: RangeFrSpecifier): Boolean = {
    val seg = range.getRealSegment(this)
    val ran = range.getRealRange(this)
    isRealisticFrsg(ran.start, seg) && isRealisticFrsg(ran.last, seg)
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="Sample Rate: sampleRate/sampleInterval/tsPerFr/frPerTs">

  /**OVERRIDE: Sampling rate of frame data in Hz
    */
  def sampleRate: Double
  /**Buffered inverse of sampling, in seconds: Double.
    *DO NOT OVERRIDE: not final due to override with lazy val in immutable frames.
    */
  def sampleInterval = 1.0/sampleRate
  /**Buffered timestamps (microseconds) between frames.
    *DO NOT OVERRIDE: not final due to override with lazy val in immutable frames.
    */
  def factorTSperFR = sampleInterval * 1000000D
  /**Buffered frames between timestamps (microseconds).
    *DO NOT OVERRIDE: not final due to override with lazy val in immutable frames.
    */
  def factorFRperTS = 1D/factorTSperFR

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="Time specification: conversion between frame/segment and TS">

  final def convertFRtoTS(frame:Int): Long = {
//    if(segmentCount==0) segmentStartTs(0) + (frame.toDouble * tsPerFr).toLong
//    else {
//      var seg = 1
//      var continue = true
//      while (continue && seg < segmentCount) {
//        if (frame >= segmentStartFr(seg)) seg += 1
//        else continue = false
//      }
//      segmentStartTs(seg-1) + (frame.toDouble * tsPerFr).toLong
//    }
    errorIfMultipleSegments("convertFrToTs(frame: Int)", "convertFsToTs(frame: Int, segment: Int)")
    convertFStoTS(frame, 0)
  }

  /** Absolute timestamp of the given data frame index (in microseconds).
    */
  final def convertFStoTS(frame:Int, segment: Int): Long = {
    loggerRequire( isValidFrsg(frame, segment), "Not valid frame/segment specification!" )
    segmentStartTs(segment) + ((frame-1).toDouble * factorTSperFR).toLong
  }

  /** Closest frame/segment index to the given absolute timestamp. Will give frames which are out of range (i.e. negative, etc)
    * if necessary.
    *
    * @param timestamp in Long
//    * @param negativeIfOOB If true, will give a frame stamp as negative or larger than data length. Useful for overhangs. If False, will throw error.
    * @return
    */
  final def convertTStoFS(timestamp: Long): (Int, Int) = {

    var tempret: (Int, Int) = (0 , 0)
    var changed = false

    //timestamp is before the start of the first segment
    if( timestamp <= segmentStartTs(0) ){
      tempret = ( ((timestamp-segmentStartTs(0)) * factorFRperTS).toInt, 0)
    } else {
      //loop through segments to find appropriate segment which (contains) given timestamp
      var seg = 0
      while(seg < segmentCount - 1 && !changed ){
        if( timestamp <= segmentEndTs(seg) ){
          // if the timestamp is smaller than the end of the current segment, it fits in the current segment
          tempret = ( ((timestamp-segmentStartTs(seg)) * factorFRperTS).toInt, seg)
          changed = true
        } else if( timestamp < segmentStartTs(seg+1) ) {
          //The timestamp is between the end of the current segment and the beginning of the next segment...
          if( timestamp - segmentEndTs(seg) < segmentStartTs(seg+1) - timestamp){
            //  ...timestamp is closer to end of current segment than beginning of next segment
            tempret = (((timestamp-segmentEndTs(seg)) * factorFRperTS).toInt, seg)
            changed = true
          } else {
            //  ...timestamp is closer to beginning of next segment than end of current segment
            tempret = (((timestamp-segmentStartTs(seg + 1)) * factorFRperTS).toInt, seg + 1)
            changed = true
          }
        } else {
          //go on to next segment
          seg += 1
        }
      }

      //deal with the lastValid segment separately
      if( !changed ){
        if(timestamp <= segmentEndTs(segmentCount -1)){
          // if the timestamp is smaller than the end of the current segment, it fits in the current segment
          tempret = ( ((timestamp - segmentStartTs(segmentCount-1)) * factorFRperTS).toInt, segmentCount - 1 )
        } else {
          // if the timestamp is larger than the end of the lastValid segment
          tempret = ( ((timestamp - segmentEndTs(segmentCount-1)) * factorFRperTS).toInt, segmentCount - 1 )
        }
      }

    }

    tempret

  }
  final def convertTStoFSA(timestamp: Long): Array[Int] = {
    val tempret = convertTStoFS(timestamp)//, false)
    Array[Int]( tempret._1, tempret._2 )
  }
  final def convertTStoFR(timestamp: Long): Int = {
    errorIfMultipleSegments("length", "segmentLength(segment: Int)")
    convertTStoFS(timestamp)._1
  }

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc="Time specification: conversion between frame/segment and ms">

  /** Time of the given data frame and segment (in milliseconds, with t=0 being the time for frame 0 within the segment).
    */
  final def convertFRtoMS(frame: Int): Double = {
    frame.toDouble * sampleInterval * 1000d
    //(frameSegmentToTS(frame, segment)-frameSegmentToTS(0, segment)).toDouble / 1000d
  }
  final def convertFRtoMS(frame: Double): Double = convertFRtoMS(round(frame).toInt)

  /** Closest frame/segment index to the given timestamp in ms (frame 0 within segment being time 0). Will give beginning or lastValid frames, if timestamp is
    * out of range.
    */
  final def convertMStoFR(ms: Double): Int = {
    //val tempret =
      (ms*sampleRate*0.001).toInt
    //require(tempret>=0, "frame index must be >0, not checking upper range. Input ms=" + ms + ", calculated output=" + tempret)
    //tempret
    //tsToFrameSegment( (ms*1000).toLong + frameSegmentToTS(0, 0), negativeIfOOB )
  }

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc="Time specification: conversion between ts and ms">

  final def convertTStoMS(timestamp: Long): Double = convertFRtoMS( convertTStoFR(timestamp) )
  final def convertMStoTS(ms: Double): Long = convertFRtoTS( convertMStoFR(ms) )

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc="Time specification: tsToClosestSg">

  /** Closest segment index to the given timestamp.
    */
  final def convertTStoClosestSegment(timestamp: Long): Int = {
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
        //print("sc " + this.segmentCount == x.segmentCount + " startTs "+ this.segmentStartTs.corresponds(x.segmentStartTs)(_ == _ ))
        (this.segmentCount == x.segmentCount) &&
          //ToDo 2: removed for corrupt page drop at end, like E04LC. Add better error code and tests
          //(this.segmentLength.corresponds(x.segmentLength)(_ == _ )) &&
          (this.segmentStartTs.corresponds(x.segmentStartTs)(_ == _ )) &&
//          (this.segmentLength.sameElements(x.segmentLength)) &&
//          (this.segmentStartTs.sameElements(x.segmentStartTs)) &&
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

  override val segmentLength: Array[Int]
  final override lazy val segmentCount: Int = segmentLength.length

//  println("XFramesImmutable segmentLength " + segmentLength.toVector.toString)
//  println("XFramesImmutable segmentCount " + segmentCount)

  /**Cumulative frame numbers for segment starts.
    */
  final lazy val segmentStartFrames: Array[Int] = {
    var sum = 0
    ( for(seg <- 0 until segmentLength.length) yield {sum += segmentLength(seg); sum} ).toArray.+:(0).dropRight(1)
  }
  //=  DenseVector( accumulate(DenseVector(length.toArray)).toArray.map( _ + 1 ).+:(0).take(length.length) ).toArray.toVector

  override val segmentStartTs: Array[Long]
  override final lazy val segmentEndTs: Array[Long] = {
    ( for(seg <- 0 until segmentCount) yield segmentStartTs(seg) + ((segmentLength(seg)-1)*factorTSperFR).toLong ).toArray
  }

  //sampling rate information
  override val sampleRate: Double
  override final lazy val sampleInterval = 1.0/sampleRate
  override final lazy val factorTSperFR = sampleInterval * 1000000D
  override final lazy val factorFRperTS = 1D/factorTSperFR

}
