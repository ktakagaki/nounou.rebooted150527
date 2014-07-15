package nounou

/**
 * @author ktakagaki
 * @date 07/15/2014.
 */
package object util {

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

}
