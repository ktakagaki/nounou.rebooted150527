/**
 *
 */
package nounou.elements

import java.io.IOException

/**
 * @author takagaki
 *
 */
class InvalidFileFormatException(arg0: String, arg1: Throwable) extends IOException(arg0, arg1) {
  def InvalidFileFormatException() = new IOException()
  def InvalidFileFormatException(message: String) = new IOException(message)
  def InvalidFileFormatException(cause: Throwable) = new IOException(cause)  
}