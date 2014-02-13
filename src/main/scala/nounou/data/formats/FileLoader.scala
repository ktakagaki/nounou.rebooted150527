package nounou.data.formats

import java.io.File
import nounou.data.X
import com.typesafe.scalalogging.slf4j.Logging



trait FileLoader extends Logging {
  //ToDo 3: some sort of function to notify which file extensions can be loaded

  def loadImpl(file: File): List[X]


  def load[Input](list: Input)(implicit canLoad: CanLoad[Input]): List[X] = canLoad(list)




  trait CanLoad[Input]{
    def apply(list: Input): List[X]
  }

  object CanLoad {
    implicit val canLoadFile: CanLoad[File] = new CanLoad[File] {
      def apply(file: File): List[X] = loadImpl(file)
    }
    implicit val canLoadString: CanLoad[String] = new CanLoad[String] {
      def apply(string: String): List[X] = loadImpl( new File(string) )
    }

    implicit val canLoadFiles: CanLoad[List[File]] = new CanLoad[List[File]] {
      def apply(list: List[File]): List[X] = {
        list.flatMap( loadImpl(_) )
      }
    }

    implicit val canLoadStrings: CanLoad[List[String]] = new CanLoad[List[String]] {
      def apply(list: List[String]): List[X] = {
        load( list.map(new File(_)) )
      }
    }
  }

}
