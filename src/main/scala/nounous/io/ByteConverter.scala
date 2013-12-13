package nounous.io

/**
 * Created by Kenta on 12/10/13.
 */

abstract class ByteConverter {

  ///// bytesToXXX /////
  def bytesToInt16(b0: Byte, b1: Byte): Short

  def bytesToUInt16(b0: Byte, b1: Byte): Int

  def bytesToInt32(b0: Byte, b1: Byte, b2: Byte, b3: Byte): Int

  def bytesToUInt32(b0: Byte, b1: Byte, b2: Byte, b3: Byte): Long

  def bytesToUInt64(b0: Byte, b1: Byte, b2: Byte, b3: Byte, b4: Byte, b5: Byte, b6: Byte, b7: Byte): Long

  def bytesToInt64(b0: Byte, b1: Byte, b2: Byte, b3: Byte, b4: Byte, b5: Byte, b6: Byte, b7: Byte): Long

  def bytesToUInt64Shifted(b0: Byte, b1: Byte, b2: Byte, b3: Byte, b4: Byte, b5: Byte, b6: Byte, b7: Byte): Long

  ///// XXXToByte /////
  def int16ToBytes(value: Short): Array[Byte]

  def uInt16ToBytes(value: Int): Array[Byte]

  def int32ToBytes(value: Int): Array[Byte]

  def uInt32ToBytes(value: Long): Array[Byte]

  def int64ToBytes(value: Long): Array[Byte]

  def uInt64ToBytes(value: Long): Array[Byte]

  def uInt64ShiftedToBytes(value: Long): Array[Byte]

}
