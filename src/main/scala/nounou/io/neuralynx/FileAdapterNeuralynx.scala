package nounou.io.neuralynx

import breeze.io.RandomAccessFile
import nounou.io.FileLoader
import nounou.util.LoggingExt


/**
* @author ktakagaki
* //@date 12/16/13
*/
abstract class FileAdapterNeuralynx extends LoggingExt with FileLoader  {

  /** Temporary file handle for the file to be read.
    * Should be updated for each new file read.
    */
  protected var fHand: RandomAccessFile = _

  /**The total number of bytes in the initial Neuralynx text header.*/
  final val headerBytes = 16384
  /**Size of each record, in bytes*/
  val recordBytes: Int
  /**Function to calculate start of each record within the file, in bytes*/
  def recordStartByte(record: Int): Int
  //  val neuralynxTextHeader: String

  var nlxHeaderText: String = "Initial"

  def nlxHeaderLoad(): Unit = {
    fHand.seek(0)
    nlxHeaderText = new String(fHand.readUInt8(headerBytes).map(_.toChar))
    //nlxHeader = new String(fHand.readChar(headerBytes))
  }

  // <editor-fold defaultstate="collapsed" desc=" header parsing ">

  def nlxHeaderParserS(valueName: String, default: String): String = {
    val pattern = ("-" + valueName + """[ ]+(\S+)""").r
    pattern.findFirstIn(nlxHeaderText) match {
      case Some(pattern(v)) => v
      case _ => default
    }
  }
  def nlxHeaderParserD(valueName: String, default: String) = nlxHeaderParserS(valueName: String, default: String).toDouble
  def nlxHeaderParserI(valueName: String, default: String) = nlxHeaderParserS(valueName: String, default: String).toInt

  // </editor-fold>

  //ToDo 4: should one even attempt to parse the text output? or just pass it along?
//  def simpleParser(text: String, valueName: String, default: String) = {
//    val pattern = ("-" + valueName + """[ ]+(\S+)""").r
//    pattern.findFirstIn(text) match {
//      case Some(pattern(v)) => v
//      case _ => default
//    }
//
//  }

}
