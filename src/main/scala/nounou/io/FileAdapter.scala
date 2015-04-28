//package nounou.io
//
//import nounou.Opt
//import nounou.util.LoggingExt
//import java.io.File
//import nounou.elements.NNElement
//
//
////object FileAdapter {
////
////  final val writers = new mutable.HashMap[String, FileAdapter]
////
////}
//
//
//trait FileAdapter extends LoggingExt {
//
//  /** Must be overridden, list of extensions (in lower case) which can be read.
//    */
//  val canLoadExt: Array[String]
//  /** Must be overridden, list of extensions (in upper case) which can be read.
//    */
//  val canWriteExt: Array[String]
//
////  //Adds loader to program loader library
////  FileAdapter.loaders.++=( canLoadExt.map( str => (str, this)) )
////  //Adds writer to program loader library
////  FileAdapter.writers.++=( canWriteExt.map( str => (str, this)) )
//
//
//
//  /** The minimal requirement which a file loader must satisfy. Default is to throw error (i.e. cannot load files;
//    * used for writer objects.)
//    */
//  def loadImpl(file: File): Array[NNElement]
//  def loadCannotImpl(file: File) = {
//    loggerError("Loading of file {} is not specified in this particular reader!", file)
//    throw new IllegalArgumentException
//  }
//  final def loadImpl(fileName: String): Array[NNElement] = loadImpl( new File(fileName) )
//
//
//
//  // <editor-fold defaultstate="collapsed" desc=" loading ">
//
//  trait CanLoad[FileSpec]{
//    def apply(file: FileSpec): Array[NNElement]
//  }
//
//  /** Load a certain file/filename/files/filenames. Will throw error if not readable with this particular adaptor.
//    * This function is programmed with an implicit loading type, so that it can handle both Array[String]
//    * and Array[File] (both have the same type specification in flat Java.
//    */
//  def load[Input](data: Input)(implicit canLoad: CanLoad[Input]): Array[NNElement] = canLoad(data)
//
//
//  implicit val canLoadFile: CanLoad[File] = new CanLoad[File] {
//    def apply(file: File): Array[NNElement] = loadImpl(file)
//  }
//  implicit val canLoadString: CanLoad[String] = new CanLoad[String] {
//    def apply(string: String): Array[NNElement] = loadImpl( new File(string) )
//  }
//
//  implicit val canLoadFiles: CanLoad[Array[File]] = new CanLoad[Array[File]] {
//    def apply(list: Array[File]): Array[NNElement] = {
//      list.flatMap( loadImpl(_) ).toArray
//    }
//  }
//
//  implicit val canLoadStrings: CanLoad[Array[String]] = new CanLoad[Array[String]] {
//    def apply(list: Array[String]): Array[NNElement] = {
//      load( list.map(new File(_)) )
//    }
//  }
//
//  // </editor-fold>
//
//  //Writing and saving will be delegated to the actual class to be written or saved.
//
////  /** Inheriting classes will implement data writers by implementing implicit instances of this trait.
////    * This design pattern allows the write( fileName, data ) to take multiple forms of "data."
////    * Some adapters might write only XData, others XEvents....
////    *
////    * @tparam Data
////    */
////  trait CanWrite[Data]{
////    def apply(data: Data, fileName: String/*, options: OptFileAdapter*/)
////  }
////
////  final def save(data: X, fileName: String): Unit = saveImpl(data, fileName/*, options: OptFileAdapter*/)
////
////  /** The minimal requirement which a file loader must satisfy. Default is to throw error (i.e. cannot load files;
////    * used for writer objects.)
////    */
////  def saveImpl(data: X, fileName: String/*, options: OptFileAdapter*/): Unit
////
////
////
//////  final def write[Data](fileName: String, data: Data, options: OptFileAdapter = OptFileAdapter.Automatic)(implicit canWrite: CanWrite[Data]): Unit = {
//////    canWrite.apply(fileName, data, options)
//////  }
//////  final def write[Data](file: File, data: Data, options: OptFileAdapter = OptFileAdapter.Automatic)(implicit canWrite: CanWrite[Data]): Unit = {
//////    canWrite(file.getCanonicalPath, data, options)
//////  }
////
////
////
//////  implicit val canWriteXList: CanWrite[List[X]] = new CanWrite[List[X]] {
//////    def apply(fileName: String, data: List[X], opt: OptFileAdapter): Unit = {
//////      opt match {
//////        case x: OptFileAdapter.ListOpt => {
//////          loggerRequire( data.length == x.list.length, "you must specify as many options as data X elements (even if it is OptFileAdapter.Automatic)")
//////          for(cnt <- 0 to data.length) write( fileName, data(cnt), x.list.apply( cnt ) )
//////        }
//////        case x: OptFileAdapter => {
//////          for(cnt <- data) write( fileName, cnt, x )
//////        }
//////      }
//////
//////    }
//////  }
//////
//////  //this implicit value will provide the fallback write error for all children
//////  implicit val cannotWriteX: CanWrite[X] = new CanWrite[X] {
//////    def apply(fileName: String, data: X, opt: OptFileAdapter): Unit = {
//////      loggerError("This writer object {} cannot write X type {}!", this.getClass.getName, data.getClass.getName )
//////    }
//////  }
//////  /** The minimal requirement which a file writer must satisfy. Default is to throw error (i.e. cannot write files;
//////    * used for writer objects.)
//////    */
//////  def writeImpl(file: File, data: List[X]): Unit = {
//////    loggerError("Writing of file {} is not specified in this particular writer!", file)
//////  }
//
//}
//
////
////
////class OptFileAdapter extends Opt
////
////object OptFileAdapter{
////  case object Automatic extends OptFileAdapter
////  case class XDataFrames(range: SampleRangeSpecifier = SampleRangeAll()) extends OptFileAdapter
////  //case class ListOpt(list: List[OptFileAdapter]) extends OptFileAdapter
////}
