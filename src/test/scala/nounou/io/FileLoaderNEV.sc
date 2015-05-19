import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}
import java.io.File

import nounou.io.neuralynx.FileAdapterNCS

/**
 * @author ktakagaki
 * //@date 1/30/14.
 */

val file = new File( getClass.getResource("/_testFiles/Neuralynx/t130911/Events.nev").getPath() )




val fHand = new RandomAccessFile(file, "r")(ByteConverterLittleEndian)

new String(fHand.readUInt8((FileAdapterNCS.headerBytes)).map(_.toChar))




















fHand.readInt16(3)
fHand.readUInt64Shifted()
fHand.readInt16(5)  //nevent_id, nttl, ncrc, ndummy1, ndummy2
fHand.readInt32(8) //dnExtra
//fHand.readChar(128)
val tempstring = new String(fHand.readUInt8(128).map(_.toChar))





tempstring.toCharArray.slice(16, 19)
tempstring.toCharArray.slice(18, 25).map(_.toShort)
fHand.readInt16(3)
fHand.readUInt64Shifted()
fHand.readInt16(5)  //nevent_id, nttl, ncrc, ndummy1, ndummy2
fHand.readInt32(8) //dnExtra
//fHand.readChar(128)
new String(fHand.readUInt8(128).filterNot( _ == 0 ).map(_.toChar))
//new String(fHand.readUInt8(128).map(_.toChar))


21412881115L
9223372036854775807L
21412881115L - 9223372036854775807L -1

