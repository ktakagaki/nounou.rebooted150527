package nounou.io

import java.util.ServiceLoader
//import scala.collection.JavaConverters._

import org.scalatest.FunSuite

/**
 * Created by ktakagaki on 15/03/24.
 */
class FileLoaderTest extends FunSuite {

  test("reading META-INF/services") {

    val loader = ServiceLoader.load(classOf[FileLoader]).iterator //.asScala

    //val loader2 = ServiceLoader.load( classOf[NNElement].getClass )
    //loader.toList.toString
    //loader.reload()
    //(for(l <- loader) yield l).toList
    assert(loader.hasNext, "FileLoaders must be accessible!")
    while( loader.hasNext ){
      println(loader.next().getClass.getName)
    }

  }

}
