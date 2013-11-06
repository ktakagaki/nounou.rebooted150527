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
  /**
   * Reads a signed 16-bit number from this file, but as Little-Endian.
   *
   * @see     java.io.RandomAccessFile#readShort()
   */
  @throws(classOf[IOException])
  override def readInt16(): Short = {
    val b1 = readByte()
    val b2 = readByte()
    bytesToInt16BE(b2, b1)//.shortValue()
    //( (ch1 << 0) + (ch2 << 8) ).shortValue()
  }


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
    bytesToUInt16BE(b2, b1)
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
    bytesToInt32BE(b4, b3, b2, b1)
	//if ((ch1 | ch2 | ch3 | ch4) < 0) throw new EOFException();
  //  ((b4 << 24) + (b3 << 16) + (b2 << 8) + (b1 << 0));
  }

  ///// UInt32 (Long) /////
  @throws(classOf[IOException])
  override def readUInt32(): Long = {
    val b1 = readByte()
    val b2 = readByte()
    val b3 = readByte()
    val b4 = readByte()
    bytesToUInt32BE(b4, b3, b2, b1)
  }//{ readInt32().toLong + 2147483648L }


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

    bytesToUInt64BE(bA7, bA6, bA5, bA4, bA3, bA2, bA1, bA0)
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
    
  	bytesToInt64BE(bA7, bA6, bA5, bA4, bA3, bA2, bA1, bA0)
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