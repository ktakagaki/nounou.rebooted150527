package nounou.data

import nounou.data.traits.XConcatenatable
import breeze.linalg.max
import scala.collection.mutable.Set

/**Class to encapsulate trode layouts
 * @author ktakagaki
 * @date 3/14/14.
 */
abstract class XTrodes extends X with XConcatenatable {

  /**Returns the total number of defined trodes.*/
  def trodeCount: Int
  /**Returns the channels included in each trode.*/
  def trodeGroup(trode: Int): Array[Int]
  def trodeNeighbors( channel: Int ): Array[Int]
  def channelCount: Int

}


/**Encapsulates a pre-specified trode configuration
 *
 * @param trodeGroups an Array of Array[Int]s, containing the trode groups, which are all assumed to be neighboring
  *
 */
class XTrodesPreloaded(private val trodeGroups: Array[Array[Int]]) extends XTrodes {

  private val trodeGroupChecker = Set[Int]()
  for( trodeGrp <- trodeGroups ){
    loggerRequire( trodeGroupChecker.intersect( trodeGrp.toSet ).size == 0, "Channels can only be assigned to one trode group!")
    trodeGroupChecker.++=(trodeGrp)
  }
  for( cnt <- 0 until trodeGroupChecker.size ){
    loggerRequire( trodeGroupChecker.contains(cnt), "Trode groups must contain all channels from 0 to the max. Create additional 1 channel groups when initialzing a XTrodePreloaded group, to achieve this.")
  }

  override val trodeCount = trodeGroups.length
  override def trodeGroup( trode: Int ) = trodeGroups(trode)
  override def trodeNeighbors( channel: Int ) = {
    val grp = trodeGroups.filter( _.contains(channel) ).apply(0)
    grp.filterNot( _ == channel )
  }
  override lazy val channelCount: Int = max( trodeGroups.flatten )

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


}

class XTrodesIndividual( channels: Int ) extends XTrodesPreloaded( Array.tabulate( channels )( Array( _ ) )  )

object XTrodesNull extends XTrodesPreloaded( Array[Array[Int]]() )


