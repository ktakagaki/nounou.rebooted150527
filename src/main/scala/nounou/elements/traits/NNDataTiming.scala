package nounou.elements.traits

import java.math.BigInteger
import breeze.numerics.round
import nounou.elements.NNElement
import nounou.elements.ranges.SampleRangeSpecifier

/**This trait of NNElement objects (especially [[nounou.elements.data.NNData]] encapsulates
  * segment, frame, and sampling information for electrophysiological and imaging data.
  *
  * Envisioned uses are for [[nounou.elements.data.NNData]],
  * [[nounou.elements.layouts.NNDataLayout]], and [[nounou.elements.discrete.NNSpikes]].
  *
  * @param sampleRate Sample rate in Hz.
  * @param segmentLengths Total number of frames in each segment.
  * @param segmentStartTss  List of starting timestamps for each segment.
  * @param timestampOffset  Timestamp offset to obtain external timestamps by adding to
  *                         internal representation. For example, many formats represent
  *                         timestamps as U64, which is not present in JVM, so they are
  *                         internally shifted to Long (S64), and therefore have
  *                         an offset of + pow(2,63).
  */
class NNDataTiming( val sampleRate: Double,
                    val segmentLengths: Array[Int],
                    val segmentStartTss: Array[Long],
                    val timestampOffset: BigInt
                    ) extends NNElement {

  // <editor-fold defaultstate="collapsed" desc=" extra constructors ">

  def this( sampleRate: Double, segmentLengths: Array[Int], segmentStartTss: Array[Long], timestampOffset: BigInteger) =
              this( sampleRate, segmentLengths, segmentStartTss, new BigInt(timestampOffset))
// These convenience constructors just serve to make you forget timestampOffset
// when it's necessary. This class shouldn't really be constructed outside of the
// library anyway, so convenience is not that critical.
//  def this( sampleRate: Double, segmentLengths: Array[Int], segmentStartTss: Array[Long]) =
//              this( sampleRate, segmentLengths, segmentStartTss, BigInteger.ZERO)
//  def this( sampleRate: Double, segmentLength: Int, startTs: Long) =
//              this( sampleRate, Array[Int](segmentLength), Array[Long](startTs) )
//  def this( sampleRate: Double, segmentLength: Int ) =
//              this( sampleRate, segmentLength, 0L)

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" utility error function (private) ">

  /** Throws IllegalArgumentException if segmentCount != 1... use as check for functions which assume segment = 0.
    * @param func name of current function/signature called (which assumes segment = 0 )
    * @param altFunc  name of function/signature which should be called instead, with explicit specification of segment = 0
    */
  @throws[IllegalArgumentException]
  private def errorIfMultipleSegments(func: String, altFunc: String): Unit = {
    loggerRequire(
      segmentCount == 1,
      func + " should not be used if the file has more than one segment. Use " + altFunc + " instead"
    )
  }

  // </editor-fold>

  final lazy val sampleInterval = 1.0/sampleRate

  // <editor-fold defaultstate="collapsed" desc=" internal representation for timestamps ">

  private val zeroBI = BigInt( BigInteger.ZERO )

  /** Converts the internal timestamp (kept in Long) to file timestamps,
    * which are often in U64.
    */
  final def convertTsToFileTimestamp(ts: Long): BigInteger = {
    if(timestampOffset == zeroBI ) BigInteger.valueOf(ts)
    else timestampOffset.bigInteger.add( BigInteger.valueOf(ts) )
  }

  // </editor-fold>

  // </editor-fold>


  // <editor-fold defaultstate="collapsed" desc="segment related: segmentCount, segmentLength/length ">
  

  /** Number of segments in data.
    */
  lazy val segmentCount: Int = segmentLengths.length

  val defaultStep: Int = 1

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" segmentStartFrames  ">

  /**Cumulative frame numbers for segment starts.
    */
  final lazy val segmentStartFrames: Array[Int] = {
    var sum = 0
    ( for(seg <- 0 until segmentCount) yield {sum += segmentLength(seg); sum} ).toArray.+:(0).dropRight(1)
  }
  final def segmentStartFrame(segment: Int) = segmentStartFrames.apply(segment)

  // </editor-fold>


  // <editor-fold defaultstate="collapsed" desc=" functions for reading segment lengths ">

  /** Length of a given segment. If you are dealing with 1 segment data,
    * a value of -1 (default) is also possible.
    */
  final def segmentLength( segment: Int ): Int = segmentLengthImpl( getRealSegment(segment))
  /** Length of the one and only segment. This signature can
    * only be used if the data only contains one segment.
    */
  final def segmentLength(): Int = segmentLength( -1 )
  final def segmentLengthReal( segment: Int ): Int =
    segmentLengthImpl( segmentLength(segment) )

  /** Implementation of [[segmentLength(Int)]]. Segment number must be a valid number
    * in the range [0, segmentCount).
    */
  private def segmentLengthImpl( segment: Int ): Int = segmentLengths( segment )

  final def getRealSegment( segment: Int ): Int =
    if(segment == -1){
      loggerRequire( segmentCount == 1, "You must always specify a segment when reading from data with multiple segments!")
      0
    } else {
      loggerRequire( segment < segmentCount, s"Segment specified ${segment} does not exist in data object!")
      segment
    }

  /**Total length in frames of data. Use
    * [[nounou.elements.traits.NNDataTiming.segmentLength(seg* segmentLength(Int)]] to
    * get the length of specific segments, for data which have more than one segment.
    */
  lazy val totalLength: Int = segmentLengths.foldLeft(0)( _ + _ )

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="segment timestamp buffered arrays: segmentStartTss/startTs/segmentEndTss/lastTs ">


  lazy val startTs: Long = {
    errorIfMultipleSegments("startTs", "segmentStartTS(segment: Int)")
    segmentStartTss(0)
  }

  /** OVERRIDE: End timestamp for each segment. Implement by overriding _endTimestamp
    */
  lazy val segmentEndTss: Array[Long] = {
    ( for(seg <- 0 until segmentCount) yield
      segmentStartTss(seg) + ((segmentLength(seg)-1)*factorTsPerFr).toLong ).toArray
  }

  lazy val endTs: Long = {
    errorIfMultipleSegments("lastTs", "segmentEndTS(segment: Int)")
    segmentEndTss(0)
  }

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" segmentStartFileTimestamps/segmentEndFileTimestamps ">

  final def segmentStartFileTimestamps(seg: Int) = convertTsToFileTimestamp(segmentStartTss(seg))

  final def segmentEndFileTimestamps(seg: Int) = convertTsToFileTimestamp(segmentStartTss(seg))


  // <editor-fold defaultstate="collapsed" desc="isValidFrsg/isRealisticFrsg">

  /** Is this frame valid?
    */
  final def isValidFrsg(frame: Int, segment: Int): Boolean =
    (0 <= frame && frame < segmentLength(segment))

  final def isRealisticFrsg(frame: Int, segment: Int): Boolean =
    (-100000 <= frame && frame < segmentLength(segment) + 100000)

  final def isRealisticRange(range: SampleRangeSpecifier): Boolean = {
    val seg = range.getRealSegment(this)
    val ran = range.getSampleRangeReal(this)
    isRealisticFrsg(ran.start, seg) && isRealisticFrsg(ran.last, seg)
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="Time specification: conversion between frame/segment and Ts">

  private final lazy val factorTsPerFr = sampleInterval * 1000000D
  private final lazy val factorFrPerTs = 1D/factorTsPerFr

  final def convertFrToTs(frame:Int): Long = {
    errorIfMultipleSegments("convertFrToTs(frame: Int)", "convertFrsgToTs(frame: Int, segment: Int)")
    convertFrsgToTs(frame, 0)
  }

  /** Absolute timestamp of the given data frame index (in microseconds).
    */
  final def convertFrsgToTs(frame:Int, segment: Int): Long = {
    loggerRequire( isValidFrsg(frame, segment), "Not valid frame/segment specification!" )
    segmentStartTss(segment) + ((frame/*-1*/).toDouble * factorTsPerFr).round
  }

  /** Closest frame/segment index to the given absolute timestamp. Will give frames which are out of range (i.e. negative, etc)
    * if necessary.
    *
    * @param timestamp in Long
    * @return
    */
  final def convertTsToFrsg(timestamp: Long): (Int, Int) = {

    var tempret: (Int, Int) = (0 , 0)
    var changed = false
    def convertImpl(startTs: Long) = ((timestamp-startTs).toDouble * factorFrPerTs- 0.00001).round.toInt

    //timestamp is before the start of the first segment
    if( timestamp <= segmentStartTss(0) ){
      tempret = ( convertImpl(segmentStartTss(0)), 0)
    } else {
      //loop through segments to find appropriate segment which (contains) given timestamp
      var seg = 0
      while(seg < segmentCount - 1 && !changed ){
        if( timestamp <= segmentEndTss(seg) ){
          // if the timestamp is smaller than the end of the current segment, it fits in the current segment
          tempret = ( convertImpl(segmentStartTss(seg)), seg)
          changed = true
        } else if( timestamp < segmentStartTss(seg+1) ) {
          //The timestamp is between the end of the current segment and the beginning of the next segment...
          if( timestamp - segmentEndTss(seg) < segmentStartTss(seg+1) - timestamp){
            //  ...timestamp is closer to end of current segment than beginning of next segment
            tempret = ( convertImpl(segmentEndTss(seg)), seg)
            changed = true
          } else {
            //  ...timestamp is closer to beginning of next segment than end of current segment
            tempret = ( convertImpl(segmentStartTss(seg + 1)), seg + 1)
            changed = true
          }
        } else {
          //go on to next segment
          seg += 1
        }
      }

      //deal with the lastValid segment separately
      if( !changed ){
        if(timestamp <= segmentEndTss(segmentCount -1)){
          // if the timestamp is smaller than the end of the current segment, it fits in the current segment
          tempret = ( convertImpl(segmentStartTss(segmentCount-1)), segmentCount - 1 )
        } else {
          // if the timestamp is larger than the end of the lastValid segment
          tempret = ( convertImpl(segmentEndTss(segmentCount-1)), segmentCount - 1 )
        }
      }

    }

    tempret
  }
  final def convertTsToFrsgArray(timestamp: Long): Array[Int] = {
    val tempret = convertTsToFrsg(timestamp)//, false)
    Array[Int]( tempret._1, tempret._2 )
  }
  final def convertTsToFr(timestamp: Long): Int = {
    errorIfMultipleSegments("convertTsToFr", "convertTsToFrsg")
    convertTsToFrsg(timestamp)._1
  }

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc="Time specification: conversion between frame/segment and ms">

  /** Time of the given data frame and segment (in milliseconds, with t=0 being the time for frame 0 within the segment).
    */
  final def convertFrToMs(frame: Int): Double = {
    frame.toDouble * sampleInterval * 1000d
    //(frameSegmentToTS(frame, segment)-frameSegmentToTS(0, segment)).toDouble / 1000d
  }
  final def convertFrToMs(frame: Double): Double = convertFrToMs(round(frame).toInt)

  /** Closest frame/segment index to the given timestamp in ms (frame 0 within segment being time 0). Will give beginning or lastValid frames, if timestamp is
    * out of range.
    */
  final def convertMsToFr(ms: Double): Int = (ms*sampleRate*0.001).toInt

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc="Time specification: conversion between ts and ms">

  final def convertTsToMs(timestamp: Long): Double = convertFrToMs( convertTsToFr(timestamp) )
  final def convertMsToTs(ms: Double): Long = convertFrToTs( convertMsToFr(ms) )

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc="Time specification: convertTsToClosestSegment">

  /** Closest segment index to the given timestamp.
    */
  final def convertTsToClosestSegment(timestamp: Long): Int = {
    if(timestamp <= segmentStartTss(0) ){
      0
    } else {
      var tempret = -1
      var seg = 0
      while(seg < segmentCount - 1 && tempret == -1){
        if( timestamp < segmentEndTss(seg) ){
          tempret = seg
        } else if(timestamp < segmentStartTss(seg+1)) {
          tempret = if(timestamp - segmentEndTss(seg) < segmentStartTss(seg+1) - timestamp) seg else seg + 1
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

  override def isCompatible(that: NNElement): Boolean = {
    that match {
      case x: NNDataTiming => {
        (this.segmentCount == x.segmentCount) &&
          //ToDo 2: removed for corrupt page drop at end, like E04LC. Add better error code and tests
          //(this.segmentLength.corresponds(x.segmentLength)(_ == _ )) &&
          (this.segmentStartTss.corresponds(x.segmentStartTss)(_ == _ )) &&
          (this.sampleRate == x.sampleRate)
      }
      case _ => false
    }
  }

  override def toString(): String =
    this.getClass.getName + s"(fs=$sampleRate, segmentCount=$segmentCount, $gitHeadShort)"

  override def toStringFull(): String = {
    var tempout = toString()+ "\n"    +
    "   seg#\t\tsegLen\t\tsegStartTs\t\t(Internal)\n"

    for( seg <- 0 until segmentCount) {
      tempout = tempout + s"   $seg\t\t	${segmentLength(seg)}\t" +
        s"${segmentStartFileTimestamps(seg)}\t${segmentStartTss(seg)}\n"
    }
    tempout.dropRight(1)
  }


}

//object NNDataTiming {
//  def apply( sampleRate: Double, segmentLengths: Array[Int], segmentStartTs: Array[Long] ): NNDataTiming =
//    new NNDataTiming( sampleRate, segmentLengths, segmentStartTs )
//  def singleSegment( sampleRate: Double, segmentLength: Int, startTs: Long ): NNDataTiming =
//    new NNDataTiming( sampleRate, Array[Int](segmentLength), Array[Long](startTs) )
//  def singleSegment( sampleRate: Double, segmentLength: Int ): NNDataTiming = singleSegment( sampleRate, segmentLength, 0L)
//}

//trait NNDataTimingImmutable extends NNDataTiming {
//
////  val segmentLengths: Array[Int]
////  override final def segmentLengthImpl(segment: Int) = segmentLengths(segment)
////  override final lazy val segmentCount: Int = segmentLengths.length
//
////  println("XFramesImmutable segmentLength " + segmentLength.toVector.toString)
////  println("XFramesImmutable segmentCount " + segmentCount)
//
//  override val segmentStartTs: Array[Long]
//
//}


//  // <editor-fold defaultstate="collapsed" desc="Sample Rate: sampleRate/sampleInterval/tsPerFr/frPerTs">
//
//  /**OVERRIDE: Sampling rate of frame data in Hz
//    */
//  def sampleRate: Double
//  /**Buffered inverse of sampling, in seconds: Double.
//    *DO NOT OVERRIDE: not final due to override with lazy val in immutable frames.
//    */
//  def sampleInterval = 1.0/sampleRate
//  /**Buffered timestamps (microseconds) between frames.
//    *DO NOT OVERRIDE: not final due to override with lazy val in immutable frames.
//    */
//  def factorTSperFR = sampleInterval * 1000000D
//  /**Buffered frames between timestamps (microseconds).
//    *DO NOT OVERRIDE: not final due to override with lazy val in immutable frames.
//    */
//  def factorFRperTS = 1D/factorTSperFR
//
//  // </editor-fold>