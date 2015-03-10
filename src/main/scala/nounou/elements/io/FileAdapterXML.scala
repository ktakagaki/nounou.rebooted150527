//package nounou.obj.io
//
//import java.io.File
//import nounou.obj.NNObject
//
//
///**
// * Created with IntelliJ IDEA.
// * User: takagaki
// * Date: 28.10.13
// * Time: 18:10
// * To change this template use File | Settings | File Templates.
// */
//class FileAdapterXML extends FileAdapter {
//
//  override val canWriteExt: List[String] = List[String]()
//  override def writeImpl(file: File, data: NNObject, options: OptFileAdapter) = writeCannotImpl(file, data, options)
//  override val canLoadExt: List[String] = List[String]( "xml" )
//  override def loadImpl(file: File): Array[NNObject] = loadCannotImpl(file)
//
//}
