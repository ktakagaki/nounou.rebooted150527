//package nounou.data.filters
//
////import nounou.OptSegment
//import nounou.data.XData
//import breeze.linalg.{DenseVector => DV, max, min}
//import nounou.data.ranges.RangeFr
////import scala.collection.immutable.VectorBuilder
////import scala.collection.mutable.{ArrayBuffer, HashMap}
//import nounou.data.filters.XDataFilterMinMaxAbs.{ABS, MIN, MAX}
//
//
//object XDataFilterMinMaxAbs {
//
//  val ABS = 0
//  val MIN = 1
//  val MAX = 2
//
//}
//
///**
// * @author ktakagaki
// * @date 2/18/14.
// */
//class XDataFilterMinMaxAbs( override val _parent: XData ) extends XDataFilterRMS( _parent ) {
//
//    override def toString() = {
//      if(halfWindow == 0) "XDataFilterMinMaxAbs: off (halfWindow=0)"
//      else "XDataFilterMinMaxAbs: halfWindow=" + halfWindow
//    }
//    override def changedTiming(): Unit = {
//      super.changedTiming()
//      //ToDo 2: how to deal with data changes?
//      //(_parent.sampleRate * 0.05).toInt //50 ms
//    }
//
//    protected var _mode: Int = ABS
//    def mode = _mode
//    def getMode = _mode
//    def mode_=(newValue: Int) : Unit = {
//      require(newValue >= 0 && newValue <= 2, logger.error("mode specification must be ABS=0, MIN=1, or MAX=2! It is{}", newValue.toString))
//      _mode = newValue
//      changedData()
//    }
//    def setMode(newValue: Int) = mode_=(newValue)
//
//    override def readPointImpl(channel: Int, frame: Int, segment: Int): Int =
//      if(halfWindow == 0){
//        _parent.readPointImpl(channel, frame)//, segment)
//      } else {
//        val tempdata = _parent.readTrace(channel, RangeFr(frame - halfWindow, frame + halfWindow))//, OptSegment(segment)))
//        mode match {
//          case ABS => absMinMax(tempdata) //scala.math.max( abs(max(tempdata)), abs(min(tempdata)) )
//          case MIN => min(tempdata)
//          case MAX => max(tempdata)
//          case _ => {logger.error("This should not happen!"); throw new IllegalArgumentException}
//        }
//      }
//
//    override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): DV[Int] =
//      if(halfWindow == 1){
//        _parent.readTraceImpl(channel, range, segment)
//      } else {
//        val trace = _parent.readTrace(channel, RangeFr(range.start - halfWindow, range.end + halfWindow, 1))//, OptSegment(segment)))
//        var tempret = DV[Int]() //= new VectorBuilder[Int]()
//            //tempret.sizeHint(range.length)
//        val window = 2*halfWindow
//        for( cnt <- 0 to (range.end-range.start) by range.step) {
//          val tempWindow = trace.slice(cnt, cnt + window)
////          val tempMax = if(mode != MIN) max( tempWindow ) else Int.MinValue
////          val tempMin = if(mode != MAX) min( tempWindow ) else Int.MaxValue
//          tempret += ( mode match {//.+=( mode match {
//            case ABS => absMinMax(tempWindow)//)scala.math.max( abs( max(tempWindow) ), abs( min(tempWindow) ) )
//            case MIN => min( tempWindow )
//            case MAX => max( tempWindow )
//            case _ => {logger.error("This should not happen!"); throw new IllegalArgumentException}
//          } )
//        }
//        tempret//.result()
//      }
//
//
//
////  private def min(vect: Vector[Int]): Int = {
////    var tempmin= Int.MaxValue
////    for( cnt <- 0 until vect.length){
////      if(vect(cnt)<tempmin) tempmin = vect(cnt)
////    }
////    tempmin
////  }
////  private def max(vect: Vector[Int]): Int = {
////    var tempmax= Int.MinValue
////    for( cnt <- 0 until vect.length){
////      if( vect(cnt) > tempmax ) tempmax = vect(cnt)
////    }
////    tempmax
////  }
//  //ToDo 3: put Into Breeze somehow
//  private def absMinMax(vect: DV[Int]): Int = {
//    var tempmax= Int.MinValue
//    for( cnt <- 0 until vect.length){
//      val tempAbs = scala.math.abs(vect(cnt))
//      if( tempAbs > tempmax ) tempmax = tempAbs
//    }
//    tempmax
//  }
//
//
//  //  private var buffer: HashMap[(Int, Int, Int), Vector[Int]] = new MinMaxHashMapBuffer()
////  private var garbageQue: ArrayBuffer[(Int, Int, Int)] = new ArrayBuffer[(Int, Int, Int)]()
////  lazy val bufferPageLength: Int = (32768 / 2) //default page length will be 32 kB
////  lazy val garbageQueBound: Int = 2048 //32MB in data + //1073741824 / 8 / (bufferPageLength * 2)  //default buffer maximum size will be 128 MB
////  class MinMaxHashMapBuffer extends HashMap[(Int, Int, Int), (Int, Int)] {
////
////    //do not use applyOrElse!
////    override def apply( key: (Int, Int, Int)  ): Vector[Int] = {
////      val index = garbageQue.indexOf( key )
////      if( index == -1 ){
////        if(garbageQue.size >= garbageQueBound ){
////          this.remove( garbageQue(1) )
////          garbageQue.drop(1)
////        }
////        garbageQue.append( key )
////        default( key )
////      }else{
////        garbageQue.remove( index )
////        garbageQue.append( key )
////        super.apply(key)
////      }
////    }
////
////    override def default( key: (Int, Int, Int)  ): Vector[Int] = {
////      val startFrame = key._2 * bufferPageLength
////      val endFramePlusOne: Int = scala.math.min( startFrame + bufferPageLength, segmentLengths( key._3 ) )
////      val returnValue = tempTraceReader( key._1, new Range.Inclusive(startFrame, endFramePlusOne, 1), key._3  )
////      this.+=( key -> returnValue )
////      returnValue
////    }
////  }
//
//
//
//    //  override def channelNames: scala.Vector[String] = _parent.channelNames
//
//    override def absUnit: String = _parent.absUnit +
//      (mode match {
//        case ABS => " (abs max)"
//        case MIN => " (min)"
//        case MAX => " (max)"
//      })
//
//    //ToDo 3: offset and gain aren't quite well established here! OK as long as offset is zero, but...
//    //  override def absOffset: Double = _parent.absOffset
//    //  override def absGain: Double = _parent.absGain
//
//    //override def sampleRate: Double = _parent.sampleRate
//
//    // override def segmentStartTSs: Vector[Long] = _parent.segmentStartTSs
//    //override def segmentEndTSs: Vector[Long]
//
//    //override def segmentLengths: Vector[Int]
//
//    //  override def segmentCount: Int = _parent.segmentCount
//
//  }