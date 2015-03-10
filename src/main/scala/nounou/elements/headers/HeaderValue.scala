package nounou.elements.headers

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
      case v: Short => new HeaderValueShort(v)
      case v: Float => new HeaderValueFloat(v)
      case v: String => new HeaderValueString(v)
      case _ => throw new IllegalArgumentException("this value type not supported as header element!")
    }
  }
}
case class HeaderValueDouble(override val value: Double) extends HeaderValue(value)
case class HeaderValueInt(override val value: Int) extends HeaderValue(value)
case class HeaderValueString(override val value: String) extends HeaderValue(value)
case class HeaderValueShort(override val value: Short) extends HeaderValue(value)
case class HeaderValueFloat(override val value: Float) extends HeaderValue(value)

