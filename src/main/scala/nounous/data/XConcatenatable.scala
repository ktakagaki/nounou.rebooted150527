package nounous.data


/**
 * Created by Kenta on 12/15/13.
 */
trait XConcatenatable extends X{
  def :::(target: X): X
}
