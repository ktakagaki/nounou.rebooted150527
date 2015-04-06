package nounou.elements.layouts

/**
 */
trait NNDataLayoutNeighbor extends NNDataLayout {

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

}
