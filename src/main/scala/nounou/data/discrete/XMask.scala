package nounou.data.discrete

import nounou.data.{XData, X}
import scala.collection.immutable.{TreeMap}
import scala.reflect.ClassTag

/**
   * @author ktakagaki
   * @date 1/30/14.
   */
  class XMask extends X {

  private var _masks: TreeMap[Long, Long] = new TreeMap[Long, Long]()
  def getMask() = _masks
  def getMaskA(): Array[Array[Long]] = _masks.map( p => Array[Long](p._1, p._2) ).toArray
  def setMaskA(mask: Array[Array[Long]]): Unit = {
    _masks = TreeMap[Long, Long]()
    for(elem <- mask) { _masks = _masks.+( elem(0) -> elem(1) ) }
  }

  def size() = _masks.size

  def mask(startTS: Long, endTS: Long): Unit = {
    val result = _masks.filter( p => (startTS < p._1 && p._1 < endTS) || (startTS < p._2 && p._2 < endTS) )
    val values = ( Vector[Long](startTS, endTS) ++ result.map( x => Vector[Long]( x._1, x._2 ) ).toVector.flatten )
    _masks = _masks -- result.keys
    _masks = _masks.+((values.min, values.max))
  }


  //ToDo unmask(startTS: Long, endTS: Long)
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

  def isMasked( timeStamp: Long ): Boolean = {
    _masks.exists( p => p._1 <= timeStamp && timeStamp <= p._2 )
  }
  def isMasked( timeSegment: (Long, Long) ): Boolean = isMasked( timeSegment._1, timeSegment._2 )
  def isMasked( frameStart: Int, frameEnd: Int, segment: Int, x: XData ): Boolean =
        isMasked(x.frameSegmentToTS(frameStart, segment) , x.frameSegmentToTS(frameEnd, segment) )
  def isMasked( timeStampStart: Long, timeStampEnd: Long ): Boolean = {
    _masks.exists( elem => (timeStampStart <= elem._1 && elem._1 <= timeStampEnd) ||
                           (elem._2 <= timeStampEnd && timeStampStart <= elem._2)    )
  }

  def filterNotMasked[T <: Iterable[Long]]( ts: T ) = ts.filterNot( p => this.isMasked(p) ).asInstanceOf[T]
  def filterNotMaskedSeq[T <: Iterable[(Long, Long)]]( ts: T ) = ts.filterNot( p => this.isMasked(p) ).asInstanceOf[T]
//  def filterNotMasked( timeSegment: (Long, Long) ) = _masks.filterNot( isMasked(timeSegment) )
//  def filterNotMasked( ts: Long ) = _masks.filterNot( isMasked(ts) )


  def activeMasksA( frameStart: Int, frameEnd: Int, segment: Int, x: XData ): Array[Array[Long]]  =
        activeMasks(x.frameSegmentToTS(frameStart, segment), x.frameSegmentToTS(frameEnd, segment)).map( ele => Array(ele._1, ele._2)).toArray
  def activeMasks( timeStampStart: Long, timeStampEnd: Long ): TreeMap[Long, Long]  = {
    _masks.filter( elem => (timeStampStart <= elem._1 && elem._1 <= timeStampEnd) ||
                            (elem._2 <= timeStampEnd && timeStampStart <= elem._2) ||
                              (timeStampStart <= elem._1 && elem._2 <= timeStampStart))
  }

  override def toString() = "XMask: " + _masks.size + " _masks"

  override def isCompatible(that: X): Boolean = false

  }

//  class XMaskTreeSet extends TreeSet[(Long, Long)] {
//
//    override val ordering = new Ordering[ (Long, Long)]{
//      override def compare( a: (Long, Long), b: (Long, Long) ) =  (b._1 -a._1).toInt
//    }
//
//    lazy val zipped = this.zipWithIndex()
//
//  }

