package nounous.data

/**
 * Created by Kenta on 12/6/13.
 */
class XHeader(val formatName: String, val header: Vector[HeaderElements]) extends X {


  def isCompatible(that: X): Boolean = {
    that match {
      case t: XHeader => this.formatName == t.formatName && this.header == t.header
      case _ => false
    }
  }

  def :::(target: X): X = {
    throw new IllegalArgumentException("Cannot concatenate XHeaders!")
  }
}
