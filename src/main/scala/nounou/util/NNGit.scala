package nounou.util

import java.io.{FileWriter, BufferedWriter, FileReader}
import java.nio.charset.StandardCharsets
import java.nio.file.Files

import com.google.gson.Gson
import nounou.NN
import org.apache.commons.io.IOUtils
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

/**
 * Created by ktakagaki on 15/04/02.
 */
object NNGit {

  /**Filename for git information serialization*/
  @transient val jsonFile = new java.io.File( "./src/main/resources/NNGit.gson.txt" )
//  @transient val jsonResource = {
//    val tempResource = getClass.getClassLoader.getResource("NNGit.gson.txt")
//
//    if(tempResource == null) null
//
//    else new java.io.File( tempResource.getPath() )
//  }
  @transient val jsonStream = NN.getClass.getClassLoader.getResourceAsStream("NNGit.gson.txt")

  @transient var repository = (new FileRepositoryBuilder()).
    setGitDir( new java.io.File( "./.git" ) ). //( null )
    readEnvironment().findGitDir().build()

//  @transient val gsonFile = new java.io.File(jsonFile)
//println("gsonFile exists: " + gsonFile.exists.toString)
//println("gsonFile: " + gsonFile.getCanonicalPath)
//println("gsonFile canRead: " + gsonFile.canRead)

  @transient private val gson = new Gson
  /**Whether the info was loaded from a repository*/
  @transient var repoLoaded: Boolean = false
  /**Whether the info was loaded from a file*/
  @transient var fileLoaded: Boolean = false

  @transient
  private val obj: NNGit = {

    //If not in active git repository, see if the Git info has been previously serialized
    if( repository.getRef("HEAD") == null /*repository.isBare*/ ) {
      repoLoaded = false

      //return either the deserialized object or empty (if file not present)
      if( jsonFile.exists ){
        fileLoaded = true
        gson.fromJson( Files.readAllLines(jsonFile.toPath, StandardCharsets.UTF_8).toArray.mkString("\n"),
          classOf[NNGit]   )
      } else if (jsonStream != null ){
        fileLoaded = true
        gson.fromJson(  IOUtils.toString(jsonStream, "UTF-8"), classOf[NNGit])
      } else {
        fileLoaded = false
        new NNGit
      }

    }else{   //If valid git repository present, use it to get latest data

      val tempRet = new NNGit()
      tempRet.initializeFromRepository(repository)

      // write newest repo information to serialized file, if exists (do not do for *.jar/resource)
        if (jsonFile.exists()) jsonFile.delete
        jsonFile.createNewFile()
        val writer = new BufferedWriter( new FileWriter( jsonFile ) )
        writer.write( gson.toJson(tempRet) )
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

  def gitRepoLoaded = repoLoaded
  def gitFileLoaded = fileLoaded

  def contentText() =
    "  + current HEAD is: " + getGitHead + "\n" +
    "  + current branch is: " + obj.branch  + "\n" +
    "  + remote names are: " + obj.remotes.mkString(", ") + "\n"
  def repoText() = "GIT repo directory: " + obj.gitDirectory + "\n"
  def fileText() = "Last GIT info from file: " + jsonFile + "\n"

  def infoPrintout() = {
    if(gitRepoLoaded) repoText() + contentText()
    else if(gitFileLoaded) fileText() + contentText()
    else s"Could not initialize GIT information with $jsonFile or current repository."
  }

}

class NNGit  {

  var head: String = "Head not initialized"
  var branch: String = "Branch not initialized"
  var remotes: Array[String] = Array("Remotes not initialized")
  var gitDirectory: String = "Git directory not initialized"

  def initializeFromRepository(repository: Repository): Unit = {
    NN.loggerRequire(repository != null, "Called with null repository!" )
    head = repository.getRef("HEAD").getObjectId.name match {
      case x: String => x
      case _ => "Head not detected"
    }
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
