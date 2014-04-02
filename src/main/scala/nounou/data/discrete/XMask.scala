package nounou.data

import scala.collection.immutable.{TreeMap}
import scala.reflect.ClassTag
import nounou.data.traits.XFrames
import scala.collection.mutable.ArrayBuffer
import nounou.ranges.RangeFr

//ToDo 1: Mask serialization

/**
   * @author ktakagaki
   * @date 1/30/14.
   */
  class XMask extends X {



  // <editor-fold defaultstate="collapsed" desc=" TreeMap[Long, Long] accessor methods ">

  private var _masks: TreeMap[Long, Long] = new TreeMap[Long, Long]()
  def getMask() = _masks
  def getMaskA(): Array[Array[Long]] = _masks.map( p => Array[Long](p._1, p._2) ).toArray

  def setMaskA(mask: Array[Array[Long]]): Unit = {
    _masks = TreeMap[Long, Long]()
    for(elem <- mask) { _masks = _masks.+( elem(0) -> elem(1) ) }
  }

  def size() = _masks.size

  def clear():Unit  = {
    _masks = new TreeMap[Long, Long]()
  }
  // </editor-fold>


  // <editor-fold defaultstate="collapsed" desc=" mask/(unmask) ">

  def mask(startTS: Long, endTS: Long): Unit = {
    val result = _masks.filter( p => (startTS < p._1 && p._1 < endTS) || (startTS < p._2 && p._2 < endTS) )
    val values = ( Vector[Long](startTS, endTS) ++ result.map( x => Vector[Long]( x._1, x._2 ) ).toVector.flatten )
    _masks = _masks -- result.keys
    _masks = _masks.+((values.min, values.max))
  }

  //ToDo 2: test this
  def eliminateOverlapping(): Unit = {
    if( _masks.size > 1 ){
      var ab = new ArrayBuffer[(Long, Long)]
      var ma = _masks
      var currentStart = ma(ma.firstKey)
      var currentEnd = currentStart
      ma = ma.drop(1)

      while(ma.size>1) {

        if( ma.firstKey <= currentEnd ){
          //next key set is overlapping, extend current segment
          currentEnd = ma(ma.firstKey)
        } else {
          //next key set is not overlapping, terminate previous and start new
          ab.+=( (currentStart, currentEnd) )
          currentStart = ma.firstKey
          currentEnd   = ma(ma.firstKey)
        }

        ma = ma.drop(1)

      }

      _masks = TreeMap(ab : _*)
    }
  }



  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" isMasked ">

  def isMaskedTS( timeStamp: Long ): Boolean = {
    _masks.exists( p => p._1 <= timeStamp && timeStamp <= p._2 )
  }

  def isMaskedTS( timeSegment: (Long, Long) ): Boolean = isMaskedTS( timeSegment._1, timeSegment._2 )

  def isMaskedTS( timeStampStart: Long, timeStampEnd: Long ): Boolean = {
    _masks.exists( elem => (timeStampStart <= elem._1 && elem._1 <= timeStampEnd) ||
      (elem._2 <= timeStampEnd && timeStampStart <= elem._2)    )
  }

  def isMaskedFrame( frameStart: Int, frameEnd: Int, segment: Int, x: XData ): Boolean =
        isMaskedTS(x.frameSegmentToTS(frameStart, segment) , x.frameSegmentToTS(frameEnd, segment) )

  def isMaskedFrame( range: RangeFr, segment: Int, x: XData ): Boolean ={
    val realRange = range.getRange(x.segmentLengths(segment))
    isMaskedFrame( realRange, segment, x)
  }

  def isMaskedFrame( range: Range.Inclusive, segment: Int, x: XData ): Boolean ={
    isMaskedTS(x.frameSegmentToTS(range.start, segment) , x.frameSegmentToTS(range.end, segment) )
  }

  def isMaskedFrame( frame: Int, segment: Int, x: XData ): Boolean =
    isMaskedTS(x.frameSegmentToTS(frame, segment) )

  // </editor-fold>


  // <editor-fold defaultstate="collapsed" desc=" filterNotMasked ">

//  def filterNotMasked[T <: Iterable[Long]]( ts: T ) = ts.filterNot( p => this.isMasked(p) ).asInstanceOf[T]
//  def filterNotMaskedSeq[T <: Iterable[(Long, Long)]]( ts: T ) = ts.filterNot( p => this.isMasked(p) ).asInstanceOf[T]
//  def filterNotMasked( timeSegment: (Long, Long) ) = _masks.filterNot( isMasked(timeSegment) )
//  def filterNotMasked( ts: Long ) = _masks.filterNot( isMasked(ts) )

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" getNextMask, getActiveMasks ">

  def getNextMask(frame: Int, segment: Int, x: XFrames) = {
    _masks.find ( p => ( p._1 >= x.frameSegmentToTS(frame, segment) )  )
  }

  def getNextMaskA(frame: Int, segment: Int, x: XFrames): Array[Long] = {
    getNextMask(frame, segment, x) match {
      case Some(p) => Array[Long](p._1, p._2)
      case None => Array[Long](-1, -1)
    }
  }


  def getActiveMasksA( frameStart: Int, frameEnd: Int, segment: Int, x: XFrames ): Array[Array[Long]]  =
        getActiveMasks(x.frameSegmentToTS(frameStart, segment), x.frameSegmentToTS(frameEnd, segment)).map( ele => Array(ele._1, ele._2)).toArray

  def getActiveMasks( timeStampStart: Long, timeStampEnd: Long ): TreeMap[Long, Long]  = {
    _masks.filter( elem => (timeStampStart <= elem._1 && elem._1 <= timeStampEnd) ||
                            (elem._2 <= timeStampEnd && timeStampStart <= elem._2) ||
                              (timeStampStart <= elem._1 && elem._2 <= timeStampStart))
  }

  // </editor-fold>


  override def toString() = "XMask: " + _masks.size + " _masks"

  override def isCompatible(that: X): Boolean = false

  }











//ToDo 3: unmask(startTS: Long, endTS: Long)
//  def findOverlapMasks( timeStamp: Long ) = _masks.find( p => p._1 <= timeStamp && timeStamp <= p._2 )
//  def findOverlapMasksZip( timeStamp: Long ) = _masks.zipWithIndex.find( p => p._1._1 <= timeStamp && timeStamp <= p._1._2 )
//  def findFirstMaskAfter( timeStamp: Long ) = _masks.zipWithIndex.find( p => p._1._1 > timeStamp )
//  def findLastMaskBefore( timeStamp: Long ) = {
//    findFirstMaskAfter( timeStamp ) match {
//      case None => {
//
//      }
//    }
//    _masks.zipWithIndex.find( p => p._1._1 > timeStamp )
//  }

