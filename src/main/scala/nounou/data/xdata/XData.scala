package nounou.data

import nounou._
import scala.collection.immutable.Vector
import nounou.data.traits._
import breeze.linalg.{DenseVector => DV}
import nounou.data.ranges.{SampleRangeValid, SampleRangeAll, SampleRangeSpecifier}

/** Base class for data encoded as Int arrays.
  * This object is mutable, to allow inheritance by [[nounou.data.filters.XDataFilter]].
  * For that class, output results may change, depending upon _parent changes.
  * Each trace of data must share the following variables:
  * sampling, start, length, xBits, absGain, absOffset, absUnit
  */
abstract class XData extends X with XConcatenatable with XDataTiming with XChannels with XDataScale {

  override def toString(): String = "XData: " + channelCount + " ch, "+ segmentCount + " seg, length=" + segmentLength + ", fs=" + sampleRate + ")"

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
    * and display functions which have an internal state to update.
    * Always remember to start with super.changedData(), in order to
    * map to all children.
    */
  def changedData(): Unit = _children.map(_.changedData())
  /** Must be overriden and expanded, especially by buffering functions
    * and display functions which have an internal state to update.
    * Always remember to start with super.changedData(Int), in order to
    * map to all children.
    */
  def changedData(channel: Int): Unit = _children.map(_.changedData(channel))
  /** Must be overriden and expanded, especially by buffering functions
    * and display functions which have an internal state to update.
    * Always remember to start with super.changedData(Array[Int]), in order to
    * map to all children.
    */
  def changedData(channels: Array[Int]): Unit = _children.map(_.changedData(channels))
  /** Must be overriden and expanded, especially by buffering functions
    * and display functions which have an internal state to update.
    * Covers sampleRate, segmentLengths, segmentEndTSs, segmentStartTSs, segmentCount
    * Always remember to start with super.changedTiming(), in order to
    * map to all children.
    */
  def changedTiming(): Unit = _children.map(_.changedTiming())
  /** Must be overriden and expanded, especially by buffering functions
    * and display functions which have an internal state to update.
    * Always remember to start with super.changedLayout(), in order to
    * map to all children.
    */
  def changedLayout(): Unit = _children.map(_.changedLayout())

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" XLayout related ">

  def layout(): XLayout
  //ToDo 2: make channelNames, etc, Array[] methods? Depends on mutability...
  override def channelNames() = layout().channelNames
  override final def channelNamesA() = layout().channelNamesA
  //channelCount???
  // </editor-fold>


  // <editor-fold defaultstate="collapsed" desc=" readXXX ">

  //<editor-fold defaultstate="collapsed" desc="reading a point">

  /** Read a single point from the data, in internal integer scaling, after checking values.
    * Implement via readPointImpl. Prefer [[readTrace()]] and [[readFrame()]]
    * when possible, as these will avoid repeated function calling overhead.
    */
  def readPoint(channel: Int, frame: Int, segment: Int /*optSegment: OptSegment*/): Int = {
    require(isRealisticFrsg(frame, getRealSegment(segment)), "Unrealistic frame/segment: " + frame.toString)
    require(isValidChannel(channel), "Invalid channel: " + channel.toString)

    if( isValidFrsg(frame, getRealSegment(segment)) ) readPointImpl(channel, frame, getRealSegment(segment)) else 0
  }
  //final def readPoint(channel: Int, frame: Int, segment: Int): Int = readPoint(channel, frame, OptSegment(segment))
  final def readPoint(channel: Int, frame: Int): Int = readPoint(channel, frame, 1)//OptSegmentAutomatic)

  // <editor-fold defaultstate="collapsed" desc=" convenience readPoint variations ">

  /** [[readPoint()]] but in physical units.
    */
  final def readPointAbs(channel: Int, frame: Int, segment: Int): Double = convertINTtoABS( readPoint(channel, frame, segment) )
  //final def readPointAbs(channel: Int, frame: Int, optSegment: OptSegment): Double = convertINTtoABS( readPoint(channel, frame, optSegment) )
  final def readPointAbs(channel: Int, frame: Int): Double = readPointAbs(channel, frame, -1)//OptSegmentAutomatic)

  // </editor-fold>

  //</editor-fold>

  /** MUST OVERRIDE: Read a single point from the data, in internal integer scaling.
    */
  protected[data] def readPointImpl(channel: Int, frame: Int, segment: Int): Int

  //<editor-fold defaultstate="collapsed" desc="reading a trace">

  /**  CAN OVERRIDE: Read a single trace from the data, in internal integer scaling.
    */
  def readTrace(channel: Int, range: SampleRangeSpecifier): DV[Int] = {

    loggerRequire(isRealisticRange(range), "Unrealistic frame/segment: " + range.toString)
    loggerRequire(isValidChannel(channel), "Invalid channel: " + channel.toString)

    val preValidPost = range.getSampleRangeValidPrePost(this)
    readTracePVPImpl(channel, preValidPost)

  }
  /** CAN OVERRIDE:
    *
    */
  def readTrace(channels: Array[Int],  range: SampleRangeSpecifier): Array[DV[Int]] = {
    val preValidPost = range.getSampleRangeValidPrePost(this)
    channels.map( readTracePVPImpl(_, preValidPost) )
  }

  private final def readTracePVPImpl(channel: Int, preValidPost: (Int, SampleRangeValid, Int)): DV[Int] = {
    val validTrace = readTraceImpl(channel, preValidPost._2)
    preValidPost match {
      case (0, rfv, 0) => validTrace
      case (pre, rfv, 0) => DV.vertcat(DV.zeros[Int](pre), validTrace)
      case (0, rfv, post) => DV.vertcat(validTrace, DV.zeros[Int](post))
      case (pre, rfv, post) => DV.vertcat(DV.zeros[Int](pre), validTrace, DV.zeros[Int](post))
    }
  }


  // <editor-fold defaultstate="collapsed" desc=" convenience readTrace variations ">

  /** Read a single trace in internal integer scaling.
    */
  final def readTrace(channel: Int): DV[Int] = readTrace(channel, SampleRangeAll())

  /** Read a single trace in absolute unit scaling (as recorded).
    */
  final def readTraceAbs(channel: Int): DV[Double] =                                                 convertINTtoABS(readTrace(channel))
  final def readTraceAbs(channel: Int,         range: SampleRangeSpecifier): DV[Double] =                convertINTtoABS(readTrace(channel, range))
  final def readTraceAbs(channels: Array[Int], range: SampleRangeSpecifier): Array[DV[Double]] =         readTrace(channels, range).map( convertINTtoABS(_) )


  final def readTraceA(channel: Int) =                                                               readTrace(channel).toArray
  final def readTraceA(channel: Int,           range: SampleRangeSpecifier) =                            readTrace(channel, range).toArray
  final def readTraceA(channels: Array[Int],   range: SampleRangeSpecifier): Array[Array[Int]] =         readTrace(channels, range).map(_.toArray)
  final def readTraceA(channel: Int,           range: Array[Int]): Array[Int] =                readTraceA(channel, convertARRtoRANGE(range))
//  final def readTraceA(channels: Array[Int], range: Array[Int]): Array[Array[Int]] =           readTraceA(channels, convertARRtoRANGE(range))

  final def readTraceAbsA(channel: Int): Array[Double] = readTraceAbs(channel).toArray
  final def readTraceAbsA(channel: Int,         range: SampleRangeSpecifier): Array[Double] =                readTraceAbs(channel, range).toArray
//  final def readTraceAbsA(channels: Array[Int], range: SampleRangeSpecifier): Array[Array[Double]] =         readTraceAbs(channels, range).map(_.toArray)
  final def readTraceAbsA(channel: Int,         range: Array[Int]): Array[Double] =                readTraceAbsA(channel, convertARRtoRANGE(range))
//  final def readTraceAbsA(channels: Array[Int], range: Array[Int]): Array[Array[Double]] =         readTraceAbsA(channels, convertARRtoRANGE(range))

//  private def convertARRtoRANGE(array: Array[Int]): SampleRangeSpecifier = {
//    loggerRequire(array != null, "Input array cannot be null!")
//    array.length match {
//      case 0 => SampleRangeAll()
//      //case 1 => RangeFrAll(array(0))
//      case 2 => SampleRange(array(0), array(1))
//      case 3 => SampleRange(array(0), array(1), array(2))
//    }
//  }

  //</editor-fold>

  /** CAN OVERRIDE: Read a single data trace from the data, in internal integer scaling.
    * Should return a defensive clone. Assumes that channel and range are within the data range!
    */
  def readTraceImpl(channel: Int, rangeFrValid: SampleRangeValid/*range: Range.Inclusive, segment: Int*/): DV[Int] = {
    val res = DV.zeros[Int]( rangeFrValid.length )
    nounou.util.forJava(rangeFrValid.start, rangeFrValid.last + 1, rangeFrValid.step, (c: Int) => (res(c) = readPointImpl(channel, c, rangeFrValid.segment)))
    res
  }

//  //<editor-fold defaultstate="collapsed" desc="reading a frame">
//
//  /** Read a single frame from the data, in internal integer scaling, for just the specified channels.
//    */
//  final def readFrame(frame: Int, channels: Array[Int], segment: Int): DV[Int] = {
//    loggerRequire(isRealisticFr(frame/*, segment*/), "Unrealistic frame/segment: " + (frame, segment).toString)
//    loggerRequire(channels.forall(isValidChannel), "Invalid channels: " + channels.toString)
//
//    if( isValidFr(frame/*, segment*/) ) readFrameImpl(frame, channels/*, segment*//*(currentSegment = segment)*/ ) else DV.zeros[Int]( channels.length )
//
//  }
//
//  /** Read a single frame from the data, in internal integer scaling.
//    */
//  final def readFrame(frame: Int, segment: Int): DV[Int] = {
//    loggerRequire(isRealisticFr(frame, segment), "Unrealistic frame/segment: " + (frame, segment).toString)
//    if( isValidFr(frame, segment) ) readFrameImpl(frame , segment ((currentSegment = segment)*/ ) else DV.zeros[Int]( channelCount )
//  }
////  final def readFrame(frame: Int): DV[Int] = readFrame(frame, 0)
//
//  final def readFrameA(frame: Int): Array[Int] = readFrame(frame).toArray
//  final def readFrameA(frame: Int, segment: Int): Array[Int] = readFrame(frame/*, segment*/).toArray
//  final def readFrameA(frame: Int, channels: Array[Int], segment: Int): Array[Int] = readFrame(frame, channels, segment).toArray
//
//  //</editor-fold>
//
//  /** CAN OVERRIDE: Read a single frame from the data, in internal integer scaling.
//    * Should return a defensive clone. Assumes that frame is within the data range!
//    */
//  def readFrameImpl(frame: Int): DV[Int] = {
//    val res = DV.zeros[Int](channelCount)
//    nounou.util.forJava(0, channelCount, 1, (channel: Int) => res(channel) = readPointImpl(channel, frame))
//    res
//  }
//  /** CAN OVERRIDE: Read a single frame from the data, for just the specified channels, in internal integer scaling.
//    * Should return a defensive clone. Assumes that frame and channels are within the data range!
//    */
//  def readFrameImpl(frame: Int, channels: Array[Int]): DV[Int] = {
//    val res = DV.zeros[Int]( channels.length )
//    nounou.util.forJava(0, channels.length, 1, (channel: Int) => res(channel) = readPointImpl(channel, frame))
//    res
//  }
//
//  // </editor-fold>

  // </editor-fold>


  // <editor-fold defaultstate="collapsed" desc="XConcatenatable">

  override def isCompatible(that: X): Boolean = {
    that match {
      case x: XData => {
        (super[XDataTiming].isCompatible(x)) && (super[XDataScale].isCompatible(x)) && this.layout.isCompatible(x.layout)
        //not channel info
      }
      case _ => false
    }
  }

  override def :::(x: X): XData

  // </editor-fold>

}




///** Immutable version of XData.
//  *
//  * Must override the following:
//  * +  def readPointImpl(channel: Int, frame: Int, segment: Int): Int
//  * +  (from XFramesImmutable)
//  * ++   val segmentCount: Int
//  * ++   val length: : Vector[Int]
//  * ++   val segmentStartTSs: Vector[Long]
//  * ++   val sampleRate: Double
//  * +  (from XChannelsImmutable)
//  * ++   val channelNames: Vector[String]
//  * +  (from XAbsoluteImmutable)
//  * ++   val absGain: Double
//  * ++   val absOffset: Double
//  * ++   val absUnit: String
//  *
//  * Can override the following:
//  * +   def readTraceImpl(channel: Int, segment: Int): Vector[Int]
//  * +   def readTraceImpl(channel: Int, span:Span, segment: Int): Vector[Int]
//  * +   def readFrameImpl(frame: Int, segment: Int): Vector[Int]
//  * +   def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int): Vector[Int]
//  * +  (from XAbsoluteImmutable)
//  * ++   val xBits: Int = 1024
//  */
//abstract class XDataImmutable extends XData with XFramesImmutable with XChannelsImmutable with XAbsoluteImmutable {
//
//
//  //ToDo 4: channelNames as lazy val?
//
//  // <editor-fold defaultstate="collapsed" desc=" DataSource related ">
//
//  override def changedData(): Unit = logger.error("this is an immutable data source, and changedData() should not be invoked!")
//  override def changedData(channel: Int): Unit = logger.error("this is an immutable data source, and changedData() should not be invoked!")
//  override def changedData(channels: Vector[Int]): Unit = logger.error("this is an immutable data source, and changedData() should not be invoked!")
//  override def changedTiming(): Unit = logger.error("this is an immutable data source, and changedTiming() should not be invoked!")
//
//  // </editor-fold>
//
//}


class XDataNull extends XData {
  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = 0
  override val absGain: Double = 1D
  override val absOffset: Double = 0D
  override val absUnit: String = "Null unit"
  override val scaleMax: Int = 0
  override val scaleMin: Int = 0
  override val segmentLength: Array[Int] = Array[Int]()
  override val segmentStartTs: Array[Long] = Array[Long]()
  override val sampleRate: Double = 1d
  override val layout: XLayout = XLayoutNull

  override def :::(x: X): XData = x match {
    case XDataNull => this
    case xData: XData => xData
    case xDataChannel: XDataChannel => new XDataChannelArray( Vector(xDataChannel) )
    case _ => require(false, "cannot append incompatible data types (XDataNull)"); this
  }
  override def toString() = "XDataNull()"

  /** Number of segments in data.
    */
  override val segmentCount: Int = 0

  /** OVERRIDE: End timestamp for each segment. Implement by overriding _endTimestamp
    */
  override val segmentEndTs: Array[Long] = Array[Long]()
}

object XDataNull extends XDataNull


class XDataAuxNull extends XDataNull with XDataAux {
  override def :::(x: X): XDataAux = x match {
    case xDataAux: XDataAux => xDataAux
    case _ => require(false, "cannot append incompatible data types (XDataAuxNull)"); this
  }
  override def toString() = "XDataAuxNull()"
}

object XDataAuxNull extends XDataAuxNull