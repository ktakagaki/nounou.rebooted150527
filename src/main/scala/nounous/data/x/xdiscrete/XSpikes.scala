package nounous.data.x.xdiscrete

import nounous.data.x.X

//import nounous.data.X

/**
  * Created with IntelliJ IDEA.
  * User: takagaki
  * Date: 23.09.13
  * Time: 13:14
  * To change this template use File | Settings | File Templates.
  */
class XSpikes(override val length: Int, override val timeStamps: Vector[Long], val waveForms: Vector[Vector[Int]], val waveFormLength: Int) extends XDiscrete {

  def :::(target: X): X = ???

}
