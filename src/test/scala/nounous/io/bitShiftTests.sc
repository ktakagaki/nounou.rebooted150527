import nounous.io.BigEndianByteConverter._

//var test = 1000030
//test.toBinaryString
//(test >> 8).toBinaryString
//(test << 8).toBinaryString
//(test >>> 8).toBinaryString
////(test <<< 8).toBinaryString

//def bytesToInt16(b1: Byte, b2: Byte): Short  = {
//  (b1 << 8 | b2 & 0xFF).toShort
//}
//def int16ToBytes(value: Short): Array[Byte] = {
//  val tempret = new Array[Byte](2)
//  tempret(0) = ((value >> 8) ).toByte
//  tempret(1) = (value & 0xFF).toByte
//  tempret
//}

var test = (32767).toShort
//(test >> 1).toBinaryString
test.toBinaryString
var test2 = int16ToBytes(test)
var test3 = bytesToInt16(test2(0), test2(1))
test3.toBinaryString
//bytesToInt16(127.toByte, 0.toByte)

var test4 = 65535-4
test4.toBinaryString
test2 = uInt16ToBytes(test4)
test4 = bytesToUInt16(test2(0), test2(1))
test4.toBinaryString

