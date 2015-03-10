package nounou.elements.layouts

/**
 * Created with IntelliJ IDEA.
 * User: takagaki
 * Date: 30.09.13
 * Time: 19:17
 * To change this template use File | Settings | File Templates.
 */
trait NNLayoutNeighbor extends NNLayout {

  def getNeighbor(channel: Int, direction: Int): Int = getNeighbor(channel, direction, 1)
  def getNeighbor(channel: Int, direction: Int, ring: Int): Int
  def getNeighbors(channel: Int, direction: Int): List[Int]
  def getNeighbors(channel: Int, direction: Int, ring: Int): List[Int]

  def getNeighborVector(channel: Int, direction: Int): (Double, Double)
  def getNeighborVector(channel: Int, direction: Int, ring: Int): (Double, Double)
  def getNeighborVectors(channel: Int): List[(Double, Double)]
  def getNeighborVectors(channel: Int, ring: Int): List[(Double, Double)]

  def isEdge(channel: Int): Boolean
  def isEdge(channel: Int, ring: Int): Boolean

  // <editor-fold desc="XConcatenatable">
  // </editor-fold>
//  override def isCompatible(that: X): Boolean = {
//    that match {
//      case x: XLayoutNeighbor => {
//        //not Channels
//        (super[XLayout].isCompatible(x)) &&
//        (this.field == x.field) && (this.channelRadius == x.channelRadius)
//      }
//      case _ => false
//    }
//  }

}
