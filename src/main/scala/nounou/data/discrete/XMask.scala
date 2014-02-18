package nounou.data.discrete

import nounou.data.X
import scala.collection.immutable.{TreeMap}
import scala.reflect.ClassTag

/**
   * @author ktakagaki
   * @date 1/30/14.
   */
  class XMask extends X {

  private var masks: TreeMap[Long, Long] = new TreeMap[Long, Long]()

  def size() = masks.size

  def mask(startTS: Long, endTS: Long): Unit = {
    val result = masks.filter( p => (startTS < p._1 && p._1 < endTS) || (startTS < p._2 && p._2 < endTS) )
    val values = ( Vector[Long](startTS, endTS) ++ result.map( x => Vector[Long]( x._1, x._2 ) ).toVector.flatten )
    masks = masks -- result.keys
    masks = masks.+((values.min, values.max))
  }


  //ToDo unmask(startTS: Long, endTS: Long)
//  def findOverlapMasks( timeStamp: Long ) = masks.find( p => p._1 <= timeStamp && timeStamp <= p._2 )
//  def findOverlapMasksZip( timeStamp: Long ) = masks.zipWithIndex.find( p => p._1._1 <= timeStamp && timeStamp <= p._1._2 )
//  def findFirstMaskAfter( timeStamp: Long ) = masks.zipWithIndex.find( p => p._1._1 > timeStamp )
//  def findLastMaskBefore( timeStamp: Long ) = {
//    findFirstMaskAfter( timeStamp ) match {
//      case None => {
//
//      }
//    }
//    masks.zipWithIndex.find( p => p._1._1 > timeStamp )
//  }

  def isMasked( timeStamp: Long ): Boolean = {
    masks.exists( p => p._1 <= timeStamp && timeStamp <= p._2 )
  }
  def isMasked( timeSegment: (Long, Long) ): Boolean = isMasked( timeSegment._1, timeSegment._2 )
  def isMasked( timeStampStart: Long, timeStampEnd: Long ): Boolean = {
    masks.exists( elem => (timeStampStart <= elem._1 && elem._1 <= timeStampEnd) ||
                           (elem._2 <= timeStampEnd && timeStampStart <= elem._2)    )
  }

  def filterNotMasked[T <: Iterable[Long]]( ts: T ) = ts.filterNot( p => this.isMasked(p) ).asInstanceOf[T]
  def filterNotMaskedSeq[T <: Iterable[(Long, Long)]]( ts: T ) = ts.filterNot( p => this.isMasked(p) ).asInstanceOf[T]
//  def filterNotMasked( timeSegment: (Long, Long) ) = masks.filterNot( isMasked(timeSegment) )
//  def filterNotMasked( ts: Long ) = masks.filterNot( isMasked(ts) )


  def activeMasks( timeStampStart: Long, timeStampEnd: Long ): TreeMap[Long, Long]  = {
    masks.filter( elem => (timeStampStart <= elem._1 && elem._1 <= timeStampEnd) ||
                            (elem._2 <= timeStampEnd && timeStampStart <= elem._2)   )
  }

  override def toString() = "XMask: " + masks.size + " masks"

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

