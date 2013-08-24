package nounous.data

import java.io.File
import scalafx.stage.FileChooser.ExtensionFilter

abstract class Reader {

  def read(): List[Source]   =  {
    val fileChooser = new scalafx.stage.FileChooser
    //val extFilter = new ExtensionFilter
    fileChooser.showOpenDialog(null)

    val filesChosen: List[File] = fileChooser.showOpenMultipleDialog(null).toList
    read(filesChosen)
  }
  def read(fileName: String): Source = read(new File(fileName))
  def read(file: File): Source

  //ToDo solve erasure problem and reinstate
  //  def read(fileNames: List[String]): List[Source] = read( fileNames.map( new File(_) ) )

  def read(files: List[File]): List[Source] ={
    var tempret = List[Source]()
    
    if(files.isEmpty || files(0) == null ){
      throw new Exception("called with empty file list!")
    } else {
      tempret = read( files(0) ) :: tempret
      readAndCompare(files.tail)
    }

    def readAndCompare(rest: List[File]): List[File] = {
      if(rest.isEmpty) rest
      else {
        val newSource = read( rest.head )
        
        if(tempret.head.isCompatible(newSource)) {
          tempret = newSource :: tempret
          readAndCompare(rest.tail)
        } else {
          throw new Error (rest.head + " is not compatible with the previously read files!")
        }
      }
    }

    tempret
  }
  
  
}