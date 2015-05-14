package nounou.util

import java.io.{FileWriter, BufferedWriter, FileReader}
import java.nio.charset.StandardCharsets
import java.nio.file.Files

import com.google.gson.Gson
import nounou.NN
import org.apache.commons.io.IOUtils
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

/**This object uses jgit to read current Git head information, via the
  * companion class [[NNGit]]. There is no real need for this to be a
  * companion object, just that it is more aesthetically pleasing that way.
  *
  * All serialized objects should be tagged with this Git information
  * for reproducibility.
 */
object NNGit {

  @transient val jsonFileName = "NNGit.gson.txt"

  /**File for saved git information. It will not be accessible at this path
    * (i.e. jsonFile.exists == false) if this library is accessed from a .jar file.
    *
    * This file will be overwritten with new information if this object is initialized
    * from within an active Git repository ( contained in "./.git" ).
    * */
  @transient val jsonFile = new java.io.File( "./src/main/resources/" + jsonFileName)

  /** Resource stream for pre-serialized Git version information.
    * It will be null if the resource does not exist.
    * This resource stream will be used to read Git information if this
    * library is accessed from within a *.jar file.
    * Use of stream (and not java.io.File) is necessary to read from within a compressed jar file.
    */
  @transient val jsonStream = NN.getClass.getClassLoader.getResourceAsStream(jsonFileName)

  /**Current repository in "./.git". Repository will be invalid if this library is accessed via
    * jar file (repository.getRef("HEAD") == null). In this case,
    * [[jsonStream]] will be used as the source for pre-serialized
    * Git information.
   */
  @transient private val repository = (new FileRepositoryBuilder()).
    setGitDir( new java.io.File( "./.git" ) ). //( null )
    readEnvironment().findGitDir().build()

  /**Whether the info was loaded from a repository*/
  @transient private var repoLoaded: Boolean = false
  /**Whether the info was loaded from a file*/
  @transient private var fileLoaded: Boolean = false

  @transient
  private val obj: NNGit = {

    //If not in active git repository, see if the Git info has been previously serialized
    if( repository.getRef("HEAD") == null /*repository.isBare*/ ) {
      repoLoaded = false

      //return either the deserialized object or empty (if file not present)
      if( jsonFile.exists ){
        fileLoaded = true
        nounou.gson.fromJson( Files.readAllLines(jsonFile.toPath, StandardCharsets.UTF_8).toArray.mkString("\n"),
          classOf[NNGit]   )
      } else if (jsonStream != null ){
        fileLoaded = true
        nounou.gson.fromJson(  IOUtils.toString(jsonStream, "UTF-8"), classOf[NNGit])
      } else {
        fileLoaded = false
        //If no information available, default (empty) class is initialized
        new NNGit
      }

    }else{   //If valid git repository present, use it to get latest data

      val tempRet = new NNGit()
      tempRet.initializeFromRepository(repository)

      // write newest repo information to serialized file, if exists (do not do for *.jar/resource)
        if (jsonFile.exists()) jsonFile.delete
        jsonFile.createNewFile()
        val writer = new BufferedWriter( new FileWriter( jsonFile ) )
        writer.write( nounou.gson.toJson(tempRet) )
        writer.close

      repoLoaded = true
      fileLoaded = false
      //write latest repository information to serialization file
      tempRet

    }


  }

  def getGitHead: String = {
    if(obj==null) "NNGit is not yet initialized!"
    else obj.head
  }
//  def getGitHeadShort: String = {
//    if(obj==null) "NNGit is not yet initialized!"
//    else obj.headShort
//  }
  def gitRepoLoaded = repoLoaded
  def gitFileLoaded = fileLoaded

  def contentText() =
    "  + current HEAD is: " + obj.head + "\n" +
    "  + current branch is: " + obj.branch  + "\n" +
    "  + remote names are: " + obj.remotes.mkString(", ") + "\n"
  def repoText() = "GIT repo directory: " + obj.gitDirectory + "\n"
  def fileText() = "Last GIT info from file resource: " + jsonFileName + "\n"

  def infoPrintout() = {
    if(gitRepoLoaded) repoText() + contentText()
    else if(gitFileLoaded) fileText() + contentText()
    else s"Could not initialize GIT information with $jsonFile or current repository."
  }

}

/**This class will be serialized by gson.
 */
class NNGit  {

  var head: String = "Head not initialized"
//  var headShort: String = "Short head not initialized"
  var branch: String = "Branch not initialized"
  var remotes: Array[String] = Array("Remotes not initialized")
  var gitDirectory: String = "Git directory not initialized"

  /**Initializes the NNGit class based on a given input repository.
    * This initialization is NOT done in constructor, due to gson
    * requiring a no-argument constructor for correct serialization.
   */
  def initializeFromRepository(repository: Repository): Unit = {
    NN.loggerRequire(repository != null, "Called with null repository!" )
    head = repository.getRef("HEAD").getObjectId.name match {
      case x: String => x
      case _ => "Head not detected"
    }
//    headShort = head.take(10)
    branch = repository.getBranch match {
      case x: String => x
      case _ => "Head not detected"
    }
    val tempRemotes = repository.getConfig.getSubsections("remote").toArray.map( _.asInstanceOf[String] )
    //repository.getRemoteNames.toArray.map( _.asInstanceOf[String]
    remotes = tempRemotes.map(repository.getConfig.getString("remote", _, "url")) match {
      case x: Array[String] => x
      case _ => Array("Remotes not detected")
    }

    gitDirectory = repository.getDirectory.getCanonicalPath
  }

}

//Notes:
//  attempts at trying to access git information outside a *.jar
//  should not be necessary, since jar is packaged, should be reading pre-generated git head anyway.
//  println(NN.getClass.getProtectionDomain.getCodeSource.getLocation.toURI)
