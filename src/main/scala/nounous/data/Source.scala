package nounous.data

import java.io.File
import breeze.linalg.Axis._0
import nounous.reader.ReaderNEX

//import nounous.data.XData
//import nounous.data.XLayout

/**
 * Created with IntelliJ IDEA.
 * User: takagaki
 * Date: 18.09.13
 * Time: 19:03
 * To change this template use File | Settings | File Templates.
 */
abstract class Source {

  /**Header object covering the whole block.*/
  var head: XHeader
  /**Main data output.*/
  var dat: XData
  /**Auxiliary data, for instance, analog signals recorded with an \optical trace.*/
  var aux: XData
  /**Events.*/
  var mask: XEvents  //ToDo: 1 masking artifacts
  /**Events.*/
  var eve: XEvents
  /**Spikes.*/
  var spk: XSpikes

  var lay: XLayout = layOri
  var layOri: XLayout = new XLayoutNull

  //ToDo: 3 video?


  def read(file: File): Unit = read( Array(file) )
  def read(files: Array[File]): Unit = files.flatMap( readImpl(_) )
  def readImpl(file: File): List[X] = {
    file.getName.toLowerCase match {
      case n: String if n.endsWith(".nex") => ReaderNEX.read( file )
      case n => throw new IllegalArgumentException("File format for " + n + " is not supported yet.")
    }
  }

  def clearData = {
    head = null
    dat  = null
    aux  = null
    mask = null
    eve  = null
    spk  = null
  }

}
