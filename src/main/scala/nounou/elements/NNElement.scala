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
  /**'''[NNElement]''' getCanonicalName, buffered for serialization with GSON. */
  val className = this.getClass.getCanonicalName
  //This temporary val is necessary to trigger initialization of `object NNGit`
  @transient val nnGitObj = nounou.util.NNGit
  /**'''[NNElement]''' Git HEAD of the current revision, buffered for serialization with GSON.*/
  lazy val gitHead = nnGitObj.getGitHead
  /**'''[NNElement]''' Git HEAD shortened to first 10 characters.*/
  @transient lazy val gitHeadShort = gitHead.take(10)

  /**'''[NNElement]''' Reads global [[nounou]] version number, buffered for serialization with GSON.*/
  val version = nounou.version

  /**'''[NNElement]'''*/
  def toJsonString: String = nounou.gson.toJson( this )

  override def toString(): String = getClass.getName
  /**Output string with short git head. Each implementation (eg [[nounou.elements.data.filters.NNDataFilter]]
    * objects should update this to provide information specific to the specific filter, etc.
    */
  def toStringFull(): String = {
    var tempout = toString().dropRight(1) + s"$gitHeadShort)/n" //+
    //"============================================================/n" +
    tempout.dropRight(1)
  }

  /** __'''SHOULD OVERRIDE'''____ Whether an NNElement is compatible with another for merging, etc.
    */
  def isCompatible(that: NNElement): Boolean
  /** __'''SHOULD OVERRIDE'''____ Whether NNElements are compatible with another for merging, etc.
    */
  final def isCompatible(that: Seq[NNElement]): Boolean = that.forall( this.isCompatible(_) )

}

class NNElementDeserializeIntermediate {
  var className: String = ""
  var gitHead: String = ""
}


object NNElement {

  /** Loads this [[nounou.elements.NNElement]] object
    * from a JSON text file created via GSON, with correct class type obtained from
    * serialized className data.
    */
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