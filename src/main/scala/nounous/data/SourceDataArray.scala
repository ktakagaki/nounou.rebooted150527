package data

import nounous.data.SourceData

/**SourceData class with internal representation as data array
 * Created with IntelliJ IDEA.
 * User: takagaki
 * Date: 13.09.13
 * Time: 18:50
 * To change this template use File | Settings | File Templates.
 */
abstract class SourceDataArray extends SourceData{

  val data: Array[Array[Int]]

  override def readData(ch: Int, fr: Int) = data(ch)(fr)


}
