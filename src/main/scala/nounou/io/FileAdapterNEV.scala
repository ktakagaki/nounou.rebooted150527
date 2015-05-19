//package nounou.io
//
//import java.io.{IOException, File}
//import nounou.elements.{NNEvent, NNElement, NNEvents}
//import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}
//import scala.collection.mutable.ListBuffer
//import scala.collection.mutable
//import scala.collection.immutable.TreeMap
//
///**
// * @author ktakagaki
// * //@date 1/30/14.
// */
//object FileAdapterNEV extends FileAdapterNLX {
//
//  override val canWriteExt: Array[String] = Array[String]()
//  override val canLoadExt: Array[String] = Array[String]( "nev" )
//
//  // <editor-fold defaultstate="collapsed" desc=" code for dealing with port/board information ">
//
//  def commentInterpreter(comment: String): (Int, Int, Int, Int) = {
//    val pattern = """TTL Input on AcqSystem(\d+)_(\d+) board (\d+) port (\d+) value.*""".r
//    //val pattern(as1, as2, b, p) = comment //"TTL Input on AcqSystem1_2 board 200 port 4 value"
//    comment match {
//      case pattern(as1, as2, b, p) => (as1.toInt, as2.toInt, b.toInt, p.toInt)
//      case _ => (0, 0, 0, 0)
//    }
//
//  }
//
//  def toPortValue(comment: String): Int = {
//    val pattern = commentInterpreter(comment)
//    toPortValue(pattern._1, pattern._2, pattern._3, pattern._4)
//  }
//
//  def toPortValue(as1: Int, as2: Int, b: Int, p: Int): Int = {
//    loggerRequire(as1>=0 && as1<=20, "as1 is out of range: {}", as1.toString)
//    loggerRequire(as2>=0 && as2<=99, "as2 is out of range: {}", as2.toString)
//    loggerRequire(b  >=0 && b  <=999, "b is out of range: {}", b.toString)
//    loggerRequire(p  >=0 && p  <=999, "p is out of range: {}", p.toString)
//    ((as1*100 + as2)*1000 + b)*1000 + p
//  }
//
//  def fromPortValue(portValue: Int): (Int, Int, Int, Int) = {
//    loggerRequire(portValue>=0 && portValue<2100000000, "portValue is out of range: {}", portValue.toString)
//    var temp = portValue
//    val p = temp % 1000
//      temp /= 1000
//    val b = temp % 1000
//      temp /= 1000
//    val as2 = temp % 100
//      temp /= 100
//    val as1 = temp % 100
//      temp /= 100
//    (as1, as2, b, p)
//  }
//
//  // </editor-fold>
//
//  override val recordBytes = 184
//
//  override def loadImpl(file: File): Array[NNElement] = {
//
//    fHand = new RandomAccessFile(file, "r")(ByteConverterLittleEndian)
//
//
//    // <editor-fold desc="parse the Neuralynx header">
//
//    nlxHeaderLoad()
//
//    //this record size is corrupt!!!
//    //val tempRecordSize = nlxHeaderParserI("RecordSize", "0")
//
//    // </editor-fold>
//
//    fHand.seek( headerBytes  )
//    val eventMap = mutable.HashMap[Long, NNEvent]()
//
//    // <editor-fold defaultstate="collapsed" desc=" read loop ">
//
//    val xEventsReturn = new NNEvents()
//    val eventStarts = new mutable.HashMap[Int, (Long, Int, String)]()
//
//    //var rec = 0
//    var break = try {
//      fHand.readInt8()
//      false
//    } catch {
//      case ioe: IOException => true
//      case _: Throwable => true
//    }
//
//    while(!break){
//
//      //skip nstx(reserved), npkt_id(ID for originating system), npkt_data_size(==2)
//      fHand.skipBytes(6-1)
//      //cheetah timestamp in microseconds, shifted from unsigned long range to regular Long range
//      val qwTimeStamp = fHand.readUInt64Shifted()
//      //skip nevent_id(ID value for this event)
//      fHand.skipBytes(2)
//      //Decimal TTL value read from the TTL input port
//      val nttl = fHand.readInt16()
//      //skip ncrc, ndummy1, ndummy2, dnExtra
//      fHand.skipBytes(38)//readInt16(3) //readInt32(8)
//      //EventString
//      val eventString = new String(fHand.readUInt8(128).filterNot(_ == 0).map(_.toChar))
//      val portValue = toPortValue(eventString)
//
//      if(eventStarts.contains(portValue)){
//        //if the port was already triggered before
//        val prevTSCode = eventStarts(portValue)
//        if(nttl == 0) {
//          //if we have zero now, the previous trigger was a start, and this trigger is an end
//          //log the previous trigger with the duration
//          xEventsReturn.addEvent(portValue -> new NNEvent(prevTSCode._1, qwTimeStamp - prevTSCode._1, prevTSCode._2, prevTSCode._3))
//          //and delete the prior trigger entry
//          eventStarts.-=(portValue)
//        } else {
//          //if we have a non-zero value again, the previous trigger was a 0 duration event
//          //log the previous trigger as a 0 duration event
//          xEventsReturn.addEvent(portValue -> new NNEvent(prevTSCode._1, 0L,                          prevTSCode._2, prevTSCode._3))
//          //and log(overwrite) a new start event
//          eventStarts.+=( portValue -> (qwTimeStamp, nttl.toInt, eventString) )
//        }
//      } else {
//        //if the port hasn't been triggered before...
//        //if current value is zero, just write it
//        if(nttl == 0) xEventsReturn.addEvent(portValue -> new NNEvent(qwTimeStamp, 0L, nttl, eventString))
//        //if current value is nonzero, wait until next
//        else eventStarts.+=( portValue -> (qwTimeStamp, nttl.toInt, eventString) )
//      }
//
//      break = try {
//        fHand.readInt8()
//        false
//      } catch {
//        case ioe: IOException => true
//        case _: Throwable => true
//      }
//
//    }
//
//    //process remaining trigger events as zero duration events
//    eventStarts.foreach(  (f: ((Int, (Long, Int, String)))) => {
//      xEventsReturn.addEvent(f._1 -> new NNEvent(f._2._1, 0L, f._2._2, f._2._3))
//    }  )
//
//
//    // </editor-fold>
//
//    logger.info( "FileAdapterNEV: loaded {} ", xEventsReturn )
//    Array[NNElement](xEventsReturn)
//
//  }
//
//}
