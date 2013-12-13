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
class RandomAccessFileLE(file: File, arg0: String = "r")  extends RandomAccessFileBE(file, arg0) {

  override val converter: ByteConverter = nounous.io.LittleEndianByteConverter

  def this(filename: String, arg0: String) =  this(new File(filename), arg0)

  ///// UInt16 (Unsigned Short) /////
  @throws(classOf[IOException])
  override def readUInt16(): Int = {
    val ba = readByte(2)
    converter.bytesToUInt16(ba(0), ba(1))
  }

  ///// UInt16 (Char) /////
  @throws(classOf[IOException])
  override def readChar(): Char = readUInt16().toChar

  ///// Int32 (Int) /////
  @throws(classOf[IOException])
  override def readInt32():Int = {
    val ba = readByte(4)
    converter.bytesToInt32(ba(0), ba(1), ba(2), ba(3))
  }

  ///// Int64 (Long) /////
  @throws(classOf[IOException])
  override def readInt64(): Long = {
    val ba = readByte(8)
    converter.bytesToInt64(ba(0), ba(1), ba(2), ba(3), ba(4), ba(5), ba(6), ba(7))
  }

  ///// Floating Point /////
  @throws(classOf[IOException])
  override def readFloat():Float = {
    java.lang.Float.intBitsToFloat(readInt())
  }

  @throws(classOf[IOException])
  override def readDouble(): Double = {
	  java.lang.Double.longBitsToDouble(readLong())
  }
  override def writeDouble(v: Double) = {
    writeInt64(java.lang.Double.doubleToLongBits(v))
  }
  override def writeFloat(v: Float) =  {
    writeInt32(java.lang.Float.floatToIntBits(v))
  }

}

object LittleEndianByteConverter extends ByteConverter  {

  override def bytesToInt16(b0: Byte, b1: Byte)  = BigEndianByteConverter.bytesToInt16(b1, b0)
  override def bytesToUInt16(b0: Byte, b1: Byte) = BigEndianByteConverter.bytesToUInt16(b1, b0)
  override def bytesToInt32(b0: Byte, b1: Byte, b2: Byte, b3: Byte) = BigEndianByteConverter.bytesToInt32(b3, b2, b1, b0)
  override def bytesToUInt32(b0: Byte, b1: Byte, b2: Byte, b3: Byte) = BigEndianByteConverter.bytesToUInt32(b3, b2, b1, b0)
  override def bytesToInt64(b0: Byte, b1 : Byte, b2 : Byte, b3 : Byte, b4 : Byte, b5 : Byte, b6 : Byte, b7 : Byte)
    = BigEndianByteConverter.bytesToInt64(b7, b6, b5, b4, b3, b2, b1, b0)
  override def bytesToUInt64(b0: Byte, b1 : Byte, b2 : Byte, b3 : Byte, b4 : Byte, b5 : Byte, b6 : Byte, b7 : Byte)
  = BigEndianByteConverter.bytesToUInt64(b7, b6, b5, b4, b3, b2, b1, b0)
  override def bytesToUInt64Shifted(b0: Byte, b1 : Byte, b2 : Byte, b3 : Byte, b4 : Byte, b5 : Byte, b6 : Byte, b7 : Byte)
  = BigEndianByteConverter.bytesToUInt64Shifted(b7, b6, b5, b4, b3, b2, b1, b0)

  override def int16ToBytes(value: Short): Array[Byte]  = BigEndianByteConverter.int16ToBytes(value).reverse
  override def uInt16ToBytes(value: Int): Array[Byte]   = BigEndianByteConverter.uInt16ToBytes(value).reverse
  override def int32ToBytes(value: Int): Array[Byte]    = BigEndianByteConverter.int32ToBytes(value).reverse
  override def uInt32ToBytes(value: Long): Array[Byte]  = BigEndianByteConverter.uInt32ToBytes(value).reverse
  override def int64ToBytes(value: Long): Array[Byte]   = BigEndianByteConverter.int64ToBytes(value).reverse
  override def uInt64ToBytes(value: Long): Array[Byte]  = BigEndianByteConverter.uInt64ToBytes(value).reverse
  override def uInt64ShiftedToBytes(value: Long): Array[Byte] = BigEndianByteConverter.uInt64ShiftedToBytes(value).reverse

}
