package nounous.reader

import nounous.data.{XSpikes, XEvents}
import java.io.File
import nounous.data.X
import scalafx.stage.FileChooser.ExtensionFilter

abstract class Reader {

  //ToDo: some sort of function to notify which file extensions can be read

  def read(): List[X]   =  {
    val fileChooser = new scalafx.stage.FileChooser
    //val extFilter = new ExtensionFilter
    val window = new scalafx.stage.Popup
    fileChooser.showOpenDialog(window)

    val filesChosen: List[File] = fileChooser.showOpenMultipleDialog(null).toList
    read(filesChosen)
  }

  //ToDo consolidate data where different channels are on different files
  def read(files: List[File]):  List[X] = {
    files.flatMap(read(_))
  }
  def read(file: String): List[X] = read(new File(file))

  def read(file: File): List[X]

}




//ToDo solve erasure problem and reinstate
//def read(fileNames: List[String]): List[X] = read( fileNames.map( new File(_) ) )

//  {
//    if(files.isEmpty/* || files(0) == null*/ ){
//      //throw new Exception("called with empty file list!")
//    } else {
//      for(f <- files) tempret = tempret ::: readImpl( f )
//    }
//
//    tempret
//  }
