package nounou

/**
 * Created with IntelliJ IDEA.
 * User: Kenta
 * Date: 11/27/13
 * Time: 7:30 PM
 * To change this template use File | Settings | File Templates.
 */
package object util{
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
