
import nounou.NN
import nounou.util.NNGit
import org.apache.commons.io.IOUtils

val url = NN.getClass.getClassLoader.getResource("NNGit.gson.txt")
url.getContent
url.getPath
val file = new java.io.File( url.getPath )
file.exists()

val stream = NN.getClass.getClassLoader.getResourceAsStream("NNGit.gson.txt")
IOUtils.toString(stream, "UTF-8")
stream.toString

