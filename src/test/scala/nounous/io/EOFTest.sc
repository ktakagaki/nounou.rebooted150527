/**
 * Created with IntelliJ IDEA.
 * User: Kenta
 * Date: 11/3/13
 * Time: 7:16 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io.File
import java.io.RandomAccessFile

val fileHead = "V:/docs/bb/nounous/src/test/scala/nounous/io"

val file = new RandomAccessFile(new File(fileHead + "/SignedInt8.bin"), "r")
file.length
file.readByte()
file.readByte()
file.readByte()
file.getFilePointer
file.readByte()
file.readByte()
//file.readByte()
file.seek(0)
val byteArray = new Array[Byte](8)
file.read(byteArray)
byteArray






