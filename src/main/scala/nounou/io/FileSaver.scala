package nounou.io

import java.io.File
import java.util.ServiceLoader

import nounou.util.LoggingExt
import scala.collection.JavaConverters._
import nounou.elements.NNElement
import scala.collection.mutable

/**This trait marks individual file adapter classes as being able to handle
  * the saving of certain objects.
  *
  * Created by ktakagaki on 15/03/24.
  */
trait FileSaver {

//  /**Factory method returning single instance.*/
//  def create(): FileLoader

  /** Whether a certain class can be saved, implement with match.*/
  def canSaveClass(obj: NNElement): Boolean

//  /** A list of lower-case extensions which can be saved.*/
//  val canSaveExtensions: Array[String]
//  final def canSaveFile(file: File): Boolean = canSaveFile( file.getName )
//  final def canSaveFile(fileName: String): Boolean = canSaveFile( nounou.util.getFileExtension(fileName) )
//  /**Whether the given extension can be saved. For now, based simply on a match with the file extension.*/
//  final def canSaveExtensions(extension: String): Boolean = canSaveExtensions.contains( extension.toLowerCase )

  /**Actual saving of file.
    * @param fileName if the filename does not end with the correct extension, it will be appended. If it exists, it will be given a postscript.
    */
  def save(data: Array[NNElement], fileName: String): Unit

}

object FileSaver extends LoggingExt {
  private lazy val savers = ServiceLoader.load(classOf[FileSaver]).iterator.asScala
  private val possibleSaverBuffer = new mutable.HashMap[String, FileSaver]()

  final def save(data: Array[NNElement], fileName: String): Unit = {
    val ext = nounou.util.getFileExtension(fileName)
//    val saver = possibleSaverBuffer.get(ext) match {
//      //If the loader for this extension has already been loaded
//      case l: Some[FileSaver] => l.get
//      case _ => {
//        val possibleSavers: Iterator[FileSaver] = savers.filter( _.canSaveFile(fileName))
//        val possibleSaver = if( possibleSavers.hasNext ){
//          val tempret = possibleSavers.next
//          if( possibleSavers.hasNext ) {
//            logger.info(s"Multiple possible savers for file $fileName found. Will take first instance, ${tempret.getClass.getName}")
//          }
//          tempret
//        } else {
//          throw loggerError(s"Cannot find saver for file: $fileName")
//        }
//        possibleSaverBuffer.+=( (ext, possibleSaver) )
//        possibleSaver
//      }
//    }
//    saver.save(data, fileName)
  }

}