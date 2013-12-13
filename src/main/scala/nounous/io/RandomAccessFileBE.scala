package nounous.io

import java.io.{File, DataInput, DataOutput, Closeable, RandomAccessFile, IOException}


/** Wrapper for [[java.io.RandomAccessFile]].
  *
  * The main differences to [[java.io.RandomAccessFile]] are
  * (1) naming (e.g. default naming is readInt64 instead of readLong)
  * (2) the readXXX(n: Int) functions, which will try to read n samples from the file. (Try to use these functions instead
  * of reading multiple times in a loop with readXXX(), as each individual read is costly
  * in terms of performance.
  *
  * Of note, RandomAccessFileBE explicitly supports big endian reading/writing (as does [[java.io.RandomAccessFile]]).
  * Use [[nounous.io.RandomAccessFileLE]] to read/write little endian.
  *
  * Each function throws a [[java.io.IOException]], which can be caught in Scala
  * if necessary to detect ends of files when reading, for example. Catching is obligatory in Java.
  *
  * <table border="2">
  * <tr><th>Type</th>                    <th>Java Type</th>    <th>Scala Type</th>   <th>Value Range</th> </tr>
  * <tr><td>Signed 8-bit integer</td>    <td>byte</td>         <td>Byte</td>         <td>[-128, 127]</td>        </tr>
  * <tr><td>Unsigned 8-bit integer</td>  <td>(int)</td>        <td>(Int)</td>        <td>[0, 255]</td> </tr>
  * <tr><td>Signed 16-bit integer</td>   <td>short</td>        <td>Short</td>        <td>[-32768, 32767]</td>        </tr>
  * <tr><td>Unsigned 16-bit integer</td> <td>char</td>         <td>Char</td>        <td>[0, 65535]</td> </tr>
  * </table>
  *
  */
class RandomAccessFileBE(file: File, arg0: String = "r") extends DataInput /*with DataOutput*/ with Closeable /*extends java.io.RandomAccessFile(file, arg0)*/ {

  /** byte converter to encode (switched for RandomAccessFileLE)
    */
  val converter: ByteConverter = nounous.io.BigEndianByteConverter

  def this(filename: String, arg0: String) = this(new File(filename), arg0)

  protected val rafObj = new RandomAccessFile(file, arg0)
  //protected var fileEnded = false


  ///// Int8 (Byte) /////
  //Reading
  /** Tries to read an Int8 (Byte) at the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def readInt8(): Byte = rafObj.readByte()

  /** Tries to read n Int8s (Bytes) from the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def readInt8(n: Int): Array[Byte] = {
    val tempret = new Array[Byte](n)
    rafObj.read(tempret)
    tempret
  }
  //(for(i <- 0 until calculateN(n) ) yield readByte() ).toArray

  //Writing
  @throws(classOf[IOException])
  final def writeInt8(v: Byte): Unit = {
    rafObj.write(v)
  }

  /** Tries to read n Int8s (Bytes) from the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def writeInt8(v: Array[Byte]): Unit = {
    rafObj.write(v)
  }

  //Aliases
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readInt8]]
    */
  @throws(classOf[IOException])
  final override def readByte(): Byte = readInt8()

  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readInt8]]
    */
  @throws(classOf[IOException])
  final def readByte(n: Int): Array[Byte] = readInt8(n)

  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.writeInt8]]
    */
  @throws(classOf[IOException])
  final def write(v: Byte) = writeInt8(v)

  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.writeInt8]]
    */
  @throws(classOf[IOException])
  final def write(v: Array[Byte]) = writeInt8(v)


  ///// UInt8 /////
  //Reading
  @throws(classOf[IOException])
  final def readUInt8() = rafObj.readUnsignedByte()

  /** Tries to read n UInt8s as Int from the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def readUInt8(n: Int): Array[Int] = {
    val tr = new Array[Int](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while (c < n) {
      tr(c) = readUnsignedByte()
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
    //    (for(i <- 0 until n ) yield readUnsignedByte() ).toArray
  }

  //Writing
  @throws(classOf[IOException])
  final def writeUInt8(value: Byte) = rafObj.write(Array[Byte](value))
  @throws(classOf[IOException])
  final def writeUInt8(values: Array[Byte]): Unit = rafObj.write(values)

  //Aliases
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readUInt8]]
    */
  @throws(classOf[IOException])
  final override def readUnsignedByte() = readUInt8()
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readUInt8]]
    */
  @throws(classOf[IOException])
  final def readUnsignedByte(n: Int): Array[Int] = readUInt8(n)
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.writeUInt8]]
    */
  @throws(classOf[IOException])
  final def writeUnsignedByte(value: Byte) = writeUInt8(value)
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.writeUInt8]]
    */
  @throws(classOf[IOException])
  final def writeUnsignedByte(values: Array[Byte]) = writeUInt8(values)


  ///// Int16 (Short) /////
  //Reading
  @throws(classOf[IOException])
  def readInt16(): Short = rafObj.readShort()

  /** Tries to read n Int16s from the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  def readInt16(n: Int): Array[Short] = {
    val ba = new Array[Byte](n * 2)
    rafObj.read(ba) //reading is much faster if many bytes are read simultaneously
    val tr = new Array[Short](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while (c < n) {
      tr(c) = converter.bytesToInt16(ba(c * 2), ba(c * 2 + 1))
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
  }

  //Writing
  /** Tries to write an Int16 (Int) to the current getFilePointer().
    */
  @throws(classOf[IOException])
  final def writeInt16(v: Short): Unit = {
    rafObj.write(converter.int16ToBytes(v))
  }

  /** Tries to write an array of Int16s (Ints) to the current getFilePointer().
    */
  @throws(classOf[IOException])
  final def writeInt16(v: Array[Short]): Unit = {
    rafObj.write( v.flatMap(converter.int16ToBytes(_)) )
  }

  //Aliases
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readInt16]]
    */
  @throws(classOf[IOException])
  final override def readShort() = readInt16()

  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readInt16]]
    */
  @throws(classOf[IOException])
  def readShort(n: Int): Array[Short] = readInt16(n)

  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readInt16]]
    */
  @throws(classOf[IOException])
  final def writeShort(v: Short) = writeInt16(v)

  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readInt16]]
    */
  @throws(classOf[IOException])
  def writeShort(v: Array[Short]) = writeInt16(v)


  ///// UInt16 (Unsigned Short) /////
  @throws(classOf[IOException])
  def readUInt16(): Int = rafObj.readUnsignedShort()

  /** Tries to read n UInt16s from the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def readUInt16(n: Int): Array[Int] = {
    val ba = new Array[Byte](n * 2)
    rafObj.read(ba) //reading is much faster if many bytes are read simultaneously
    val tr = new Array[Int](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while (c < n) {
      tr(c) = converter.bytesToUInt16(ba(c * 2), ba(c * 2 + 1))
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
  }

  //Writing
  /** Tries to write a UInt16 (represented by Int) to the current getFilePointer().
    */
  @throws(classOf[IOException])
  final def writeUInt16(v: Int): Unit = {
    rafObj.write(converter.uInt16ToBytes(v))
  }

  /** Tries to write an array of UInt16s (represented by Ints) to the current getFilePointer().
    */
  @throws(classOf[IOException])
  final def writeUInt16(v: Array[Int]): Unit = {
    rafObj.write( v.flatMap(converter.uInt16ToBytes(_)) )
  }

  //Aliases
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readUInt16]]
    */
  @throws(classOf[IOException])
  final override def readUnsignedShort(): Int = readUInt16()

  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readUInt16]]
    */
  @throws(classOf[IOException])
  final def readUnsignedShort(n: Int): Array[Int] = readUInt16(n)

  ///// UInt16 (Char) /////
  @throws(classOf[IOException])
  override def readChar(): Char = rafObj.readChar()

  /** Tries to read n UInt16s from the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def readChar(n: Int): Array[Char] = {
    val ba = new Array[Byte](n * 2)
    rafObj.read(ba) //reading is much faster if many bytes are read simultaneously
    val tr = new Array[Char](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while (c < n) {
      tr(c) = converter.bytesToUInt16(ba(c * 2), ba(c * 2 + 1)).toChar
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
  }


  ///// Int32 (Int) /////
  @throws(classOf[IOException])
  def readInt32() = rafObj.readInt()

  /** Tries to read n Int32s from the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def readInt32(n: Int): Array[Int] = {
    val ba = new Array[Byte](n * 4)
    rafObj.read(ba) //reading is much faster if many bytes are read simultaneously
    val tr = new Array[Int](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while (c < n) {
      tr(c) = converter.bytesToInt32(ba(c * 4), ba(c * 4 + 1), ba(c * 4 + 2), ba(c * 4 + 3))
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
  }

  //Aliases
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readUInt32]]
    */
  @throws(classOf[IOException])
  final override def readInt() = readInt32()

  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readUInt32]]
    */
  @throws(classOf[IOException])
  final def readInt(n: Int): Array[Int] = readInt32(n)

  ///// UInt32 (Long) /////
  @throws(classOf[IOException])
  def readUInt32(): Long = {
    val ba = readByte(4)
    converter.bytesToUInt32(ba(0), ba(1), ba(2), ba(3))
  }

  /** Tries to read n UInt32s from the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def readUInt32(n: Int): Array[Long] = {
    val ba = new Array[Byte](n * 4)
    rafObj.read(ba) //reading is much faster if many bytes are read simultaneously
    val tr = new Array[Long](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while (c < n) {
      tr(c) = converter.bytesToUInt32(ba(c * 4), ba(c * 4 + 1), ba(c * 4 + 2), ba(c * 4 + 3))
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
  }


  ///// Int64 (Int) /////
  @throws(classOf[IOException])
  def readInt64() = rafObj.readLong()

  /** Tries to read n Int64s from the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def readInt64(n: Int): Array[Long] = {
    val ba = new Array[Byte](n * 8)
    rafObj.read(ba) //reading is much faster if many bytes are read simultaneously
    val tr = new Array[Long](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while (c < n) {
      val c8 = c * 8
      tr(c) = converter.bytesToInt64(ba(c8), ba(c8 + 1), ba(c8 + 2), ba(c8 + 3), ba(c8 + 4), ba(c8 + 5), ba(c8 + 6), ba(c8 + 7))
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
  }
  //    (for(i <- 0 until n ) yield readInt64).toArray

  //Aliases
  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readInt64]]
    */
  @throws(classOf[IOException])
  final override def readLong() = readInt64()

  /** Alias, in java style, for [[nounous.io.RandomAccessFileBE.readInt64]]
    */
  @throws(classOf[IOException])
  final def readLong(n: Int): Array[Long] = readInt64(n)

  ///// UInt64 (Long) /////
  @throws(classOf[IOException])
  final def readUInt64(): Long = {
    val ba = readByte(8)
    converter.bytesToUInt64(ba(0), ba(1), ba(2), ba(3), ba(4), ba(5), ba(6), ba(7))
  }

  @throws(classOf[IOException])
  final def readUInt64Shifted(): Long = {
    val ba = readByte(8)
    converter.bytesToUInt64Shifted(ba(0), ba(1), ba(2), ba(3), ba(4), ba(5), ba(6), ba(7))
  }

  /** Tries to read n UInt64s, shifted to fit into Int64/Longs from the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def readUInt64Shifted(n: Int): Array[Long] = {
    val ba = new Array[Byte](n * 8)
    rafObj.read(ba) //reading is much faster if many bytes are read simultaneously
    val tr = new Array[Long](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while (c < n) {
      val c8 = c * 8
      tr(c) = converter.bytesToUInt64Shifted(ba(c8), ba(c8 + 1), ba(c8 + 2), ba(c8 + 3), ba(c8 + 4), ba(c8 + 5), ba(c8 + 6), ba(c8 + 7))
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
  }

  /** Tries to read n UInt64s from the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def readUInt64(n: Int): Array[Long] = {
    val ba = new Array[Byte](n * 8)
    rafObj.read(ba) //reading is much faster if many bytes are read simultaneously
    val tr = new Array[Long](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while (c < n) {
      val c8 = c * 8
      tr(c) = converter.bytesToUInt64(ba(c8), ba(c8 + 1), ba(c8 + 2), ba(c8 + 3), ba(c8 + 4), ba(c8 + 5), ba(c8 + 6), ba(c8 + 7))
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
  }


  ///// Floating Point /////
  override def readDouble() = rafObj.readDouble()
  override def readFloat() = rafObj.readFloat()

  /** Tries to read n Doubles from the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def readDouble(n: Int): Array[Double] = {
    val ba = new Array[Byte](n * 8)
    rafObj.read(ba) //reading is much faster if many bytes are read simultaneously
    val tr = new Array[Double](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while (c < n) {
      val c8 = c * 8
      tr(c) = java.lang.Double.longBitsToDouble(
        converter.bytesToInt64(ba(c8), ba(c8 + 1), ba(c8 + 2), ba(c8 + 3), ba(c8 + 4), ba(c8 + 5), ba(c8 + 6), ba(c8 + 7))
      )
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
  }

  /** Tries to read n Floats from the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def readFloat(n: Int): Array[Float] = {
    val ba = new Array[Byte](n * 4)
    rafObj.read(ba) //reading is much faster if many bytes are read simultaneously
    val tr = new Array[Float](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while (c < n) {
      val c4 = c * 4
      tr(c) = java.lang.Float.intBitsToFloat(
        converter.bytesToInt32(ba(c4), ba(c4 + 1), ba(c4 + 2), ba(c4 + 3))
      )
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
  }

  @throws(classOf[IOException])
  def writeDouble(v: Double) = rafObj.writeDouble(v)
  @throws(classOf[IOException])
  def writeFloat(v: Float) = rafObj.writeFloat(v)


  //other RandomAccessFile overrides
  /** Pass on to [[java.io.RandomAccessFile]]
    */
  final override def readBoolean() = rafObj.readBoolean()

  /** Pass on to [[java.io.RandomAccessFile]]
    */
  override def readFully(b: Array[Byte]) = rafObj.readFully(b)

  /** Pass on to [[java.io.RandomAccessFile]]
    */
  override def readFully(b: Array[Byte], off: Int, len: Int) = rafObj.readFully(b, off, len)

  /** Pass on to [[java.io.RandomAccessFile]]
    */
  override def readLine() = rafObj.readLine()

  /** Pass on to [[java.io.RandomAccessFile]]
    */
  override def readUTF() = rafObj.readUTF()

  /** Pass on to [[java.io.RandomAccessFile]]
    */
  override def skipBytes(n: Int): Int = rafObj.skipBytes(n)

  /** Pass on to [[java.io.RandomAccessFile]]
    */
  def getFilePointer: Long = rafObj.getFilePointer

  /** Pass on to [[java.io.RandomAccessFile]]
    */
  def seek(pos: Long): Unit = rafObj.seek(pos)

  /** Pass on to [[java.io.RandomAccessFile]]
    */
  def length: Long = rafObj.length

  /** Pass on to [[java.io.RandomAccessFile]]
    */
  def setLength(newLength: Long): Unit = rafObj.setLength(newLength)

  /** Pass on to [[java.io.RandomAccessFile]]
    */
  def close: Unit = rafObj.close

  /** Pass on to [[java.io.RandomAccessFile]]
    */
  def getChannel = rafObj.getChannel

  /** Pass on to [[java.io.RandomAccessFile]]
    */
  def getFD = rafObj.getFD

}


object BigEndianByteConverter extends ByteConverter {

  ///// bytesToXXX /////
  def bytesToInt16(b0: Byte, b1: Byte): Short = {
    (b0 << 8 | b1 & 0xFF).toShort
  }

  def bytesToUInt16(b0: Byte, b1: Byte): Int = {
    (b0.toInt & 0xFF) << 8 | (b1.toInt & 0xFF)
  }

  def bytesToInt32(b0: Byte, b1: Byte, b2: Byte, b3: Byte): Int = {
    b0.toInt << 24 | (b1 & 0xFF) << 16 | (b2 & 0xFF) << 8 | (b3 & 0xFF)
  }

  def bytesToUInt32(b0: Byte, b1: Byte, b2: Byte, b3: Byte): Long = {
    (b0.toLong & 0xFFL) << 24 | (b1.toLong & 0xFFL) << 16 | (b2.toLong & 0xFFL) << 8 | (b3.toLong & 0xFFL)
  }

  def bytesToUInt64(b0: Byte, b1: Byte, b2: Byte, b3: Byte, b4: Byte, b5: Byte, b6: Byte, b7: Byte): Long = {
    if ((b0/*.toInt*/ & 0x80) != 0x00) {
      throw new IOException("UInt64 too big to read given limitations of Long format.")
    } else {
      (b0.toLong & 0xFFL) << 56 | (b1.toLong & 0xFFL) << 48 | (b2.toLong & 0xFFL) << 40 | (b3.toLong & 0xFFL) << 32 |
        (b4.toLong & 0xFFL) << 24 | (b5.toLong & 0xFFL) << 16 | (b6.toLong & 0xFFL) << 8 | (b7.toLong & 0xFFL)
    }
  }

  def bytesToInt64(b0: Byte, b1: Byte, b2: Byte, b3: Byte, b4: Byte, b5: Byte, b6: Byte, b7: Byte): Long = {
    b0.toLong << 56 | (b1.toLong & 0xFFL) << 48 | (b2.toLong & 0xFFL) << 40 | (b3.toLong & 0xFFL) << 32 |
      (b4.toLong & 0xFFL) << 24 | (b5.toLong & 0xFFL) << 16 | (b6.toLong & 0xFFL) << 8 | (b7.toLong & 0xFFL)
  }

  def bytesToUInt64Shifted(b0: Byte, b1: Byte, b2: Byte, b3: Byte, b4: Byte, b5: Byte, b6: Byte, b7: Byte): Long = {
    (b0 ^ 0x80).toLong << 56 | (b1.toLong & 0xFFL) << 48 | (b2.toLong & 0xFFL) << 40 | (b3.toLong & 0xFFL) << 32 |
      (b4.toLong & 0xFFL) << 24 | (b5.toLong & 0xFFL) << 16 | (b6.toLong & 0xFFL) << 8 | (b7.toLong & 0xFFL)
  }

  ///// XXXToByte /////
  def int16ToBytes(value: Short): Array[Byte] = {
    val tempret = new Array[Byte](2)
    tempret(0) = (value >> 8).toByte
    tempret(1) = (value & 0xFF).toByte
    tempret
  }

  def uInt16ToBytes(value: Int): Array[Byte] = {
    require(value <= 65535 && value >= 0, "Value " + value + " is out of range of 2-byte unsigned array.")

    val tempret = new Array[Byte](2)
    tempret(0) = ((value >> 8) & 0xFF).toByte
    tempret(1) =  (value       & 0xFF).toByte
    tempret
  }

  def int32ToBytes(value: Int): Array[Byte] = {
    val tempret = new Array[Byte](4)
    tempret(0) =  (value >> 24).toByte
    tempret(1) = ((value >> 16) & 0xFF).toByte
    tempret(2) = ((value >> 8)  & 0xFF).toByte
    tempret(3) =  (value        & 0xFF).toByte
    tempret
  }

  def uInt32ToBytes(value: Long): Array[Byte] = {
    require(value <= 4294967295L && value >= 0L, "Value " + value + " is out of range of 4-byte unsigned array.")

    val tempret = new Array[Byte](4)
    tempret(0) = ((value >> 24) & 0xFF).toByte
    tempret(1) = ((value >> 16) & 0xFF).toByte
    tempret(2) = ((value >> 8)  & 0xFF).toByte
    tempret(3) =  (value        & 0xFF).toByte
    tempret
  }

  def int64ToBytes(value: Long): Array[Byte] = {
    val tempret = new Array[Byte](8)
    tempret(0) =  (value >> 56).toByte
    tempret(1) = ((value >> 48) & 0xFF).toByte
    tempret(2) = ((value >> 40) & 0xFF).toByte
    tempret(3) = ((value >> 32) & 0xFF).toByte
    tempret(4) = ((value >> 24) & 0xFF).toByte
    tempret(5) = ((value >> 16) & 0xFF).toByte
    tempret(6) = ((value >> 8)  & 0xFF).toByte
    tempret(7) =  (value        & 0xFF).toByte
    tempret
  }

  def uInt64ToBytes(value: Long): Array[Byte] = {
    require(value >= 0, "Value " + value + " is out of range of 4-byte unsigned array.")

    val tempret = new Array[Byte](8)
    tempret(0) = ((value >> 56) & 0xFF).toByte
    tempret(1) = ((value >> 48) & 0xFF).toByte
    tempret(2) = ((value >> 40) & 0xFF).toByte
    tempret(3) = ((value >> 32) & 0xFF).toByte
    tempret(4) = ((value >> 24) & 0xFF).toByte
    tempret(5) = ((value >> 16) & 0xFF).toByte
    tempret(6) = ((value >> 8)  & 0xFF).toByte
    tempret(7) =  (value        & 0xFF).toByte
    tempret
  }

  def uInt64ShiftedToBytes(value: Long): Array[Byte] = {

    val tempret = new Array[Byte](8)
    tempret(0) = (((value >> 56) & 0xFF) ^ 0x80).toByte
    tempret(1) = ((value >> 48) & 0xFF).toByte
    tempret(2) = ((value >> 40) & 0xFF).toByte
    tempret(3) = ((value >> 32) & 0xFF).toByte
    tempret(4) = ((value >> 24) & 0xFF).toByte
    tempret(5) = ((value >> 16) & 0xFF).toByte
    tempret(6) = ((value >> 8)  & 0xFF).toByte
    tempret(7) =  (value        & 0xFF).toByte
    tempret
  }

}