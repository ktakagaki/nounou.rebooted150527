package nounou.io.neuralynx

import breeze.io.RandomAccessFile
import nounou.io.FileLoader
import nounou.util.LoggingExt


/**
* @author ktakagaki
* @date 12/16/13
*/
abstract class FileAdapterNeuralynx extends LoggingExt with FileLoader  {

//  //saving not implemented yet
//  //override
//  def saveImpl(data: NNElement, fileName: String/*, options: OptFileAdapter*/) =
//    throw loggerError("not implemented yet!")
//    //saveCannotImpl(data, fileName/*, options*/)

  /** File handle for the file to be read.
    *
    */
  var fHand: RandomAccessFile = _

  /**The total number of bytes in the initial Neuralynx text header.*/
  final val headerBytes = 16384
  val recordBytes: Int
  //  val neuralynxTextHeader: String

  var nlxHeaderText: String = "Inital"

  def nlxHeaderLoad(): Unit = {
    fHand.seek(0)
    nlxHeaderText = new String(fHand.readUInt8(headerBytes).map(_.toChar))
    //nlxHeader = new String(fHand.readChar(headerBytes))
  }
  def nlxHeaderParserS(valueName: String, default: String): String = {
    val pattern = ("-" + valueName + """[ ]+(\S+)""").r
    pattern.findFirstIn(nlxHeaderText) match {
      case Some(pattern(v)) => v
      case _ => default
    }
  }
  def nlxHeaderParserD(valueName: String, default: String) = nlxHeaderParserS(valueName: String, default: String).toDouble
  def nlxHeaderParserI(valueName: String, default: String) = nlxHeaderParserS(valueName: String, default: String).toInt

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
