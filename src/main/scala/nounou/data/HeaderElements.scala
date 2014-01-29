package nounou.data

/**
 * Created by Kenta on 12/5/13.
 */
abstract class HeaderElements

object HeaderElements {
  def apply(name: String, value: Any): HeaderElements = {
    value match {
      case v: Double => new HeaderElementDouble(name, v)
      case v: Int => new HeaderElementInt32(name, v)
      case v: String => new HeaderElementString(name, v)
      case _ => throw new IllegalArgumentException("this value type not supported as header element!")
    }
  }
}
case class HeaderElementDouble(name: String, value: Double) extends HeaderElements
case class HeaderElementInt32(name: String, value: Int) extends HeaderElements
case class HeaderElementString(name: String, value: String) extends HeaderElements

