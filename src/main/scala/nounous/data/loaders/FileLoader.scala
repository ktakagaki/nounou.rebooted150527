package nounous.reader

import java.io.File
import nounous.data.X

//import scalafx.stage.FileChooser.ExtensionFilter

trait FileLoader {

  //ToDo: some sort of function to notify which file extensions can be load

  final def load(): List[X]   =  {
    val fileChooser = new scalafx.stage.FileChooser
    //val extFilter = new ExtensionFilter
    val window = new scalafx.stage.Popup
    fileChooser.showOpenDialog(window)

    val filesChosen: List[File] = fileChooser.showOpenMultipleDialog(null).toList
    load(filesChosen)
  }

  //ToDo consolidate data where different channels are on different files
  final def load(files: List[File]):  List[X] = {
    files.flatMap(load(_))
  }
  final def read(file: String): List[X] = load(new File(file))

  def load(file: File): List[X]

}




//ToDo solve erasure problem and reinstate
//def load(fileNames: List[String]): List[X] = load( fileNames.map( new File(_) ) )

//  {
//    if(files.isEmpty/* || files(0) == null*/ ){
//      //throw new Exception("called with empty file list!")
//    } else {
//      for(f <- files) tempret = tempret ::: loadImpl( f )
//    }
//
//    tempret
//  }
