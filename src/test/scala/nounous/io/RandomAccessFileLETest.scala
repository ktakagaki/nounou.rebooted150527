package nounous.io

import java.io.IOException
import org.scalatest.FunSuite

/**
 * Created with IntelliJ IDEA.
 * User: Kenta
 * Date: 11/4/13
 * Time: 6:22 PM
 * To change this template use File | Settings | File Templates.
 */
class RandomAccessFileLETest extends FunSuite {

  val fileHead = "V:/docs/bb/nounous/src/test/scala/nounous/io"
  var stream: RandomAccessFileLE = null

  test("readDouble"){
    stream = new RandomAccessFileLE(fileHead + "/DoubleLE.bin", "r")
    val result = stream.readDouble(5)
    assert(result(0) == 0.0)
    assert(result(1) == 3.141592653589793)
    assert(result(2) == 2.718281828459045)
    assert(result(3) == 6.02214E23)
    assert(result(4) == 1.6726231000000002E-24)
    stream.close
  }

  test("readFloat"){
    stream = new RandomAccessFileLE(fileHead + "/FloatLE.bin", "r")
    val result = stream.readFloat(5)
    assert(result(0) == 0.0)
    assert(result(1) == 3.1415927F)
    assert(result(2) == 2.7182817F)
    assert(result(3) == 6.02214E23F)
    assert(result(4) == 1.6726232E-24F)
    stream.close
  }

  test("readInt8/readByte"){
    stream = new RandomAccessFileLE(fileHead + "/Int8.bin", "r")
    val res = stream.readInt8(5)
    stream.seek(0)
    val resB = stream.readByte(5)
    assert(res(0) == resB(0) && resB(0) == 0)
    assert(res(1) == resB(1) && resB(1) == 1)
    assert(res(2) == resB(2) && resB(2) == -1)
    assert(res(3) == resB(3) && resB(3) == -128)
    assert(res(4) == resB(4) && resB(4) == 127)
    stream.close
  }

  test("readUInt8/readUnsignedByte"){
    stream = new RandomAccessFileLE(fileHead + "/UInt8.bin", "r")
    val res = stream.readUInt8(5)
    stream.seek(0)
    val resB = stream.readUnsignedByte(5)
    assert(res(0) == resB(0) && resB(0) == 0)
    assert(res(1) == resB(1) && resB(1) == 1)
    assert(res(2) == resB(2) && resB(2) == 1)
    assert(res(3) == resB(3) && resB(3) == 255)
    assert(res(4) == resB(4) && resB(4) == 255)
    stream.close
  }

  test("readInt16/readShort"){
    stream = new RandomAccessFileLE(fileHead + "/Int16LE.bin", "r")
    val res = stream.readInt16(5)
    stream.seek(0)
    val resB = stream.readShort(5)
    assert(res(0) == resB(0) && resB(0) == 0)
    assert(res(1) == resB(1) && resB(1) == 1)
    assert(res(2) == resB(2) && resB(2) == -1)
    assert(res(3) == resB(3) && resB(3) == -32768)
    assert(res(4) == resB(4) && resB(4) == 32767)
    stream.close
  }

  test("readUInt16/readUnsignedShort"){
    stream = new RandomAccessFileLE(fileHead + "/UInt16LE.bin", "r")
    val res = stream.readUInt16(5)
    stream.seek(0)
    val resB = stream.readUnsignedShort(5)
    assert(res(0) == resB(0) && resB(0) == 0)
    assert(res(1) == resB(1) && resB(1) == 1)
    assert(res(2) == resB(2) && resB(2) == 1)
    assert(res(3) == resB(3) && resB(3) == 65535)
    assert(res(4) == resB(4) && resB(4) == 65535)
    stream.close
  }

  test("readInt32/readInt"){
    stream = new RandomAccessFileLE(fileHead + "/Int32LE.bin", "r")
    val res = stream.readInt32(5)
    stream.seek(0)
    val resB = stream.readInt(5)
    assert(res(0) == resB(0) && resB(0) == 0)
    assert(res(1) == resB(1) && resB(1) == 1)
    assert(res(2) == resB(2) && resB(2) == -1)
    assert(res(3) == resB(3) && resB(3) == 2147483647)
    assert(res(4) == resB(4) && resB(4) == -2147483648)
    stream.close
  }

  test("readUInt32"){
    stream = new RandomAccessFileLE(fileHead + "/UInt32LE.bin", "r")
    val res = stream.readUInt32(5)
    assert(res(0) ==  0L)
    assert(res(1) ==  1L)
    assert(res(2) ==  1L)
    assert(res(3) ==  4294967295L)
    assert(res(4) ==  4294967295L)
    stream.close
  }

  test("readInt64/readLong"){
    stream = new RandomAccessFileLE(fileHead + "/Int64LE.bin", "r")
    val res = stream.readInt64(5)
    stream.seek(0)
    val resB = stream.readLong(5)
    assert(res(0) == resB(0) && resB(0) == 0)
    assert(res(1) == resB(1) && resB(1) == 1)
    assert(res(2) == resB(2) && resB(2) == -1)
    assert(res(3) == resB(3) && resB(3) == 9223372036854775807L)
    assert(res(4) == resB(4) && resB(4) == -9223372036854775808L)
    stream.close
  }

  test("readUInt64"){
    stream = new RandomAccessFileLE(fileHead + "/UInt64LE.bin", "r")
    val res = stream.readUInt64(4)
    assert(res(0) ==  0L)
    assert(res(1) ==  1L)
    assert(res(2) ==  1L)
    assert(res(3) ==  9223372036854775807L)
    try{
      stream.readUInt64
    }catch{
      case e: IOException => assert(true)
      case _: Throwable => assert(false)
    }
    stream.close
  }
}
