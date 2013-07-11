// From LittleEndianDataInputStream.java in the guava project
// https://code.google.com/p/guava-libraries/source/browse/guava/src/com/google/common/io/LittleEndianDataInputStream.java

package nounous.io

import java.io.RandomAccessFile;
import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.io.EOFException;

class RandomAccessFileLE(file: File, arg0: String) extends DataInput {
//ToDo implement write functions
  
  val rafObj = new RandomAccessFile(file, arg0);

  def this(filename: String, arg0: String) =  this(new File(filename), arg0)
  def this(filename: String) = this (filename, "r")
  def this(file: File) = this (file, "r")
  
  def close = rafObj.close()
  def getChannel = rafObj.getChannel()
  def getFD = rafObj.getFD()
  def getFilePointer = rafObj.getFilePointer()
  def length = rafObj.length()
  
  def seek(pos: Long) { rafObj.seek(pos)}
  def skipBytes(n: Int): Int = rafObj.skipBytes(n)
  

  /**
   * Reads a signed 16-bit number from this file, but as Little-Endian.
   *
   * @see     java.io.RandomAccessFile#readShort()
   */
  @throws(classOf[IOException])
  def readShort():Short = {
  	val ch1 = readAndCheckByte();
   	val ch2 = readAndCheckByte();
   	//if ((ch1 | ch2) < 0) throw new EOFException();
   	( (ch2 << 8) + (ch1 << 0) ).shortValue()
  }
  @throws(classOf[IOException])
  def readShort(n: Int) : Array[Short] = {
	val ret = new Array[Short](n)
	for(i <- 0 to n-1) ret(i) = readShort()
	ret
  }

   /**
   * Reads an unsigned {@code short} as specified by
   * {@link DataInputStream#readUnsignedShort()}, except using little-endian
   * byte order.
   *
   * @return the next two bytes of the input stream, interpreted as an 
   *         unsigned 16-bit integer in little-endian byte order
   * @throws IOException if an I/O error occurs
   */
  @throws(classOf[IOException])
  def readUnsignedShort(): Int = {
    val b1 = readAndCheckByte();
    val b2 = readAndCheckByte();
    intFromBytes(0.toByte, 0.toByte, b2, b1);
  }


  /**
   * Reads a char as specified by {@link DataInputStream#readChar()}, except
   * using little-endian byte order.
   *
   * @return the next two bytes of the input stream, interpreted as a 
   *         {@code char} in little-endian byte order
   * @throws IOException if an I/O error occurs
   */
  @throws(classOf[IOException])
  def readChar():Char = {
  		readUnsignedShort().toChar;
  }
  @throws(classOf[IOException])
  def readChar(n: Int) : Array[Char] = {
	val ret = new Array[Char](n)
	for(i <- 0 to n-1) ret(i) = readChar()
	ret
  }
 
  
  /**
   * Reads a signed 32-bit integer from this file, but as Little-Endian.
   * @see     java.io.RandomAccessFile#readInt()
   */
  @throws(classOf[IOException])
  def readInt():Int = {
	val ch1 = readAndCheckByte();
	val ch2 = readAndCheckByte();
	val ch3 = readAndCheckByte();
	val ch4 = readAndCheckByte();
	//if ((ch1 | ch2 | ch3 | ch4) < 0) throw new EOFException();
    ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
  }
  /**
   * Reads a number of signed 32-bit integer from this file, but as Little-Endian.
   * @see     java.io.RandomAccessFile#readInt()
   */
  @throws(classOf[IOException])
  def readInt(n: Int) : Array[Int] = {
	val ret = new Array[Int](n)
	for(i <- 0 to n-1) ret(i) = readInt()
	ret
  }

  /**
   * Reads a signed 64-bit integer from this file, but as Little-Endian.
   *
   * @see     java.io.RandomAccessFile#readLong()
   */
  @throws(classOf[IOException])
  def readLong(): Long = {
    val bA0 = readAndCheckByte()
    val bA1 = readAndCheckByte()
    val bA2 = readAndCheckByte()
    val bA3 = readAndCheckByte()
    val bA4 = readAndCheckByte()
    val bA5 = readAndCheckByte()
    val bA6 = readAndCheckByte()
    val bA7 = readAndCheckByte()
    
  	bytesToLong(bA7, bA6, bA5, bA4, bA3, bA2, bA1, bA0)
  }
  def bytesToLong(b1 : Byte, b2 : Byte, b3 : Byte, b4 : Byte, b5 : Byte, b6 : Byte, b7 : Byte, b8 : Byte): Long = {
    return (b1 & 0xFFL) << 56 | (b2 & 0xFFL) << 48  | (b3 & 0xFFL) << 40 | (b4 & 0xFFL) << 32 |
           (b5 & 0xFFL) << 24 | (b6 & 0xFFL) << 16  | (b7 & 0xFFL) << 8  | (b8 & 0xFFL);
  }  

  /**
   * Reads a <code>float</code> from this file, but as Little-Endian. 
   *
   * @see        java.io.RandomAccessFile#readFloat()
   * @see        java.io.RandomAccessFile#readInt()
   * @see        java.lang.Float#intBitsToFloat(int)
   */
   @throws(classOf[IOException])
   def readFloat():Float = {
    	java.lang.Float.intBitsToFloat(readInt());
   }

   /**
    * Reads a <code>double</code> from this file, but as Little-Endian. 
    *
    * @see        java.io.RandomAccessFile#readDouble()
    * @see        java.io.RandomAccessFile#readLong()
    * @see        java.lang.Double#longBitsToDouble(long)
    */
   @throws(classOf[IOException])
   def readDouble(): Double = {
	java.lang.Double.longBitsToDouble(readLong());
   }
  @throws(classOf[IOException])
  def readDouble(n: Int) : Array[Double] = {
	val ret = new Array[Double](n)
	for(i <- 0 to n-1) ret(i) = readDouble()
	ret
  }

   def readUTF = rafObj.readUTF()
   def readLine = rafObj.readLine()
   def readUnsignedByte = rafObj.readUnsignedByte()
   def readBoolean = rafObj.readBoolean()
   def readByte = rafObj.readByte()
   def readByte(n: Int) : Array[Byte] = {
	val ret = new Array[Byte](n)
	for(i <- 0 to n-1) ret(i) = readByte()
	ret
  }
   def readFully(x: Array[Byte]) = rafObj.readFully(x)
   def readFully(x: Array[Byte], a: Int, b: Int) = rafObj.readFully(x, a, b)
   
   
   
   
   
   
  /**
   * Returns the {@code Int} value whose byte representation is the given 4
   * bytes, in big-endian order; equivalent to {@code Ints.fromByteArray(new
   * byte[] {b1, b2, b3, b4})}.
   *
   * @since 7.0
   */
  private def intFromBytes(b1: Byte, b2: Byte, b3: Byte, b4: Byte):Int  = {
       b1 << 24 | (b2 & 0xFF) << 16 | (b3 & 0xFF) << 8 | (b4 & 0xFF)
  }
   
  /**
   * Reads a byte from the input stream checking that the end of file (EOF)
   * has not been encountered.
   *  
   * @return byte read from input
   * @throws IOException if an error is encountered while reading
   * @throws EOFException if the end of file (EOF) is encountered.
   */
  @throws(classOf[IOException])
  @throws(classOf[EOFException])
  private def readAndCheckByte():Byte = {
    val b1 = rafObj.read();
    if (-1 == b1) {
      throw new EOFException();
    }
    return b1.toByte;
  }

}