package nounou.data.io

import java.io.File
import nounou.data.X
import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}
import nounou.data.discrete.{XEvents, XEvent}
import scala.collection.mutable.ListBuffer
import scala.collection.mutable
import scala.collection.immutable.TreeMap

/**
 * @author ktakagaki
 * @date 1/30/14.
 */
object FileAdapterNEV extends FileAdapterNLX {

  override val canWriteExt: List[String] = List[String]()
  override val canLoadExt: List[String] = List[String]( "nev" )


  override val recordBytes = 184

  override def loadImpl(file: File): List[X] = {

    fHand = new RandomAccessFile(file, "r")(ByteConverterLittleEndian)


    // <editor-fold desc="parse the Neuralynx header">

    nlxHeaderLoad()
    val tempRecordSize = nlxHeaderParserI("RecordSize", "0")

    // </editor-fold>

    fHand.seek( headerBytes  )
    val eventMap = mutable.HashMap[Long, XEvent]()

    // <editor-fold defaultstate="collapsed" desc=" read loop ">
    var previousTTL: Short = 0
    var previousTS: Long = 0L
    var previousEventString = " "


    for(rec <- 0 until tempRecordSize){

      fHand.skipBytes(6)//readInt16(3)
      val qwTimeStamp = fHand.readUInt64Shifted()
      fHand.skipBytes(2)//val nevent_id = fHand.readInt16()
      val nttl = fHand.readInt16()
      fHand.skipBytes(6)//readInt16(3)  //ncrc, ndummy1, ndummy2
      fHand.skipBytes(32)//readInt32(8) //dnExtra
      val eventString = new String(fHand.readUInt8(128).filterNot(_ == 0).map(_.toChar))

      //only detect on and off here
      if( previousTTL != 0 && nttl == 0 ) {
          eventMap += (previousTS -> new XEvent( qwTimeStamp - previousTS, previousTTL, previousEventString) )
      }

      previousTTL = nttl
      previousEventString = eventString
      previousTS = qwTimeStamp
    }

    // </editor-fold>

    val xEvents = new XEvents( TreeMap(eventMap.toArray:_*) )
    logger.info( "FileAdapterNEV: loaded {} ", xEvents )

    List[X]( xEvents )
  }

}
