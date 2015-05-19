package nounou

/**
 * @author ktakagaki
 * //@date 07/15/2014.
 */
package object util {

  // <editor-fold defaultstate="collapsed" desc=" forJava ">


  /** Provides a quick java-based for loop, avoiding Scala for-comprehensions
    *
    */
  def forJava(start: Int, endExclusive: Int, step: Int, function: (Int => Unit) ): Unit = {
    var count = start
    if( step>0 ) while( count < endExclusive){
      function(count)
      count = count + step
    } else if (step<0) while( count > endExclusive){
      function(count)
      count = count + step
    } else throw new IllegalArgumentException
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" isMatrix ">

  def isMatrix(data: Array[Array[Int]]): Boolean = {
    if(data==null) false
    else{
      data.length match {
        case 0 => false
        case 1 => if (data(0).length == 0) false else true
        case _ => {
          val templen0 = data(0).length
          if (templen0 == 0) false
          else {
            var temp = 1
            var tempres = true
            while (temp < data.length) {
              if (data(temp) == null || templen0 != data(temp).length){
                tempres = false
                temp = data.length
              }else{
                temp += 1
              }
            }
            tempres
          }
        }
      }
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" getFileExtensionCapital ">

  def getFileExtension(fileName: String): String = fileName.split('.').last

  // </editor-fold>

}
