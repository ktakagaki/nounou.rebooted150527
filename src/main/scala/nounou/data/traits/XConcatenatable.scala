package nounou.data.traits

import nounou.data.X

/**
 * Created by Kenta on 12/15/13.
 */
trait XConcatenatable extends X{
  def :::(target: X): X
}
