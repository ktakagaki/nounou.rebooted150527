package nounou.data.io

import nounou.Opt
import nounou.util.LoggingExt
import java.io.File
import nounou.data.X
import nounou.data.ranges.{SampleRangeSpecifier, SampleRangeAll, FrRange$}


//object FileAdapter {
//
//  final val loaders = new mutable.HashMap[String, FileAdapter]
//  final val writers = new mutable.HashMap[String, FileAdapter]
//
//}


trait FileAdapter extends LoggingExt {

  /** Must be overridden, list of extensions (in lower case) which can be read.
    */
  val canLoadExt: Array[String]
  /** Must be overridden, list of extensions (in upper case) which can be read.
    */
  val canWriteExt: Array[String]

//  //Adds loader to program loader library
//  FileAdapter.loaders.++=( canLoadExt.map( str => (str, this)) )
//  //Adds writer to program loader library
//  FileAdapter.writers.++=( canWriteExt.map( str => (str, this)) )



  /** The minimal requirement which a file loader must satisfy. Default is to throw error (i.e. cannot load files;
    * used for writer objects.)
    */
  def loadImpl(file: File): Array[X]
  def loadCannotImpl(file: File) = {
    loggerError("Loading of file {} is not specified in this particular reader!", file)
    throw new IllegalArgumentException
  }
  final def loadImpl(fileName: String): Array[X] = loadImpl( new File(fileName) )



  // <editor-fold defaultstate="collapsed" desc=" loading ">

  trait CanLoad[FileSpec]{
    def apply(file: FileSpec): Array[X]
  }


  /** Load a certain file/filename/files/filenames. Will throw error if not readable with this particular adaptor
    *
    */
  def load[Input](list: Input)(implicit canLoad: CanLoad[Input]): Array[X] = canLoad(list)


  implicit val canLoadFile: CanLoad[File] = new CanLoad[File] {
    def apply(file: File): Array[X] = loadImpl(file)
  }
  implicit val canLoadString: CanLoad[String] = new CanLoad[String] {
    def apply(string: String): Array[X] = loadImpl( new File(string) )
  }

  implicit val canLoadFiles: CanLoad[List[File]] = new CanLoad[List[File]] {
    def apply(list: List[File]): Array[X] = {
      list.flatMap( loadImpl(_) ).toArray
    }
  }

  implicit val canLoadStrings: CanLoad[List[String]] = new CanLoad[List[String]] {
    def apply(list: List[String]): Array[X] = {
      load( list.map(new File(_)) )
    }
  }

  // </editor-fold>

//  /** Inheriting classes will implement data writers by implementing implicit instances of this trait.
//    * This design pattern allows the write( fileName, data ) to take multiple forms of "data."
//    * Some adapters might write only XData, others XEvents....
//    *
//    * @tparam Data
//    */
//  trait CanWrite[Data]{
//    def apply(fileName: String, data: Data, opts: OptFileAdapter)
//  }

  /** The minimal requirement which a file loader must satisfy. Default is to throw error (i.e. cannot load files;
    * used for writer objects.)
    */
  def writeImpl(file: File, data: X, options: OptFileAdapter): Unit
  def writeCannotImpl(file: File, data: X, options: OptFileAdapter): Unit = {
    loggerError("Loading of file {} is not specified in this particular writer!", file)
    throw new IllegalArgumentException
  }

  //final def write(file: File, data: List[X]): Unit = writeImpl(file: File, data: List[X], OptFileAdapter.Automatic)
  //final def write(fileName: String, data: List[X]): Unit = writeImpl( new File(fileName), data, OptFileAdapter.Automatic )
  final def write(file: File, data: X): Unit = writeImpl(file: File, data, OptFileAdapter.Automatic)
  final def write(fileName: String, data: X): Unit = writeImpl( new File(fileName), data, OptFileAdapter.Automatic )


//  final def write[Data](fileName: String, data: Data, options: OptFileAdapter = OptFileAdapter.Automatic)(implicit canWrite: CanWrite[Data]): Unit = {
//    canWrite.apply(fileName, data, options)
//  }
//  final def write[Data](file: File, data: Data, options: OptFileAdapter = OptFileAdapter.Automatic)(implicit canWrite: CanWrite[Data]): Unit = {
//    canWrite(file.getCanonicalPath, data, options)
//  }



//  implicit val canWriteXList: CanWrite[List[X]] = new CanWrite[List[X]] {
//    def apply(fileName: String, data: List[X], opt: OptFileAdapter): Unit = {
//      opt match {
//        case x: OptFileAdapter.ListOpt => {
//          loggerRequire( data.length == x.list.length, "you must specify as many options as data X elements (even if it is OptFileAdapter.Automatic)")
//          for(cnt <- 0 to data.length) write( fileName, data(cnt), x.list.apply( cnt ) )
//        }
//        case x: OptFileAdapter => {
//          for(cnt <- data) write( fileName, cnt, x )
//        }
//      }
//
//    }
//  }
//
//  //this implicit value will provide the fallback write error for all children
//  implicit val cannotWriteX: CanWrite[X] = new CanWrite[X] {
//    def apply(fileName: String, data: X, opt: OptFileAdapter): Unit = {
//      loggerError("This writer object {} cannot write X type {}!", this.getClass.getName, data.getClass.getName )
//    }
//  }
//  /** The minimal requirement which a file writer must satisfy. Default is to throw error (i.e. cannot write files;
//    * used for writer objects.)
//    */
//  def writeImpl(file: File, data: List[X]): Unit = {
//    loggerError("Writing of file {} is not specified in this particular writer!", file)
//  }

}



class OptFileAdapter extends Opt

object OptFileAdapter{
  case object Automatic extends OptFileAdapter
  case class XDataFrames(range: SampleRangeSpecifier = SampleRangeAll()) extends OptFileAdapter
  //case class ListOpt(list: List[OptFileAdapter]) extends OptFileAdapter
}
