package nounou.data

/**
 * Created by Kenta on 12/5/13.
 */
abstract class HeaderValue(val value: Any){
  override def toString = value.toString
}

object HeaderValue {
  def apply(value: Any): HeaderValue = {
    value match {
      case v: Double => new HeaderValueDouble(v)
      case v: Int => new HeaderValueInt(v)
      case v: String => new HeaderValueString(v)
      case _ => throw new IllegalArgumentException("this value type not supported as header element!")
    }
  }
}
case class HeaderValueDouble(override val value: Double) extends HeaderValue(value)
case class HeaderValueInt(override val value: Int) extends HeaderValue(value)
case class HeaderValueString(override val value: String) extends HeaderValue(value)

