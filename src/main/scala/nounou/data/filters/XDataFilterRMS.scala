//package nounou.data.filters
//
////import nounou.OptSegment
//import nounou.data.XData
//import breeze.signal.rootMeanSquare
//import breeze.linalg.{DenseVector => DV}
////import breeze.numerics.sqrt
//import nounou.data.ranges.RangeFr
//
///**
// * @author ktakagaki
// * @date 2/18/14.
// */
//class XDataFilterRMS( override val _parent: XData ) extends XDataFilter( _parent ) {
//
//    override def toString() = {
//      if(halfWindow == 0) "XDataFilterRMS: off (halfWindow=0)"
//      else "XDataFilterRMS: halfWindow=" + halfWindow
//    }
//    override def changedTiming(): Unit = {
//      super.changedTiming()
//      //ToDo 2: how to deal with data changes?
//      //(_parent.sampleRate * 0.05).toInt //50 ms
//    }
//
//    protected var _halfWindow: Int = 0
//    def halfWindow = _halfWindow
//    def getHalfWindow = _halfWindow
//    def halfWindow_=(newValue: Int) : Unit = {
//      require(halfWindow >= 0, logger.error("half window must be zero or positive!"))
//      _halfWindow = newValue
//      changedData()
//    }
//    def setHalfWindow(newValue: Int) = halfWindow_=(newValue)
//
//
//  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int =
//      if(halfWindow == 0){
//        _parent.readPointImpl(channel, frame, segment)
//      } else {
//        //logger.info("2"+halfWindow + " ")
//        rootMeanSquare( _parent.readTrace(channel, new RangeFr(frame - halfWindow, frame + halfWindow, 1, OptSegment(segment)) ) ).toInt
//        //rootMeanSquare( DenseVector[Int]( _parent.readTrace(channel, frame - halfWindow to frame + halfWindow, segment).toArray ) ).toInt
//      }
//
//  override def readTraceImpl(channel: Int, range: Range.Inclusive, segment: Int): DV[Int] = {
//    if(halfWindow == 0){
//      _parent.readTraceImpl(channel, range, segment)
//    } else {
//      val trace = _parent.readTrace(channel, new RangeFr(range.start - halfWindow, range.last + halfWindow, 1, OptSegment(segment)))
//      val tempret = DV[Int](range.length) //= new VectorBuilder[Int]()
//        //tempret.sizeHint(range.length)
//        val window = 2*halfWindow
//        var tempIndex = 0
//        for( cnt <- 0 to (range.end-range.start) by range.step) {
//          tempret(tempIndex) = rootMeanSquare( trace.slice(cnt, cnt + window)  ).toInt
//          tempIndex += 1
//        } //rootMeanSquare( DenseVector(trace.slice(cnt, cnt + windowMinus1).toArray) ).toInt
//      //logger.info(halfWindow + " " + windowMinus1)
//      tempret
//    }
//  }
//
//    //  override def channelNames: scala.Vector[String] = _parent.channelNames
//
//    override def absUnit: String = _parent.absUnit + " (rms)"
//  //ToDo 3: offset and gain aren't quite well established here! OK as long as offset is zero, but...
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
////    private def rms(vect: Vector[Int]): Int = {
////      var tempsum = 0d
////      var tempcount = 0
////      for( cnt <- 0 until vect.length){
////        tempsum += ( vect(cnt).toDouble * vect(cnt).toDouble )
////        tempcount += 1
////      }
////      sqrt((tempsum / tempcount)).toInt
////    }
//
//  }