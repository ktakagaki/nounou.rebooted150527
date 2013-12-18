package breeze.io

import java.io.{File, DataInput, DataOutput, Closeable, IOException}


/** Wrapper for [[java.io.RandomAccessFile]].
  *
  * The main differences to [[java.io.RandomAccessFile]] are
  * (1) naming (e.g. default naming is readInt64 instead of readLong)
  * (2) the readXXX(n: Int) functions, which will try to read n samples from the file. (Try to use these functions instead
  * of reading multiple times in a loop with readXXX(), as each individual read is costly
  * in terms of performance.
  *
  * Child [[breeze.io.RandomAccessFileBE]] explicitly supports big endian reading/writing (as does [[java.io.RandomAccessFile]]).
  * Use [[breeze.io.RandomAccessFileLE]] to read/write little endian.
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
abstract class RandomAccessFile(file: File, arg0: String = "r") extends DataInput /*with DataOutput*/ with Closeable /*extends java.io.RandomAccessFile(file, arg0)*/ {

  /** byte converter to encode (switched for RandomAccessFileLE)
    */
  val converter: ByteConverter

  def this(filename: String, arg0: String) = this(new File(filename), arg0)

  protected val rafObj = new java.io.RandomAccessFile(file, arg0)
  //protected var fileEnded = false


  ///// Int8 (Byte) /////

  //<editor-fold desc="Reading">

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
  //</editor-fold>

  //<editor-fold desc="Writing">

  /** Tries to write an Int8 (Byte) to the current getFilePointer().
    */
  @throws(classOf[IOException])
  final def writeInt8(v: Byte): Unit = {
    rafObj.write(v)
  }

  /** Tries to write n Int8s (Bytes) to the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def writeInt8(v: Array[Byte]): Unit = {
    rafObj.write(v)
  }
  //</editor-fold>

  //<editor-fold desc="Aliases">

  /** Alias, in java style, for [[breeze.io.RandomAccessFileLE]].io.RandomAccessFileBE.readInt8]]
    */
  @throws(classOf[IOException])
  final override def readByte(): Byte = readInt8()

  /** Alias, in java style, for [[breeze.io.RandomAccessFileLE]].io.RandomAccessFileBE.readInt8]]
    */
  @throws(classOf[IOException])
  final def readByte(n: Int): Array[Byte] = readInt8(n)

  /** Alias, in java style, for [[breeze.io.RandomAccessFileLE]].io.RandomAccessFileBE.writeInt8]]
    */
  @throws(classOf[IOException])
  final def write(v: Byte) = writeInt8(v)

  /** Alias, in java style, for [[breeze.io.RandomAccessFileLE]].io.RandomAccessFileBE.writeInt8]]
    */
  @throws(classOf[IOException])
  final def write(v: Array[Byte]) = writeInt8(v)
  //</editor-fold>


  ///// UInt8 /////

  //<editor-fold desc="Reading">
  /** Tries to read a UInt8 as an Int16 at the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def readUInt8() = rafObj.readUnsignedByte()

  /** Tries to read n UInt8s as Int from the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def readUInt8(n: Int): Array[Short] = {
    val tr = new Array[Short](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while (c < n) {
      tr(c) = readUnsignedByte().toShort
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
    //    (for(i <- 0 until n ) yield readUnsignedByte() ).toArray
  }
  //</editor-fold>

  //<editor-fold desc="Writing">

  //ToDo 1: is this right? Unsigned?
  /** Tries to write a UInt8 to the current getFilePointer().
    */
  @throws(classOf[IOException])
  final def writeUInt8(value: Byte) = rafObj.write(Array[Byte](value))

  /** Tries to write n UInt8s (Bytes) at the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def writeUInt8(values: Array[Byte]): Unit = rafObj.write(values)
  //</editor-fold>

  //<editor-fold desc="Aliases">

  /** Alias, in java style, for [[breeze.io.RandomAccessFileLE]].io.RandomAccessFileBE.readUInt8]]
    */
  @throws(classOf[IOException])
  final override def readUnsignedByte() = readUInt8()
  /** Alias, in java style, for [[breeze.io.RandomAccessFileLE]].io.RandomAccessFileBE.readUInt8]]
    */
  @throws(classOf[IOException])
  final def readUnsignedByte(n: Int): Array[Short] = readUInt8(n)
  /** Alias, in java style, for [[breeze.io.RandomAccessFileLE]].io.RandomAccessFileBE.writeUInt8]]
    */
  @throws(classOf[IOException])
  final def writeUnsignedByte(value: Byte) = writeUInt8(value)
  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.writeUInt8]]
    */
  @throws(classOf[IOException])
  final def writeUnsignedByte(values: Array[Byte]) = writeUInt8(values)
  //</editor-fold>


  ///// Int16 (Short) /////

  //<editor-fold desc="Reading">

  /** Tries to read an Int16 at the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  def readInt16(): Short

  /** Tries to read n Int16s from the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def readInt16(n: Int): Array[Short] = {
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
  //</editor-fold>

  //<editor-fold desc="Writing">

  /** Tries to write an Int16 (Short) to the current getFilePointer().
    */
  @throws(classOf[IOException])
  final def writeInt16(v: Short): Unit = {
    rafObj.write(converter.int16ToBytes(v))
  }

  /** Tries to write an array of Int16s (Shorts) to the current getFilePointer().
    */
  @throws(classOf[IOException])
  final def writeInt16(v: Array[Short]): Unit = {
    rafObj.write( v.flatMap(converter.int16ToBytes(_)) )
  }
  //</editor-fold>

  //<editor-fold desc="Aliases">

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.readInt16]]
    */
  @throws(classOf[IOException])
  final override def readShort() = readInt16()

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.readInt16]]
    */
  @throws(classOf[IOException])
  final def readShort(n: Int): Array[Short] = readInt16(n)

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.readInt16]]
    */
  @throws(classOf[IOException])
  final def writeShort(v: Short) = writeInt16(v)

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.readInt16]]
    */
  @throws(classOf[IOException])
  final def writeShort(v: Array[Short]) = writeInt16(v)
  //</editor-fold>


  ///// UInt16 (Char) /////

  //<editor-fold desc="Reading">

  /** Tries to read a UInt16 (Char) at the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  def readUInt16(): Char

  /** Tries to read n UInt16s (Chars) from the current getFilePointer() as Int32.
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def readUInt16(n: Int): Array[Char] = {
    val ba = new Array[Byte](n * 2)
    rafObj.read(ba) //reading is much faster if many bytes are read simultaneously
    val tr = new Array[Char](n)
    //the following is a hack to avoid the heavier Scala for loop
    var c = 0
    while (c < n) {
      tr(c) = converter.bytesToUInt16(ba(c * 2), ba(c * 2 + 1))
      c += 1
    }
    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
    tr
  }
  //</editor-fold>

  //<editor-fold desc="Writing">

  /** Tries to write a UInt16 (Char) to the current getFilePointer().
    */
  @throws(classOf[IOException])
  final def writeUInt16(v: Char): Unit = {
    rafObj.write(converter.uInt16ToBytes(v))
  }

  /** Tries to write an array of UInt16s (Chars) to the current getFilePointer().
    */
  @throws(classOf[IOException])
  final def writeUInt16(v: Array[Char]): Unit = {
    rafObj.write( v.flatMap(converter.uInt16ToBytes(_)) )
  }
  //</editor-fold>

  //<editor-fold desc="Aliases">

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.readUInt16]], but reads as Int.
    */
  @throws(classOf[IOException])
  final override def readUnsignedShort(): Int = readUInt16().toInt

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.readUInt16]], but reads as Int.
    */
  @throws(classOf[IOException])
  final def readUnsignedShort(n: Int): Array[Int] = readUInt16(n).map(_.toInt)

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.readUInt16]]
    */
  @throws(classOf[IOException])
  override final def readChar(): Char = readUInt16()

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.readUInt16]]
    */
  @throws(classOf[IOException])
  final def readChar(n: Int): Array[Char] = readUInt16(n)
//  {
//    val ba = new Array[Byte](n * 2)
//    rafObj.read(ba) //reading is much faster if many bytes are read simultaneously
//    val tr = new Array[Char](n)
//    //the following is a hack to avoid the heavier Scala for loop
//    var c = 0
//    while (c < n) {
//      tr(c) = converter.bytesToUInt16(ba(c * 2), ba(c * 2 + 1)).toChar
//      c += 1
//    }
//    //for(c <- 0 until n) tr(c) = bytesToInt16(ba(c), ba(c + 1))
//    tr
//  }

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.writeUInt16]], but reads as Int.
    */
  @throws(classOf[IOException])
  final def writeUnsignedShort(value: Int): Unit = writeUInt16(value.toChar)

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.writeUInt16]], but reads as Int.
    */
  @throws(classOf[IOException])
  final def writeUnsignedShort(value: Array[Int]): Unit = writeUInt16(value.map(_.toChar))

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.writeUInt16]]
    */
  @throws(classOf[IOException])
  final def writeChar(value: Char): Unit = writeUInt16( value )

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.writeUInt16]]
    */
  @throws(classOf[IOException])
  final def WriteChar(value: Array [Char]): Unit = writeUInt16(value)
  //
  //</editor-fold>


  ///// Int32 (Int) /////

  //<editor-fold desc="Reading">

  /** Tries to read an Int32 at the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  def readInt32(): Int

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
  //</editor-fold>

  //<editor-fold desc="Writing">

  /** Tries to write an Int32 (Int) to the current getFilePointer().
    */
  @throws(classOf[IOException])
  final def writeInt32(v: Int): Unit = {
    rafObj.write(converter.int32ToBytes(v))
  }

  /** Tries to write an array of Int32s (Ints) to the current getFilePointer().
    */
  @throws(classOf[IOException])
  final def writeInt32(v: Array[Int]): Unit = {
    rafObj.write( v.flatMap(converter.int32ToBytes(_)) )
  }

  //</editor-fold>

  //<editor-fold desc="Aliases">

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.readUInt32]]
    */
  @throws(classOf[IOException])
  final override def readInt() = readInt32()

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.readUInt32]]
    */
  @throws(classOf[IOException])
  final def readInt(n: Int): Array[Int] = readInt32(n)

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.writeInt32]]
    */
  @throws(classOf[IOException])
  final def writeInt(value: Int): Unit = writeInt32( value )

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.writeInt32]]
    */
  @throws(classOf[IOException])
  final def writeInt(value: Array [Int]): Unit = writeInt32(value)

  //</editor-fold>

  ///// UInt32 (Long) /////

  //<editor-fold desc="Reading">

  /** Tries to read a UInt32 as Long at the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def readUInt32(): Long = {
    val ba = readByte(4)
    converter.bytesToUInt32(ba(0), ba(1), ba(2), ba(3))
  }

  /** Tries to read n UInt32s as Longs from the current getFilePointer().
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
  //</editor-fold>

  //<editor-fold desc="Writing">

  /** Tries to write a UInt32 (represented by Int) to the current getFilePointer().
    */
  @throws(classOf[IOException])
  final def writeUInt32(v: Int): Unit = {
    rafObj.write(converter.uInt32ToBytes(v))
  }
  /** Tries to write an array of Int32s (Ints) to the current getFilePointer().
    */
  @throws(classOf[IOException])
  final def writeUInt32(v: Array[Int]): Unit = {
    rafObj.write( v.flatMap(converter.uInt32ToBytes(_)) )
  }
  //</editor-fold>


  ///// Int64 (Long) /////

  //<editor-fold desc="Reading">

  /** Tries to read an Int64 at the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  def readInt64(): Long

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
  //</editor-fold>

  //<editor-fold desc="Writing">

  /** Tries to write an Int64 (Long) to the current getFilePointer().
    */
  @throws(classOf[IOException])
  final def writeInt64(v: Long): Unit = {
    rafObj.write(converter.int64ToBytes(v))
  }

  /** Tries to write an array of Int64s (Longs) to the current getFilePointer().
    */
  @throws(classOf[IOException])
  final def writeInt64(v: Array[Long]): Unit = {
    rafObj.write( v.flatMap(converter.int64ToBytes(_)) )
  }
  //</editor-fold>

  //<editor-fold desc="Aliases">

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.readInt64]]
    */
  @throws(classOf[IOException])
  final override def readLong() = readInt64()

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.readInt64]]
    */
  @throws(classOf[IOException])
  final def readLong(n: Int): Array[Long] = readInt64(n)

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.writeInt64]]
    */
  @throws(classOf[IOException])
  final def writeLong(value: Long): Unit = writeInt64( value )

  /** Alias, in java style, for [[breeze.io.RandomAccessFileBE.writeInt64]]
    */
  @throws(classOf[IOException])
  final def writeLong(value: Array [Long]): Unit = writeInt64(value)

  //</editor-fold>


  ///// UInt64 (Long) /////

  //<editor-fold desc="Reading">

  /** Tries to read a UInt64 as Long at the current getFilePointer().
    * Will throw an exception for UInt64 values which are larger than the maximum Long.
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def readUInt64(): Long = {
    val ba = readByte(8)
    converter.bytesToUInt64(ba(0), ba(1), ba(2), ba(3), ba(4), ba(5), ba(6), ba(7))
  }

  /** Tries to read n UInt64s from the current getFilePointer().
    * Will throw an exception for UInt64 values which are larger than the maximum Long.
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

  //</editor-fold>

  //<editor-fold desc="Writing">

  /** Tries to write an UInt64 (described as Long) to the current getFilePointer().
    * Will throw error if value < 0.
    */
  @throws(classOf[IOException])
  final def writeUInt64(v: Long): Unit = {
    rafObj.write(converter.uInt64ToBytes(v))
  }

  /** Tries to write an array of UInt64s (described as Longs) to the current getFilePointer().
    * Will throw error if value < 0.
    */
  @throws(classOf[IOException])
  final def writeUInt64(v: Array[Long]): Unit = {
    rafObj.write( v.flatMap(converter.uInt64ToBytes(_)) )
  }

  //</editor-fold>



  ///// UInt64Shifted (Long) /////

  //<editor-fold desc="Reading">

  /** Tries to read a UInt64, shifted down in value to fit into Int64/Long at the current getFilePointer().
    * Will throw an exception if it encounters an end of file.
    */
  @throws(classOf[IOException])
  final def readUInt64Shifted(): Long = {
    val ba = readByte(8)
    converter.bytesToUInt64Shifted(ba(0), ba(1), ba(2), ba(3), ba(4), ba(5), ba(6), ba(7))
  }

  /** Tries to read n UInt64s, shifted down in value to fit into Int64/Longs from the current getFilePointer().
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
  //</editor-fold>

  //<editor-fold desc="Writing">

  /** Tries to write an UInt64Shifted (shifted down to Long range) to the current getFilePointer().
    */
  @throws(classOf[IOException])
  final def writeUInt64Shifted(v: Long): Unit = {
    rafObj.write(converter.uInt64ShiftedToBytes(v))
  }

  /** Tries to write an array of UInt64Shifted (shifted down to Long range) to the current getFilePointer().
    */
  @throws(classOf[IOException])
  final def writeUInt64Shifted(v: Array[Long]): Unit = {
    rafObj.write( v.flatMap(converter.uInt64ShiftedToBytes(_)) )
  }
  //</editor-fold>


  ///// Floating Point /////

  //<editor-fold desc="Reading">

  override def readDouble(): Double
  override def readFloat(): Float

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
  //</editor-fold>

  //<editor-fold desc="Writing">

  @throws(classOf[IOException])
  def writeDouble(v: Double): Unit
  @throws(classOf[IOException])
  def writeFloat(v: Float): Unit


  //</editor-fold>


  /////other RandomAccessFile overrides
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


abstract class ByteConverter {

  ///// bytesToXXX /////
  /**Takes 2 Bytes and returns an Int16*/
  def bytesToInt16(b0: Byte, b1: Byte): Short

  /**Takes 2 Bytes and returns a UInt16 (as Char)*/
  def bytesToUInt16(b0: Byte, b1: Byte): Char

  def bytesToInt32(b0: Byte, b1: Byte, b2: Byte, b3: Byte): Int

  /**Takes 4 Bytes and returns a UInt32 (as Long)*/
  def bytesToUInt32(b0: Byte, b1: Byte, b2: Byte, b3: Byte): Long

  /**Takes 8 Bytes and returns a UInt64, throwing an error if it overflows Long, which is Int64*/
  def bytesToUInt64(b0: Byte, b1: Byte, b2: Byte, b3: Byte, b4: Byte, b5: Byte, b6: Byte, b7: Byte): Long

  /**Takes 8 Bytes and returns a Int64*/
  def bytesToInt64(b0: Byte, b1: Byte, b2: Byte, b3: Byte, b4: Byte, b5: Byte, b6: Byte, b7: Byte): Long

  /**Takes 8 Bytes and returns a UInt64 shifted down to the range of Int64. The shifted number range runs from
    * -2^63 to 2^63-1, so that UInt64 can be represented in the JVM long (Int64). Addition and subtraction
    * are valid with these long representations, multiplication and division, naturally, are not.*/
  def bytesToUInt64Shifted(b0: Byte, b1: Byte, b2: Byte, b3: Byte, b4: Byte, b5: Byte, b6: Byte, b7: Byte): Long

  ///// XXXToByte /////
  /**Takes an Int16, and returns an array of 2 bytes*/
  def int16ToBytes(value: Short): Array[Byte]

  /**Takes a UInt16, and returns an array of 2 bytes*/
  def uInt16ToBytes(value: Char): Array[Byte]

  /**Takes an Int32, and returns an array of 4 bytes*/
  def int32ToBytes(value: Int): Array[Byte]

  /**Takes a UInt32, and returns an array of 4 bytes*/
  def uInt32ToBytes(value: Long): Array[Byte]

  /**Takes an Int64, and returns an array of 8 bytes*/
  def int64ToBytes(value: Long): Array[Byte]

  /**Takes a UInt64, and returns an array of 8 bytes*/
  def uInt64ToBytes(value: Long): Array[Byte]

  /**Takes an Int64, and returns an array of 8 bytes, shifted up to a UInt64. See [[breeze.io.ByteConverter.bytesToUInt64Shifted()]]*/
  def uInt64ShiftedToBytes(value: Long): Array[Byte]

}
