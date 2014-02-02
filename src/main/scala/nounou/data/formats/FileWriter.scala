package nounou.data.formats

import java.io.File
import nounou.data.X

/**
 * @author ktakagaki
 * @date 2/2/14.
 */
trait FileWriter {

  def write(fileName: String, data: List[X])


}
