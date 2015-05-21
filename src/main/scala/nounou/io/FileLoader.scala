package nounou.io

import java.io.File
import java.util.ServiceLoader

import nounou.util.LoggingExt
import scala.collection.JavaConverters._
import nounou.elements.NNElement
import scala.collection.mutable

/**This trait marks individual file adapter classes as being able to handle
  * the loading of certain file extensions.
  *
 * Created by ktakagaki on 15/03/24.
 */
trait FileLoader {

//  /**Factory method returning single instance.*/
//  def create(): FileLoader

  /** A list of lower-case extensions which can be loaded.*/
  val canLoadExtensions: Array[String]
  /**Whether the given file can be loaded. For now, based simply on a match with the file extension.*/
  final def canLoadFile(file: File): Boolean = canLoadFile( file.getName )
  /**Whether the given file can be loaded. For now, based simply on a match with the file extension.*/
  final def canLoadFile(fileName: String): Boolean = canLoadExtension( nounou.util.getFileExtension(fileName) )
  /**Whether the given extension can be loaded. For now, based simply on a match with the file extension.*/
  final def canLoadExtension(extension: String): Boolean = canLoadExtensions.contains( extension.toLowerCase )
  /**Actual loading of file.*/
  def load(file: File): Array[NNElement]
  /**Actual loading of file.*/
  final def load(fileName: String): Array[NNElement] = load( new File(fileName) )

}

object FileLoader extends LoggingExt {
  private lazy val loaders = ServiceLoader.load(classOf[FileLoader]).iterator.asScala
  private val possibleLoaderBuffer = new mutable.HashMap[String, FileLoader]()

  final def load(fileName: String): Array[NNElement] = {
    val ext = nounou.util.getFileExtension(fileName)
    val loader = possibleLoaderBuffer.get(ext) match {
      //If the loader for this extension has already been loaded
      case l: Some[FileLoader] => l.get
      case _ => {
        val possibleLoaders: Iterator[FileLoader] = loaders.filter( _.canLoadFile(fileName))
        val possibleLoader = if( possibleLoaders.hasNext ){
          val tempret = possibleLoaders.next
          if( possibleLoaders.hasNext ) {
            logger.info(s"Multiple possible loaders for file $fileName found. Will take first instance, ${tempret.getClass.getName}")
          }
          tempret
        } else {
          throw loggerError(s"Cannot find loader for file: $fileName")
        }
        possibleLoaderBuffer.+=( (ext, possibleLoader) )
        possibleLoader
      }
    }
    loader.load(fileName)
  }

}