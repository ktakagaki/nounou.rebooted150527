package nounou.data

import nounou._
import scala.collection.immutable.Vector
import nounou.data.traits._
import breeze.linalg.{DenseVector => DV}

/** Base class for data encoded as Int arrays.
  * This object is mutable, to allow inheritance by [[nounou.data.filters.XDataFilter]].
  * For that class, output results may change, depending upon upstream changes.
  * Each trace of data must share the following variables:
  * sampling, start, length, xBits, absGain, absOffset, absUnit
  */
abstract class XData extends X with XConcatenatable with XFrames with XChannels with XAbsolute {

  override def toString(): String = "XData: " + channelCount + " ch, "+ segmentCount + " seg, lengths=" + segmentLengths + ", fs=" + sampleRate + ")"

  /** Provides a textual representation of the child hierarchy starting from this data object.
    * If multiple XDataFilter objects (e.g. an XDataFilterFIR object) is chained after this data,
    * this method will show the chained objects and their tree hierarchy.
    * @return
    */
  final def toStringChain(): String = {
    toString() + (if(getChildren.size!=0) {
      getChildren.map(p => "\n\\ " + p.toStringChain).reduce( _ + _ ).split("\n").flatMap( "\n     " + _ ).mkString.drop(1)
    } else {
      ""
    })
  }

  // <editor-fold defaultstate="collapsed" desc=" DataSource related ">

  private val _children = scala.collection.mutable.Set[XData]()
  def setChild(x: XData): Unit = _children.+=(x)
  final def setChildren(xs: TraversableOnce[XData]): Unit = xs.map( setChild(_) )
  def getChildren() = _children
  def clearChildren(): Unit = _children.clear()
  def clearChild(x: XData): Unit = _children.-=(x)
  final def clearChildren(xs: TraversableOnce[XData]): Unit = xs.map( clearChild(_) )

  /** Must be overriden and expanded, especially by buffering functions
    * and functions which have an active update which must be updated.
    */
  def changedData(): Unit = for( child <- getChildren ) child.changedData()
  /** Must be overriden and expanded, especially by buffering functions
    * and functions which have an active update which must be updated.
    */
  def changedData(channel: Int): Unit = for( child <- getChildren ) child.changedData(channel)
  def changedData(channels: Vector[Int]): Unit = for( channel <- channels ) changedData(channel)
  /** Must be overriden and expanded, especially by buffering functions
    * and functions which have an active update which must be updated.
    * Covers sampleRate, segmentLengths, segmentEndTSs, segmentStartTSs, segmentCount
    */
  def changedTiming(): Unit = for( child <- getChildren ) child.changedTiming()

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" XLayout related ">

  def layout(): XLayout
  override final def channelNames() = layout().channelNames
  override final def channelNamesA() = layout().channelNamesA
  //channelCount???
  // </editor-fold>



  // <editor-fold defaultstate="collapsed" desc=" READING ACTUAL DATA ">

  //<editor-fold defaultstate="collapsed" desc="reading a point">
  /** Read a single point from the data, in internal integer scaling, after checking values.
    * Implement via readPointImpl.
    */
  final def readPoint(channel: Int, frame: Int, segment: Int): Int = {

    require(isRealisticFrame(frame, segment), "Unrealistic frame/segment: " + (frame, segment).toString)
    require(isValidChannel(channel), "Invalid channel: " + channel.toString)
    //require(isValidFrame(frame, segment), "Invalid frame/segment: " + (frame, segment).toString)
    if( isValidFrame(frame, segment) ) readPointImpl(channel, frame, currentSegment = segment) else 0
  }

  /** Read a single point from current segment (or segment 0 if not initialized), in internal integer scaling.
    */
  final def readPoint(channel: Int, frame: Int): Int = readPoint(channel, frame, currentSegment)
  /** Read a single point from the data, in absolute unit scaling (as recorded).
    */
  final def readPointAbs(channel: Int, frame: Int, segment: Int): Double = toAbs(readPoint(channel, frame, segment))
  /** Read a single point from current segment (or segment 0 if not initialized), in absolute unit scaling (as recorded).
    */
  final def readPointAbs(channel: Int, frame: Int): Double = toAbs(readPoint(channel, frame))
  //</editor-fold>

  /** MUST OVERRIDE: Read a single point from the data, in internal integer scaling.
    */
  def readPointImpl(channel: Int, frame: Int, segment: Int): Int


  //<editor-fold defaultstate="collapsed" desc="reading a trace">

  /** Read a single trace from current segment (or segment 0 if not initialized), in internal integer scaling.
    */
  final def readTrace(channel: Int): DV[Int] = readTrace(channel, FrameRange.all, currentSegment)
  final def readTraceA(channel: Int) = readTrace(channel).toArray

  /** Read a single trace (within the span) from current segment (or segment 0 if not initialized), in internal integer scaling.
    */
  final def readTrace(channel: Int, range: FrameRange): DV[Int] = readTrace(channel, range, currentSegment)
  final def readTraceA(channel: Int, range: FrameRange) = readTrace(channel, range).toArray

  /** Read a single trace from the data, in internal integer scaling.
    */
  final def readTrace(channel: Int, range: FrameRange, segment: Int): DV[Int] = {

    require(isRealisticFrameRange(range, segment), "Unrealistic frame/segment: " + (range, segment).toString)
    require(isValidChannel(channel), "Invalid channel: " + channel.toString)

    //val realRange = range.getRangeWithoutNegativeIndexes( segmentLengths(segment) )
//    require(realRange.max < segmentLengths(segment), "Span is out of range, realRange.max >= segmentLengths(segment)!")
//    require(realRange.min >= 0, "Span is out of range, realRange.min < 0 !")
    val totalLength =  segmentLengths(segment)
        val preLength = range.preLength( totalLength )
        val postLength = range.postLength( totalLength )

    val vr = range.getValidRange(totalLength)
    val tempData: DV[Int] = if( vr.length == 0 ) DV[Int]() else readTraceImpl(channel, vr, (currentSegment = segment))

    DV.vertcat( DV.zeros[Int]( preLength ), tempData, DV.zeros[Int]( postLength ) )

  }
  final def readTraceA(channel: Int, range: FrameRange, segment: Int) = readTrace(channel, range, segment).toArray

//  final def readTraceA(channel: Int, range: FrameRange): Array[Int] = readTrace(channel, range).toArray
//  final def readTraceA(channel: Int, range: FrameRange, segment: Int): Array[Int] = readTrace(channel, range, segment).toArray

  /** Read a single trace from the data, in internal integer scaling.
    */
  final def readTraceFromTS(channel: Int, ts: Long, preFrames: Int = 0, postFrames: Int = 0, readOOB: Boolean = true): DV[Int] = {
    require(isValidChannel(channel), "Invalid channel: " + channel.toString)

    val keyFrame = tsToFrameSegment(ts, negativeIfOOB = true)

    if( readOOB ){
      if( keyFrame._1 - postFrames < 0 || keyFrame._1 + postFrames >= segmentLengths( keyFrame._2 ) ) null
           //readTraceImpl will only read within range
      else readTraceImpl( channel, (keyFrame._1 - postFrames) to (keyFrame._1 + postFrames), keyFrame._2 )
    } else {
      //readTrace pads with zeros if out of range
      readTrace( channel, (keyFrame._1 - postFrames) to (keyFrame._1 + postFrames), keyFrame._2 )
    }

  }

  /** Read a single trace from current segment (or segment 0 if not initialized),  in absolute unit scaling (as recorded).
    */
  final def readTraceAbs(channel: Int): DV[Double] = toAbs(readTrace(channel))

  /** Read a single trace (within the span) from current segment (or segment 0 if not initialized), in absolute unit scaling (as recorded).
    */
  final def readTraceAbs(channel: Int, range: FrameRange = FrameRange.all): DV[Double] = toAbs(readTrace(channel, range))

  /** Read a single trace (within the span) from the data, in absolute unit scaling (as recorded).
    */
  final def readTraceAbs(channel: Int, range: FrameRange, segment: Int): DV[Double] = toAbs(readTrace(channel, range, segment))

  final def readTraceAbsA(channel: Int): Array[Double] = readTraceAbs(channel).toArray
  final def readTraceAbsA(channel: Int, range: FrameRange): Array[Double] = readTraceAbs(channel, range).toArray
  final def readTraceAbsA(channel: Int, range: FrameRange, segment: Int): Array[Double] = readTraceAbs(channel, range, segment).toArray

  //</editor-fold>

  /** CAN OVERRIDE: Read a single data trace from the data, in internal integer scaling.
    * Should return a defensive clone. Assumes that channel and range are within the data range!
    */
  def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): DV[Int] = {
    val res = DV.zeros[Int]( range.length )
    forJava(range.start, range.end + 1, range.step, (c: Int) => (res(c) = readPointImpl(channel, c, segment)))
    res
  }

  //<editor-fold defaultstate="collapsed" desc="reading a frame">

  /** Read a single frame from the data, in internal integer scaling, for just the specified channels.
    */
  final def readFrame(frame: Int, channels: Vector[Int], segment: Int): DV[Int] = {
    loggerRequire(isRealisticFrame(frame, segment), "Unrealistic frame/segment: " + (frame, segment).toString)
    loggerRequire(channels.forall(isValidChannel), "Invalid channels: " + channels.toString)

    if( isValidFrame(frame, segment) ) readFrameImpl(frame, channels, (currentSegment = segment) ) else DV.zeros[Int]( channels.length )

  }

  /** Read a single frame from the data, in internal integer scaling.
    */
  final def readFrame(frame: Int, segment: Int): DV[Int] = {
    loggerRequire(isRealisticFrame(frame, segment), "Unrealistic frame/segment: " + (frame, segment).toString)
    if( isValidFrame(frame, segment) ) readFrameImpl(frame, (currentSegment = segment) ) else DV.zeros[Int]( channelCount )
  }
  final def readFrame(frame: Int): DV[Int] = readFrame(frame, 0)

  final def readFrameA(frame: Int): Array[Int] = readFrame(frame).toArray
  final def readFrameA(frame: Int, segment: Int): Array[Int] = readFrame(frame, segment).toArray
  final def readFrameA(frame: Int, channels: Array[Int], segment: Int): Array[Int] = readFrame(frame, channels.toVector, segment).toArray

  //</editor-fold>

  /** CAN OVERRIDE: Read a single frame from the data, in internal integer scaling.
    * Should return a defensive clone. Assumes that frame is within the data range!
    */
  def readFrameImpl(frame: Int, segment: Int): DV[Int] = {
    val res = DV.zeros[Int](channelCount)//new Array[Int](channelCount)
    forJava(0, channelCount, 1, (channel: Int) => res(channel) = readPointImpl(channel, frame, segment))
    res
  }
  /** CAN OVERRIDE: Read a single frame from the data, for just the specified channels, in internal integer scaling.
    * Should return a defensive clone. Assumes that frame and channels are within the data range!
    */
  def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int): DV[Int] = {
    val res = DV.zeros[Int]( channels.length )
    forJava(0, channels.length, 1, (channel: Int) => res(channel) = readPointImpl(channel, frame, segment))
    res
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="XConcatenatable">

  override def isCompatible(that: X): Boolean = {
    that match {
      case x: XData => {
        (super[XFrames].isCompatible(x)) && (super[XAbsolute].isCompatible(x)) && this.layout.isCompatible(x.layout)
        //not channel info
      }
      case _ => false
    }
  }

  override def :::(x: X): XData

  // </editor-fold>

}




/** Immutable version of XData.
  *
  * Must override the following:
  * +  def readPointImpl(channel: Int, frame: Int, segment: Int): Int
  * +  (from XFramesImmutable)
  * ++   val segmentCount: Int
  * ++   val length: : Vector[Int]
  * ++   val segmentStartTSs: Vector[Long]
  * ++   val sampleRate: Double
  * +  (from XChannelsImmutable)
  * ++   val channelNames: Vector[String]
  * +  (from XAbsoluteImmutable)
  * ++   val absGain: Double
  * ++   val absOffset: Double
  * ++   val absUnit: String
  *
  * Can override the following:
  * +   def readTraceImpl(channel: Int, segment: Int): Vector[Int]
  * +   def readTraceImpl(channel: Int, span:Span, segment: Int): Vector[Int]
  * +   def readFrameImpl(frame: Int, segment: Int): Vector[Int]
  * +   def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int): Vector[Int]
  * +  (from XAbsoluteImmutable)
  * ++   val xBits: Int = 1024
  */
abstract class XDataImmutable extends XData with XFramesImmutable with XChannelsImmutable with XAbsoluteImmutable {


  //ToDo 4: channelNames as lazy val?
  // <editor-fold defaultstate="collapsed" desc=" DataSource related ">

  override def changedData(): Unit = logger.error("this is an immutable data source, and changedData() should not be invoked!")
  override def changedData(channel: Int): Unit = logger.error("this is an immutable data source, and changedData() should not be invoked!")
  override def changedData(channels: Vector[Int]): Unit = logger.error("this is an immutable data source, and changedData() should not be invoked!")
  override def changedTiming(): Unit = logger.error("this is an immutable data source, and changedTiming() should not be invoked!")

  // </editor-fold>

}


abstract class XDataNullImpl extends XDataImmutable {
  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = 0
  override val absGain: Double = 1D
  override val absOffset: Double = 0D
  override val absUnit: String = "Null unit"
  override val scaleMax: Int = 0
  override val scaleMin: Int = 0
  override val segmentLengths: Vector[Int] = Vector[Int]()
  override val segmentStartTSs: Vector[Long] = Vector[Long]()
  override val sampleRate: Double = 1d
  override val layout: XLayout = XLayoutNull
}

object XDataNull extends XDataNull

class XDataNull extends XDataNullImpl {
  override def :::(x: X): XDataImmutable = x match {
    case XDataNull => this
    case _ => require(false, "cannot append incompatible data types (XDataNull)"); this
  }
  override def toString() = "XDataNull()"
}

object XDataAuxNull extends XDataNullImpl with XDataAux {
  override def :::(x: X): XDataImmutable = x match {
    case XDataAuxNull => this
    case _ => require(false, "cannot append incompatible data types (XDataAuxNull)"); this
  }
  override def toString() = "XDataAuxNull()"
}
