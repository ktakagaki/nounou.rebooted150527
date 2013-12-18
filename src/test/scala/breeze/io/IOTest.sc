import nounous.io.{RandomAccessFileLE,RandomAccessFileBE}


val fileHead = "V:/docs/bb/nounous/src/test/scala/nounous/io"
var stream: RandomAccessFileBE = null
////Floating Point
println("/FloatBE.bin")
stream = new RandomAccessFileBE(fileHead + "/FloatBE.bin", "r")
stream.readFloat(5)






println("/FloatLE.bin")
stream = new RandomAccessFileLE(fileHead + "/FloatLE.bin", "r")
stream.readFloat(5)






println("/DoubleBE.bin")
stream = new RandomAccessFileBE(fileHead + "/DoubleBE.bin", "r")
stream.readDouble(5)


println("/DoubleLE.bin")
stream = new RandomAccessFileLE(fileHead + "/DoubleLE.bin", "r")
stream.readDouble(5)


//1 byte
println("/Int8.bin")
stream = new RandomAccessFileLE(fileHead + "/Int8.bin", "r")
stream.readByte(5)
println("/UInt8.bin")
stream = new RandomAccessFileLE(fileHead + "/UInt8.bin", "r")
stream.readUnsignedByte(5)
//2 bytes
println("/Int16LE.bin")
stream = new RandomAccessFileLE(fileHead + "/Int16LE.bin", "r")
stream.readShort(5)
println("/Int16BE.bin")
stream = new RandomAccessFileBE(fileHead + "/Int16BE.bin", "r")
stream.readShort(5)
println("/UInt16LE.bin")
stream = new RandomAccessFileLE(fileHead + "/UInt16LE.bin", "r")
stream.readUInt16(5)
println("/UInt16BE.bin")
stream = new RandomAccessFileBE(fileHead + "/UInt16BE.bin", "r")
stream.readUInt16(5)
//4 bytes
println("/Int32LE.bin")
stream = new RandomAccessFileLE(fileHead + "/Int32LE.bin", "r")
stream.readInt32(5)
println("/Int32BE.bin")
stream = new RandomAccessFileBE(fileHead + "/Int32BE.bin", "r")
stream.readInt32(5)
//stream.seek(0)
//stream.readInt(5)
println("/UInt32BE.bin")
stream = new RandomAccessFileBE(fileHead + "/UInt32BE.bin", "r")
stream.readUInt32(5)
println("/UInt32LE.bin")
stream = new RandomAccessFileLE(fileHead + "/UInt32LE.bin", "r")
stream.readUInt32(5)
//8 bytes BE
println("/Int64BE.bin")
stream = new RandomAccessFileBE(fileHead + "/Int64BE.bin", "r")
stream.readInt64(5)
println("/Int64LE.bin")
stream = new RandomAccessFileLE(fileHead + "/Int64LE.bin", "r")
stream.readInt64(5)
println("/UInt64BE.bin")
stream = new RandomAccessFileBE(fileHead + "/UInt64BE.bin", "r")
stream.readUInt64(4)
println("/UInt64LE.bin")
stream = new RandomAccessFileLE(fileHead + "/UInt64LE.bin", "r")
stream.readUInt64(4)
