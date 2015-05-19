import scala.collection.immutable.{TreeMap, HashMap}
import scala.collection.mutable

/**
 * @author ktakagaki
 * //@date 1/31/14.
 */
val tempMap = TreeMap[Int, String]( 1->"one", 2->"two", 1->"not one")


tempMap(2)
tempMap(1)


val tempMap2 = mutable.HashMap[Int, String]()
tempMap2 += (1 -> "one")
tempMap2 += (5 -> "five")
tempMap2 += (2 -> "two")
tempMap2 += (5 -> "fiveB")

TreeMap(tempMap2.toArray:_*)



