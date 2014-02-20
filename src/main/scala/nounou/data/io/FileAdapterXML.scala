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
  override val canLoadExt: List[String] = List[String]( "xml" )

  /** The minimal requirement which a file loader must satisfy. Default is to throw error (i.e. cannot load files;
    * used for writer objects.)
    */
  override def loadImpl(file: File): List[X] = ???
}
