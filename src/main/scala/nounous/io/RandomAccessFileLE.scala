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


  ///// Int16 (Short) /////
//  /**
//   * Reads a signed 16-bit number from this file, but as Little-Endian.
//   *
//   * @see     java.io.RandomAccessFile#readShort()
//   */
//  @throws(classOf[IOException])
//  override def readInt16(): Short = {
//    val b1 = readByte()
//    val b2 = readByte()
//    bytesToInt16(b1, b2)//.shortValue()
//    //( (ch1 << 0) + (ch2 << 8) ).shortValue()
//  }
//  override def readShort(n: Int = 1): Array[Short] = {
//    val ba = new Array[Byte](n*2)
//    rafObj.read(ba)
//
//    val tr = new Array[Short](n)
//    var c = 0
//    while(c < n){
//      tr(c) = bytesToInt16(ba(c), ba(c + 1))
//      c += 1
//    }
//    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
//    tr
//  }


  ///// UInt16 (Unsigned Short) /////
  /**
   * Reads an unsigned short (UInt16) as specified by
   * {@link DataInputStream#readUnsignedShort()}, except using little-endian
   * byte order.
   *
   * @return the next two bytes of the input stream, interpreted as an
   *         unsigned 16-bit integer in little-endian byte order
   * @throws IOException if an I/O error occurs
   */
  @throws(classOf[IOException])
  override def readUInt16(): Int = {
    val b1 = readByte()
    val b2 = readByte()
    bytesToUInt16(b1, b2)
  }
  ///// UInt16 (Char) /////
  /**
   * Reads a char as specified by {@link java.io.RandomAccessFile#readChar()}, except
   * using little-endian byte order.
   *
   * @return the next two bytes of the input stream, interpreted as a
   *         [[scala.Char]] in little-endian byte order
   * @throws IOException if an I/O error occurs
   */
  @throws(classOf[IOException])
  override def readChar(): Char = readUInt16().toChar




  ///// Int32 (Int) /////
  /**
   * Reads a signed 32-bit integer from this file, but as Little-Endian.
   * @see     java.io.RandomAccessFile#readInt()
   */
  @throws(classOf[IOException])
  override def readInt32():Int = {
    val b1 = readByte()
    val b2 = readByte()
    val b3 = readByte()
    val b4 = readByte()
    bytesToInt32(b1, b2, b3, b4)
	//if ((ch1 | ch2 | ch3 | ch4) < 0) throw new EOFException();
  //  ((b4 << 24) + (b3 << 16) + (b2 << 8) + (b1 << 0));
  }

//  /// UInt32 (Long) /////
//  @throws(classOf[IOException])
//  override def readUInt32(): Long = {
//    val b1 = readByte()
//    val b2 = readByte()
//    val b3 = readByte()
//    val b4 = readByte()
//    bytesToUInt32(b1, b2, b3, b4)
//  }//{ readInt32().toLong + 2147483648L }


  ///// Int64 (Long) /////
  /**
   * Reads an unsigned 64-bit integer from this file, as Little-Endian.
   *
   * @see     java.io.RandomAccessFile#readLong()
   */
  @throws(classOf[IOException])
  override def readUInt64(): Long = {
    val bA0 = readByte()
    val bA1 = readByte()
    val bA2 = readByte()
    val bA3 = readByte()
    val bA4 = readByte()
    val bA5 = readByte()
    val bA6 = readByte()
    val bA7 = readByte()

    bytesToUInt64(bA0, bA1, bA2, bA3, bA4, bA5, bA6, bA7)
//    bytesToUInt64(bA7, bA6, bA5, bA4, bA3, bA2, bA1, bA0)
  }

  /**
   * Reads a signed 64-bit integer from this file, but as Little-Endian.
   *
   * @see     java.io.RandomAccessFile#readLong()
   */
  @throws(classOf[IOException])
  override def readInt64(): Long = {
    val bA0 = readByte()
    val bA1 = readByte()
    val bA2 = readByte()
    val bA3 = readByte()
    val bA4 = readByte()
    val bA5 = readByte()
    val bA6 = readByte()
    val bA7 = readByte()

    bytesToInt64(bA0, bA1, bA2, bA3, bA4, bA5, bA6, bA7)
//  	bytesToInt64(bA7, bA6, bA5, bA4, bA3, bA2, bA1, bA0)
  }


  /**
   * Reads a <code>float</code> from this file, but as Little-Endian. 
   *
   * @see        java.io.RandomAccessFile#readFloat()
   * @see        java.io.RandomAccessFile#readInt()
   * @see        java.lang.Float#intBitsToFloat(int)
   */
   @throws(classOf[IOException])
   override def readFloat():Float = {
    	java.lang.Float.intBitsToFloat(readInt())
   }

 /**
  * Reads a <code>double</code> from this file, but as Little-Endian.
  *
  * @see        java.io.RandomAccessFile#readDouble()
  * @see        java.io.RandomAccessFile#readLong()
  * @see        java.lang.Double#longBitsToDouble(long)
  */
  @throws(classOf[IOException])
  override def readDouble(): Double = {
	  java.lang.Double.longBitsToDouble(readLong())
  }


  override protected def bytesToInt16(b1: Byte, b2: Byte): Short  = {
    //    b3 << 8 | b4
    (b2 << 8 | b1 & 0xFF).toShort
  }
  override protected def bytesToUInt16(b1: Byte, b2: Byte): Int  = {
    //    b3 << 8 | b4
    (b2.toInt & 0xFF) << 8 | (b1.toInt & 0xFF)
  }
  override protected def bytesToInt32(b1: Byte, b2: Byte, b3: Byte, b4: Byte): Int  = {
    b4.toInt << 24 | (b3 & 0xFF) << 16 | (b2 & 0xFF) << 8 | (b1 & 0xFF)
  }
  override protected def bytesToUInt32(b1: Byte, b2: Byte, b3: Byte, b4: Byte): Long  = {
    (b4.toLong & 0xFFL) << 24 | (b3.toLong & 0xFFL) << 16 | (b2.toLong & 0xFFL) << 8 | (b1.toLong & 0xFFL)
  }
  override protected def bytesToUInt64(b1 : Byte, b2 : Byte, b3 : Byte, b4 : Byte, b5 : Byte, b6 : Byte, b7 : Byte, b8 : Byte): Long = {
    if((b8.toInt & 0x80) != 0x00){
      throw new IOException("UInt64 too big to read given limitations of Long format.")
    }else{
      (b8.toLong & 0xFFL) << 56 | (b7.toLong & 0xFFL) << 48  | (b6.toLong & 0xFFL) << 40 | (b5.toLong & 0xFFL) << 32 |
        (b4.toLong & 0xFFL) << 24 | (b3.toLong & 0xFFL) << 16  | (b2.toLong & 0xFFL) << 8  | (b1.toLong & 0xFFL)
    }
  }
  override protected def bytesToInt64(b1 : Byte, b2 : Byte, b3 : Byte, b4 : Byte, b5 : Byte, b6 : Byte, b7 : Byte, b8 : Byte): Long = {
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