package nounou.elements

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.TreeSet
import scala.collection.immutable.TreeMap

/**Database to encapsulate marked events in data recording.
  * Events are stored as [[NNEvent]] objects, which encapsulate timestamp, duration, code, and comment string.
  * An [[NNEvents]] database consists of TreeSet(s) of XEvents.
  * Each event "port" has its own TreeSet---this allows different ports to have events with exactly the same timing.
  * (TreeSet's with black-red trees cannot support keys with equivalent sorting values)
  * @author ktakagaki
 */
class NNEvents extends NNElement {

  private var _database: TreeMap[Int, TreeSet[NNEvent]] = new TreeMap[Int, TreeSet[NNEvent]]()

  def lengths: Array[Int] = _database.values.map( _.size ).toArray
  def ports: Array[Int] = _database.keys.toArray
  def portCount: Int = _database.size

  def addEvent( portEvent: (Int, NNEvent) ): Unit = addEvent(portEvent._1, portEvent._2)
  def addEvent( port: Int, xEvent: NNEvent ): Unit = {
    loggerRequire(port >= 0, "port specification {} must be >= zero!", port.toString)
    if( !_database.contains(port) ){
      _database = _database.+(port -> new TreeSet[NNEvent]())
    }
    _database(port).+=(xEvent)
  }

  // <editor-fold defaultstate="collapsed" desc=" filterByPort/filterByPortCode ">

  def filterByPort(port: Int): TreeSet[NNEvent] = {
    if( _database.contains(port) ) _database(port)
    else new TreeSet[NNEvent]()
  }
  def filterByPortA(port: Int) = filterByPort(port).toArray

  def filterByPortCode(port: Int, code: Int): TreeSet[NNEvent] = {
    filterByPort(port).filter( p => p.code == code )
  }
  def filterByPortCodeA(port: Int, code: Int) = filterByPortCode(port, code).toArray

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" expandZeroEvents ">

  def expandZeroEvents(): Unit = {
    _database = _database.map( ( ev:(Int, TreeSet[NNEvent]) ) => ( ev._1, {
      var tempTreeSet = TreeSet[NNEvent]()
      ev._2.foreach(
        (x: NNEvent) => {
          if (x.duration != 0) {
            tempTreeSet = tempTreeSet + new NNEvent(x.timestamp, 0L, x.code, x.comment)
            tempTreeSet = tempTreeSet + new NNEvent(x.timestamp + x.duration, 0L, 0, "nounou: expanded reset")
          }
          else tempTreeSet = tempTreeSet + x
        }
      )
      tempTreeSet}   )
    )
  }

  // </editor-fold>


  // <editor-fold defaultstate="collapsed" desc=" toArray/toArrayArray ">

  def toArray(): Array[(Int, NNEvent)] = {
    val tempret: ArrayBuffer[(Int, NNEvent)] = new ArrayBuffer[(Int, NNEvent)]()
    _database.keys.foreach( key => _database(key).foreach( event => tempret.+=( (key, event) ) ) )
    tempret.toArray
  }

  // </editor-fold>


//  lazy val maxDuration: Long = events.map( _._2.duration).max
//  lazy val uniqueEventCodes = events.map(_._2.code).toList.distinct.toVector
//  lazy val sortedEvents = new Array[TreeMap[Long,XEvent]]( uniqueEventCodes.length )

  //def nextEvent(timeStamp: Long): XEvent
  //def nextEvent(timeStamp: Long, eventCode: Int): XEvent
  //def previousEvent(timeStamp: Long): XEvent
  //def getEvents(timeStamp0: Long, timeStamp1: Long): Vector[XEvent]
  //def getEvents(timeStamp: Long): Vector[XEvent]
  //def getEventList: Vector[XEvent]
  //def containsEvent(timeStamp1: Long, timeStamp2: Long): Boolean

  // <editor-fold desc="XConcatenatable">

  //override def :::(that: NNElement): NNEvents = ???
//    that match {
//      case x: XEvents => {
//        new XEvents( this._database ++ x._database)
//      }
//      case _ => {
//        require(false, "cannot concatenate different types!")
//        this
//      }
//    }
//  }

  override def isCompatible(that: NNElement): Boolean =
    that match {
      case x: NNEvents => true
//      {
//        (super[XDiscrete].isCompatible(x) && super[XDiscrete].isCompatible(x))
//      }
      case _ => false
    }

  // </editor-fold>

  override def toString() = {
    "XEvents( "// + length + " events total" + " )"
  }

}