import java.util.ServiceLoader
import nounou.io.FileLoader
import scala.collection.JavaConverters._

classOf[FileLoader].getCanonicalName()
classOf[FileLoader].getName()
classOf[FileLoader].getSimpleName()


val loader =  ServiceLoader.load( classOf[FileLoader] ).iterator.asScala
//ServiceLoader.load( classOf[NNElement].getClass )

loader.toList.toString
"This does not work in the worksheet! Use real run!"
//loader.reload()
//(for(l <- loader) yield l).toList

//val resources = getClass().getClassLoader().getResources("META-INF/services/nounou.io.FileLoader")
//var output = "";
//while(resources.hasMoreElements){
//  output = output + ";\n " + resources.nextElement().toString
//}
//output

