package nounou.elements

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

import nounou.elements.layouts.NNDataLayoutHexagonal
import nounou.util.LoggingExt

/** Base class for all data elements.
  */
abstract class NNElement extends LoggingExt {
  //ToDo3: consider add: pt info, rec info, rec start time/date, etc
  val className = this.getClass.getCanonicalName
  //This temporary val is necessary to trigger initialization of `object NNGit`
  @transient private lazy val nnGitObj = nounou.util.NNGit
  val gitHead = nnGitObj.getGitHead
  @transient lazy val gitHeadShort = gitHead.take(10)
  val version = nounou.version

  def toJsonString: String = nounou.gson.toJson( this )
  def toStringFull(): String = {
    var tempout = toString().dropRight(1) + s"$gitHeadShort)/n" //+
    //"============================================================/n" +
    tempout.dropRight(1)
  }
  def isCompatible(that: NNElement): Boolean
  final def isCompatible(that: Seq[NNElement]): Boolean = that.forall( this.isCompatible(_) )

}

class NNElementDeserializeIntermediate {
  var className: String = ""
  var gitHead: String = ""
}


object NNElement {

  def loadJson(file: File): NNElement = {
    val gsonString = Files.readAllLines( file.toPath, StandardCharsets.UTF_8 ).toArray.mkString("\n")
    val tempObj = nounou.gson.fromJson( gsonString, classOf[NNElementDeserializeIntermediate] )
    val targetClass = Class.forName(tempObj.className)
    val tempret = nounou.gson.fromJson( gsonString, targetClass ).asInstanceOf[NNElement]//targetClass.type]
    //the following casting is not elegant, but seems necessary to satisfy the scala compiler,
    //which doesn't seem to be able to infer the type of XXXXX.asInstanceOf[targetClass.type]
    tempret match {
      case x: NNDataLayoutHexagonal => x.asInstanceOf[NNDataLayoutHexagonal]
      case x: NNElement => x
    }
  }

}