/**
 * Created with IntelliJ IDEA.
 * User: Kenta
 * Date: 12/3/13
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */

class A {
  val hello = "Hello"
}
class B extends A {
  override val hello = "HelloB"
}

val instance = new B
instance match {
  case x: A => println(x.hello)
  case _ => println("fell through")
}                                            // > HelloB
