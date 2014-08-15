package nounou.data.io

import java.io.File
import nounou.data.X


/**
 * Created with IntelliJ IDEA.
 * User: takagaki
 * Date: 28.10.13
 * Time: 18:10
 * To change this template use File | Settings | File Templates.
 */
class FileAdapterXML extends FileAdapter {

  override val canWriteExt: List[String] = List[String]()
  override def writeImpl(file: File, data: X, options: OptFileAdapter) = writeCannotImpl(file, data, options)
  override val canLoadExt: List[String] = List[String]( "xml" )
  override def loadImpl(file: File): Array[X] = loadCannotImpl(file)

}
