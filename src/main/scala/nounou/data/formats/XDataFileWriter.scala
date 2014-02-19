package nounou.data.formats

import java.io.File
import nounou.data.{XData, X}

/**
 * @author ktakagaki
 * @date 2/2/14.
 */
trait XDataFileWriter {

  def write(fileName: String, data: XData)


}
