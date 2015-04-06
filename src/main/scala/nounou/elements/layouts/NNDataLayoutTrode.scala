//package nounou.elements.layouts
//
//import breeze.linalg.max
//
//import scala.collection.mutable.ArrayBuffer
//
///**
// */
//class NNDataLayoutTrode(val trodeInput: Array[Array[Int]],
//                        override val channelCount: Int) extends NNDataLayoutPoint(channelCount) {
//
//  def NNDataLayoutTrode(trodes: Array[Array[Int]]) = new NNDataLayoutTrode( trodes, max(trodes) + 1 )
//
//  lazy val trodes: Array[Array[Int]] = trodeInput.map( _.distinct.sorted )
//  val trodeCount = trodes.length
//
//  lazy val neighborArray: Array[Array[Int]] = {
//    val temp = new Array[ArrayBuffer[Int]](channelCount)
//    for(tr <- trodes; ch <- tr) temp(ch).appendAll(tr)
//    (for(ch <- 0 until channelCount ) yield temp(ch).sorted.distinct.-=(ch).toArray ).toArray
//  }
//
//  lazy val trodeLookup: Array[Array[Int]] = {
//    val temp = Array.tabulate[ArrayBuffer[Int]](channelCount)((f: Int) => new ArrayBuffer[Int])
//    for(trN <- 0 until trodes.length; ch <- trodes(trN)){
//      temp(ch).append(trN)
//    }
//    temp.map( _.sorted.toArray )
//  }
//
//  def channelTrodeNeighbors( channel: Int ): Array[Int] = neighborArray.apply(channel)
//  def channelMemberTrodes( channel: Int ): Array[Int] = trodeLookup.apply(channel)
//  def trodeMemberChannels( trode: Int ): Array[Int] = trodes( trode )
//
//  //private def includes(array: ArrayBuffer[Int], element: Int) = !array.forall( _ != element )
//
//  private def max(array: Array[Array[Int]]) = {
//    var max = 0
//    array.foreach( _.foreach(
//      (i: Int) => {if( i < 0 ) throw loggerError("Cannot have negative trodes within the specification array!")
//        else if ( i > max ) max = i}
//    ))
//    max
//  }
//
//  def isValidTrode(trode: Int) = trode >=0 && trode < trodeCount
//
//
//}
//
//object NNDataLayoutTrode {
//
//  def singleChannels(channelCount: Int): NNDataLayoutTrode =
//    new NNDataLayoutTrode( Array.tabulate(channelCount)((p: Int) => Array(p)), channelCount )
//
//
//}
