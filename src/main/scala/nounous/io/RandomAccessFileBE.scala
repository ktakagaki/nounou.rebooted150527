package nounous.io

import java.io.{File, DataInput, RandomAccessFile, IOException}
//import scala.util.Try

/**Wrapper for {@link java.io.RandomAccessFile}.
  *The main differences are in the readXXX(n: Int) functions, which will try to read n samples from the file.
  *
  * <table border="2">
  * <tr><th>Type</th>                    <th>Java Type</th>    <th>Scala Type</th>   <th>Value Range</th> </tr>
  * <tr><td>Signed 8-bit integer</td>    <td>byte</td>         <td>Byte</td>         <td>[-128, 127]</td>        </tr>
  * <tr><td>Unsigned 8-bit integer</td>  <td>(int)</td>        <td>(Int)</td>        <td>[0, 255]</td> </tr>
  * <tr><td>Signed 16-bit integer</td>   <td>short</td>        <td>Short</td>        <td>[-32768, 32767]</td>        </tr>
  * <tr><td>Unsigned 16-bit integer</td> <td>char</td>         <td>Char</td>        <td>[0, 65535]</td> </tr>
  * </table>
 *
 * @param file
 * @param arg0
 */
class RandomAccessFileBE(file: File, arg0: String = "r") extends DataInput /*extends java.io.RandomAccessFile(file, arg0)*/ {

  def this(filename: String, arg0: String) =  this(new File(filename), arg0)

  protected val rafObj = new RandomAccessFile(file, arg0)// = new FileInputStream(file)
  protected var fileEnded = false

  @deprecated
  protected def calculateN(n: Int) = {
    if( rafObj.getFilePointer + n >= rafObj.length ){ (rafObj.length - rafObj.getFilePointer).toInt }
    else{ n }
  }

  ///// Int8 (Byte) /////
  @throws(classOf[IOException])
  final def readInt8(): Byte = rafObj.readByte()
  @throws(classOf[IOException])
  final def readInt8(n: Int): Array[Byte] = {
    val tempret = new Array[Byte](n)
    rafObj.read(tempret)
    //(for(i <- 0 until n ) yield readByte() ).toArray
    tempret
  }
  //(for(i <- 0 until calculateN(n) ) yield readByte() ).toArray
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readInt8]]
    */
  @throws(classOf[IOException])
  final override def readByte(): Byte = readInt8()
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readInt8]]
    */
  @throws(classOf[IOException])
  final def readByte(n: Int): Array[Byte] = readInt8(n)



  ///// UInt8 /////
  @throws(classOf[IOException])
  final def readUInt8() = rafObj.readUnsignedByte()
  /**Tries to read n unsigned 8-bit Bytes as Int from the current getFilePointer().
    */
  @throws(classOf[IOException])
  final def readUInt8(n: Int) : Array[Int] =
    (for(i <- 0 until n ) yield readUnsignedByte() ).toArray
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readUInt8]]
    * @throws java.io.IOException
    */
  @throws(classOf[IOException])
  final override def readUnsignedByte() = readUInt8()
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readUInt8]]
    * @throws java.io.IOException
    */
  @throws(classOf[IOException])
  final def readUnsignedByte(n: Int) : Array[Int] = readUInt8(n)



  ///// Int16 (Short) /////
  @throws(classOf[IOException])
  def readInt16(): Short = rafObj.readShort()
  @throws(classOf[IOException])
  def readInt16(n: Int): Array[Short] = {
    val ba = new Array[Byte](n*2)
    rafObj.read(ba)  //reading is much faster if many bytes are read simultaneously
    val tr = new Array[Short](n)
    //the following is a hack to avoid the heavier Scala for loop
      var c = 0
      while(c < n){
        tr(c) = bytesToInt16(ba(c*2), ba(c*2 + 1))
        c += 1
      }
      //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
  }
//  final def readInt16(n: Int = 1) : Array[Short] =
//    (for(i <- 0 until n ) yield readInt16() ).toArray
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readInt16]]
    */
  @throws(classOf[IOException])
  final override def readShort() = readInt16()
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readInt16]]
    */
  @throws(classOf[IOException])
  def readShort(n: Int) : Array[Short] = readInt16(n)



  ///// UInt16 (Unsigned Short) /////
  @throws(classOf[IOException])
  def readUInt16() : Int = rafObj.readUnsignedShort()
  @throws(classOf[IOException])
  final def readUInt16(n: Int): Array[Int] = {
    val ba = new Array[Byte](n*2)
    rafObj.read(ba)  //reading is much faster if many bytes are read simultaneously
    val tr = new Array[Int](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while(c < n){
      tr(c) = bytesToUInt16(ba(c*2), ba(c*2 + 1))
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
  }
//  @throws(classOf[IOException])
//  final def readUInt16(n: Int) : Array[Int] =
//    (for(i <- 0 until n ) yield readUInt16() ).toArray
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readUInt16]]
    */
  @throws(classOf[IOException])
  final override def readUnsignedShort() : Int = readUInt16()
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readUInt16]]
    */
  @throws(classOf[IOException])
  final def readUnsignedShort(n: Int) : Array[Int] = readUInt16(n)

  ///// UInt16 (Char) /////
  @throws(classOf[IOException])
  override def readChar() : Char = rafObj.readChar()
  /**Tries to read n unsigned 16-bit Bytes as Int from the current getFilePointer().
    * If file length() is shorter, will read to end of file
    */
  @throws(classOf[IOException])
  final def readChar(n: Int): Array[Char] = {
    val ba = new Array[Byte](n*2)
    rafObj.read(ba)  //reading is much faster if many bytes are read simultaneously
    val tr = new Array[Char](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while(c < n){
      tr(c) = bytesToUInt16(ba(c*2), ba(c*2 + 1)).toChar
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
  }
//  @throws(classOf[IOException])
//  final def readChar(n: Int) : Array[Char] =
//    (for(i <- 0 until n ) yield readChar ).toArray


  ///// Int32 (Int) /////
  @throws(classOf[IOException])
  def readInt32() = rafObj.readInt()
  @throws(classOf[IOException])
  final def readInt32(n: Int) : Array[Int] = {
    val ba = new Array[Byte](n*4)
    rafObj.read(ba)  //reading is much faster if many bytes are read simultaneously
    val tr = new Array[Int](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while(c < n){
      tr(c) = bytesToInt32(ba(c*4), ba(c*4 + 1), ba(c*4 + 2), ba(c*4 + 3))
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
}
//  @throws(classOf[IOException])
//  final def readInt32(n: Int) : Array[Int] =
//    (for(i <- 0 until n ) yield readInt32 ).toArray
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readUInt32]]
    */
  @throws(classOf[IOException])
  final override def readInt() = readInt32()
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readUInt32]]
    */
  @throws(classOf[IOException])
  final def readInt(n: Int) : Array[Int] = readInt32(n)

  ///// UInt32 (Long) /////
  @throws(classOf[IOException])
  def readUInt32(): Long = {
    val ba = readByte(4)
    bytesToUInt32(ba(0), ba(1), ba(2), ba(3))
  }
  @throws(classOf[IOException])
  final def readUInt32(n: Int) : Array[Long] = {
    val ba = new Array[Byte](n*4)
    rafObj.read(ba)  //reading is much faster if many bytes are read simultaneously
    val tr = new Array[Long](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while(c < n){
      tr(c) = bytesToUInt32(ba(c*4), ba(c*4 + 1), ba(c*4 + 2), ba(c*4 + 3))
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
  }
    //(for(i <- 0 until n ) yield readUInt32 ).toArray


  ///// Int64 (Int) /////
  @throws(classOf[IOException])
  def readInt64() = rafObj.readLong()
  @throws(classOf[IOException])
  final def readInt64(n: Int) : Array[Long] = {
    val ba = new Array[Byte](n*8)
    rafObj.read(ba)  //reading is much faster if many bytes are read simultaneously
    val tr = new Array[Long](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while(c < n){
      val c8 = c * 8
      tr(c) = bytesToInt64(ba(c8), ba(c8 + 1), ba(c8 + 2), ba(c8 + 3), ba(c8 + 4), ba(c8 + 5), ba(c8 + 6), ba(c8 + 7))
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
  }
//    (for(i <- 0 until n ) yield readInt64).toArray
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readInt64]]
    */
  @throws(classOf[IOException])
  final override def readLong() = readInt64()
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readInt64]]
    */
  @throws(classOf[IOException])
  final def readLong(n: Int) : Array[Long] = readInt64(n)

  ///// UInt64 (Long) /////
  @throws(classOf[IOException])
  def readUInt64(): Long = {
    val ba = readByte(8)
    bytesToUInt64(ba(0), ba(1), ba(2), ba(3), ba(4), ba(5), ba(6), ba(7))
  }
  @throws(classOf[IOException])
  final def readUInt64(n: Int) : Array[Long] = {
      val ba = new Array[Byte](n*8)
      rafObj.read(ba)  //reading is much faster if many bytes are read simultaneously
      val tr = new Array[Long](n)
      //the following is a hack to avoid the heavier Scala for loop
      var c = 0
      while(c < n){
        val c8 = c * 8
        tr(c) = bytesToUInt64(ba(c8), ba(c8 + 1), ba(c8 + 2), ba(c8 + 3), ba(c8 + 4), ba(c8 + 5), ba(c8 + 6), ba(c8 + 7))
        c += 1
      }
      //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
      tr
  }
//    (for(i <- 0 until n ) yield readUInt64 ).toArray


  ///// Floating Point /////
  override def readDouble() = rafObj.readDouble()
  override def readFloat() = rafObj.readFloat()
  @throws(classOf[IOException])
  final def readDouble(n: Int) : Array[Double] = {
    val ba = new Array[Byte](n*8)
    rafObj.read(ba)  //reading is much faster if many bytes are read simultaneously
    val tr = new Array[Double](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while(c < n){
      val c8 = c * 8
      tr(c) = java.lang.Double.longBitsToDouble(
        bytesToInt64(ba(c8), ba(c8 + 1), ba(c8 + 2), ba(c8 + 3), ba(c8 + 4), ba(c8 + 5), ba(c8 + 6), ba(c8 + 7))
      )
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
  }
//    (for(i <- 0 until n ) yield readDouble).toArray
  @throws(classOf[IOException])
  final def readFloat(n: Int) : Array[Float] = {
    val ba = new Array[Byte](n*4)
    rafObj.read(ba)  //reading is much faster if many bytes are read simultaneously
    val tr = new Array[Float](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while(c < n){
      val c4 = c * 4
      tr(c) = java.lang.Float.intBitsToFloat(
        bytesToInt32(ba(c4), ba(c4 + 1), ba(c4 + 2), ba(c4 + 3)).toInt
      )
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
  }
//    (for(i <- 0 until n ) yield readFloat).toArray


  def bytesToInt16(b1: Byte, b2: Byte): Short  = {
    //    b3 << 8 | b4
    (b1 << 8 | b2 & 0xFF).toShort
  }
  def bytesToUInt16(b1: Byte, b2: Byte): Int  = {
    //    b3 << 8 | b4
    (b1.toInt & 0xFF) << 8 | (b2.toInt & 0xFF)
  }
  def bytesToInt32(b1: Byte, b2: Byte, b3: Byte, b4: Byte): Int  = {
    b1.toInt << 24 | (b2 & 0xFF) << 16 | (b3 & 0xFF) << 8 | (b4 & 0xFF)
  }
  def bytesToUInt32(b1: Byte, b2: Byte, b3: Byte, b4: Byte): Long  = {
    (b1.toLong & 0xFFL) << 24 | (b2.toLong & 0xFFL) << 16 | (b3.toLong & 0xFFL) << 8 | (b4.toLong & 0xFFL)
  }
  def bytesToUInt64(b1 : Byte, b2 : Byte, b3 : Byte, b4 : Byte, b5 : Byte, b6 : Byte, b7 : Byte, b8 : Byte): Long = {
    if((b1.toInt & 0x80) != 0x00){
      throw new IOException("UInt64 too big to read given limitations of Long format.")
    }else{
      (b1.toLong & 0xFFL) << 56 | (b2.toLong & 0xFFL) << 48  | (b3.toLong & 0xFFL) << 40 | (b4.toLong & 0xFFL) << 32 |
        (b5.toLong & 0xFFL) << 24 | (b6.toLong & 0xFFL) << 16  | (b7.toLong & 0xFFL) << 8  | (b8.toLong & 0xFFL)
    }
  }
  def bytesToInt64(b1 : Byte, b2 : Byte, b3 : Byte, b4 : Byte, b5 : Byte, b6 : Byte, b7 : Byte, b8 : Byte): Long = {
    (b1.toLong /*& 0xFFL*/) << 56 | (b2.toLong & 0xFFL) << 48  | (b3.toLong & 0xFFL) << 40 | (b4.toLong & 0xFFL) << 32 |
      (b5.toLong & 0xFFL) << 24 | (b6.toLong & 0xFFL) << 16  | (b7.toLong & 0xFFL) << 8  | (b8.toLong & 0xFFL)
  }



  final override def readBoolean() = rafObj.readBoolean()
  override def readFully(b: Array[Byte]) = rafObj.readFully(b)
  override def readFully(b: Array[Byte], off: Int, len: Int) = rafObj.readFully(b, off, len)
  override def readLine() = rafObj.readLine()
  override def readUTF() = rafObj.readUTF()
  /**Attempts to skip over n bytes of input discarding the skipped bytes.
    * This method may skip over some smaller number of bytes, possibly zero.
    * This may result from any of a number of conditions; reaching end of file before n bytes have been skipped
    * is only one possibility. This method never throws an EOFException. The actual number of bytes skipped is returned.
    * If n is negative, no bytes are skipped.
    * @see     java.io.RandomAccessFile#skipBytes   */
  override def skipBytes(n: Int): Int = rafObj.skipBytes(n)

  /**Returns the current offset in this file.
    * @see     java.io.RandomAccessFile#getFilePointer   */
  def getFilePointer: Long = rafObj.getFilePointer()
  /**Sets the file-pointer offset, measured from the beginning of this file, at which the next read or write occurs.
    * The offset may be set beyond the end of the file. Setting the offset beyond the end of the file does not change
    * the file length. The file length will change only by writing after the offset has been set beyond the end of the file.
    * @see     java.io.RandomAccessFile#seek   */
  def seek(pos: Long): Unit = rafObj.seek(pos)
  /**Returns the length of this file.
    * @see     java.io.RandomAccessFile#length   */
  def length: Long = rafObj.length()
  /**Sets the length of this file. /n
    * If the present length of the file as returned by the length method is greater than the newLength argument
    * then the file will be truncated. In this case, if the file offset as returned by the getFilePointer method
    * is greater than newLength then after this method returns the offset will be equal to newLength./n
    * If the present length of the file as returned by the length method is smaller than the newLength argument
    * then the file will be extended. In this case, the contents of the extended portion of the file are not defined.
    * @see     java.io.RandomAccessFile#setLength   */
  def setLength(newLength: Long): Unit = rafObj.setLength(newLength)
  /**Closes this random access file stream and releases any system resources associated with the stream.
    * A closed random access file cannot perform input or output operations and cannot be reopened./n
    * If this file has an associated channel then the channel is closed as well.
    * @see     java.io.RandomAccessFile#close   */
  def close: Unit = rafObj.close()
  def getChannel = rafObj.getChannel()
  def getFD = rafObj.getFD()


}


