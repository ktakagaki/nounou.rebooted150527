package nounous.data

import java.io.File
import scalafx.stage.FileChooser.ExtensionFilter

abstract class Reader {

  //ToDo: some sort of function to notify which file extensions can be read

  def read(): List[Source]   =  {
    val fileChooser = new scalafx.stage.FileChooser
    //val extFilter = new ExtensionFilter
    fileChooser.showOpenDialog(null)

    val filesChosen: List[File] = fileChooser.showOpenMultipleDialog(null).toList
    read(filesChosen)
  }
  def read(file: File): List[Source] = readImpl(file)
  def read(fileName: String): List[Source] = read(new File(fileName))

  //ToDo solve erasure problem and reinstate
  //def read(fileNames: List[String]): List[Source] = read( fileNames.map( new File(_) ) )

  //ToDo consolidate data where different channels are on different files
  def read(files: List[File]): List[Source] ={
    var tempret = List[Source]()
    
    if(files.isEmpty || files(0) == null ){
      throw new Exception("called with empty file list!")
    } else {
      for(f <- files) tempret = tempret ::: readImpl( f )
    }

    tempret
  }

  def readImpl(file: File): List[Source]
  
  
}
