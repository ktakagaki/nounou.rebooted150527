package nounou.data

import nounou.data.traits.XConcatenatable
import breeze.linalg.max
import scala.collection.mutable.Set

abstract class XTrode extends X with XConcatenatable {
  def channelCount=channels.length
  def channels: Vector[Int]
  def neighbors(ch: Int): Vector[Int]
}

class XTrodeN(override val channels: Vector[Int]) extends XTrode {
  override def toString() = "XTrode( " + channels.toString + " )"

  override def neighbors(ch: Int) = {
    if(!channels.contains(ch)){ throw loggerError(ch.toString + " is not a valid channel, cannot specify neighbors!")}
    channels.filter( _ != ch )
  }

  override def isCompatible(that: X): Boolean = true
  override def :::(target: X): X = target match {
    case x: XTrodeN => new XTrodeN( (channels ++ x.channels).toSet.toVector )
    case _ => throw loggerError("Cannot concatenate non-XTrode with XTrode!")
  }
}

class XTrodeNAutomatic( channels: Int ) extends XTrodeN( Vector.tabulate(channels)( i => i ) )

@deprecated
object XTrodes {
  def apply( trodeGroups: Array[Array[Int]] ) = new XTrodesPreloaded( trodeGroups )
  def apply( channelCount: Int ) = new XTrodesIndividual( channelCount )
}

/**Immutable class to encapsulate trode layouts.
 * @author ktakagaki
 * @date 3/14/14.
 */
@deprecated
abstract class XTrodes extends X with XConcatenatable {

  override def toString() = "XTrodes, channelCount = " + channelCount + ", trodeCount = " + trodeCount


  /**Returns the total number of defined trodes.*/
  def trodeCount: Int
  /**Returns the channels included in each trode.*/
  def trodeChannels(trode: Int): Array[Int]
  /**Returns the number of channels included in each trode.*/
  final def trodeSize(trode: Int) = trodeChannels(trode).length

  def trodeNeighbors( channel: Int ): Array[Int]
  def channelCount: Int

}


/**Encapsulates a pre-specified trode configuration
 *
 * @param trodeGroups an Array of Array[Int]s, containing the trode groups, which are all assumed to be neighboring
  *
 */
class XTrodesPreloaded(private val trodeGroups: Array[Array[Int]]) extends XTrodes {

  override def toString() = {
    "XTrodesPreloaded( channelCount = " + channelCount + ", trodeCount = " + trodeCount +
    "\n      trodeGroups: " //ToDo3 print ragged arrays + trodeGroups.map()
  }

  //constructer argument checks
  private val trodeGroupChecker = Set[Int]()
  for( trodeGrp <- trodeGroups ){
    loggerRequire( trodeGroupChecker.intersect( trodeGrp.toSet ).size == 0, "Channels can only be assigned to one trode group!")
    trodeGroupChecker.++=(trodeGrp)
  }
  for( cnt <- 0 until trodeGroupChecker.size ){
    loggerRequire( trodeGroupChecker.contains(cnt),
      "Trode groups must contain all channels from 0 to the max. Create additional 1 " +
       " channel groups when initialzing a XTrodePreloaded group, to achieve this.")
  }

  override val trodeCount = trodeGroups.length
  override def trodeChannels( trode: Int ) = trodeGroups(trode)
  override def trodeNeighbors( channel: Int ) = {
    val grp = trodeGroups.filter( _.contains(channel) ).apply(0)
    grp.filterNot( _ == channel )
  }
  override lazy val channelCount: Int = trodeGroupChecker.size//max( trodeGroups.flatten ) + 1


  // <editor-fold defaultstate="collapsed" desc=" XConcatenable ">

  override def isCompatible(that: X): Boolean = {
    that match {
      case t: XTrodes => true
      case _ => false
    }
  }

  def :::(target: X): X = {
    target match {
      case t: XTrodesPreloaded => {
        val newTrodeGroups = new Array[Array[Int]](this.trodeCount + t.trodeCount)
        for( count <- 0 until this.trodeCount ){
          newTrodeGroups(count) = this.trodeGroups(count)
        }
        for( count <- trodeCount until newTrodeGroups.length ){
          newTrodeGroups(count - trodeCount) = t.trodeGroups(count).map( _ + this.channelCount )
        }
        new XTrodesPreloaded( newTrodeGroups )
      }
      case _ => throw new IllegalArgumentException("the two X types are not compatible, and cannot be concatenated.")
    }
  }

  // </editor-fold>


}

class XTrodesIndividual( channels: Int ) extends XTrodesPreloaded( Array.tabulate( channels )( Array( _ ) )  )

object XTrodesOne extends XTrodesPreloaded( Array(Array[Int](0)) )
object XTrodesNull extends XTrodesPreloaded( Array(Array[Int]()) )


