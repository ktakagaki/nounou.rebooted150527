package nounous.io

import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;
import java.io.EOFException
import scala.util.Try
;

/** Scala wrapper for java.io.RandomAccessFile in little endian.
  * Errors are passed as Try constructs, having the form Failure(ex).
  * Access results by using pattern matching for Success(X).
  *
  * @param file
  * @param arg0
  */
class RandomAccessFileLE(file: File, arg0: String = "r") extends RandomAccessFileBE(file, arg0) {

  def this(filename: String, arg0: String) =  this(new File(filename), arg0)

//ToDo implement write functions

  // Defined in RandomAccessFileBE
  // def readBoolean: Try[Boolean] = Try(rafObj.readBoolean())


  ///// UInt16 (Unsigned Short) /////
  @throws(classOf[IOException])
  override def readUInt16(): Int = {
    val ba = readByte(2)
    bytesToUInt16(ba(0), ba(1))
  }

  ///// UInt16 (Char) /////
  @throws(classOf[IOException])
  override def readChar(): Char = readUInt16().toChar



  ///// Int32 (Int) /////
  @throws(classOf[IOException])
  override def readInt32():Int = {
    val ba = readByte(4)
    bytesToInt32(ba(0), ba(1), ba(2), ba(3))
  }


  ///// Int64 (Long) /////
  @throws(classOf[IOException])
  override def readInt64(): Long = {
    val ba = readByte(8)
    bytesToInt64(ba(0), ba(1), ba(2), ba(3), ba(4), ba(5), ba(6), ba(7))
  }


   @throws(classOf[IOException])
   override def readFloat():Float = {
    	java.lang.Float.intBitsToFloat(readInt())
   }

  @throws(classOf[IOException])
  override def readDouble(): Double = {
	  java.lang.Double.longBitsToDouble(readLong())
  }


  override def bytesToInt16(b1: Byte, b2: Byte): Short  = {
    //    b3 << 8 | b4
    (b2 << 8 | b1 & 0xFF).toShort
  }
  override def bytesToUInt16(b1: Byte, b2: Byte): Int  = {
    //    b3 << 8 | b4
    (b2.toInt & 0xFF) << 8 | (b1.toInt & 0xFF)
  }
  override def bytesToInt32(b1: Byte, b2: Byte, b3: Byte, b4: Byte): Int  = {
    b4.toInt << 24 | (b3 & 0xFF) << 16 | (b2 & 0xFF) << 8 | (b1 & 0xFF)
  }
  override def bytesToUInt32(b1: Byte, b2: Byte, b3: Byte, b4: Byte): Long  = {
    (b4.toLong & 0xFFL) << 24 | (b3.toLong & 0xFFL) << 16 | (b2.toLong & 0xFFL) << 8 | (b1.toLong & 0xFFL)
  }
  override def bytesToUInt64(b1 : Byte, b2 : Byte, b3 : Byte, b4 : Byte, b5 : Byte, b6 : Byte, b7 : Byte, b8 : Byte): Long = {
    if((b8.toInt & 0x80) != 0x00){
      throw new IOException("UInt64 too big to read given limitations of Long format.")
    }else{
      (b8.toLong & 0xFFL) << 56 | (b7.toLong & 0xFFL) << 48  | (b6.toLong & 0xFFL) << 40 | (b5.toLong & 0xFFL) << 32 |
        (b4.toLong & 0xFFL) << 24 | (b3.toLong & 0xFFL) << 16  | (b2.toLong & 0xFFL) << 8  | (b1.toLong & 0xFFL)
    }
  }
  override def bytesToInt64(b1 : Byte, b2 : Byte, b3 : Byte, b4 : Byte, b5 : Byte, b6 : Byte, b7 : Byte, b8 : Byte): Long = {
    (b8.toLong /*& 0xFFL*/) << 56 | (b7.toLong & 0xFFL) << 48  | (b6.toLong & 0xFFL) << 40 | (b5.toLong & 0xFFL) << 32 |
      (b4.toLong & 0xFFL) << 24 | (b3.toLong & 0xFFL) << 16  | (b2.toLong & 0xFFL) << 8  | (b1.toLong & 0xFFL)
  }
   
//
//  /**
//   * Reads a byte from the input stream checking that the end of file (EOF)
//   * has not been encountered.
//   *
//   * @return byte read from input
//   * @throws IOException if an error is encountered while reading
//   * @throws EOFException if the end of file (EOF) is encountered.
//   */
//  @throws(classOf[IOException])
//  @throws(classOf[EOFException])
//  private def readAndCheckByte():Byte = {
//    val b1 = rafObj.read();
//    if (-1 == b1) {
//      throw new EOFException();
//    }
//    return b1.toByte;
//  }

}