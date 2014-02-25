package nounou.data.io

import breeze.io.RandomAccessFile
import java.io.File
import nounou.data.X


/**
 * @author ktakagaki
 * @date 12/16/13
 */
abstract class FileAdapterNLX extends FileAdapter {

  override def writeImpl(file: File, data: X, options: OptFileAdapter) = writeCannotImpl(file, data, options)

  var fHand: RandomAccessFile = _

  val headerBytes = 16384
  val recordBytes: Int
  //  val neuralynxTextHeader: String

  var nlxHeader: String = "Inital"

  def nlxHeaderLoad(): Unit = {
    fHand.seek(0)
    nlxHeader = new String(fHand.readUInt8(headerBytes).map(_.toChar))
    //nlxHeader = new String(fHand.readChar(headerBytes))
  }
  def nlxHeaderParserS(valueName: String, default: String): String = {
    val pattern = ("-" + valueName + """[ ]+(\S+)""").r
    pattern.findFirstIn(nlxHeader) match {
      case Some(pattern(v)) => v
      case _ => default
    }
  }
  def nlxHeaderParserD(valueName: String, default: String) = nlxHeaderParserS(valueName: String, default: String).toDouble
  def nlxHeaderParserI(valueName: String, default: String) = nlxHeaderParserS(valueName: String, default: String).toInt

//  def simpleParser(text: String, valueName: String, default: String) = {
//    val pattern = ("-" + valueName + """[ ]+(\S+)""").r
//    pattern.findFirstIn(text) match {
//      case Some(pattern(v)) => v
//      case _ => default
//    }
//
//  }

}
