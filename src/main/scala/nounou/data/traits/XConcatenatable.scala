package nounou.data.traits

import nounou.data.X

import scala.reflect.ClassTag

/**Trait to mark weather two [[nounou.data.X]] objects can be concatenated
  * (eg multiple channels of data can be concatenated to one channel array).
 * Created by Kenta on 12/15/13.
 */
trait XConcatenatable extends X{

  /** Concatenate two [[nounou.data.X]] objects
    */
  def :::(target: X): X
}
