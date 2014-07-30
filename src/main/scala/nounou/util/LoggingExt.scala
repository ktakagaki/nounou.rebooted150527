package nounou.util

import com.typesafe.scalalogging.slf4j.LazyLogging

/**
 * @author ktakagaki
 * @date 07/15/2014.
 */
trait LoggingExt extends LazyLogging {

  /** Logs a message using slf4j, and returns a new [[IllegalArgumentException]].
    * Use as follows:
    * ````
    * throw loggerError("This is an error with params {}", param1.toString)
    * ````
   *
   */
  def loggerError(message: String, params: AnyRef*): IllegalArgumentException = {
    logger.error(message, params )
    new IllegalArgumentException(message)
  }


  /** If input 'boolean' is false, logs a message using slf4j and throws an error.
   *
   */
  @throws[IllegalArgumentException]
  def loggerRequire(boolean: Boolean, message: String, params: AnyRef*): Unit = {
    if(!boolean){
      logger.error(message, params )
      throw new IllegalArgumentException( "require:" +message)
    }
  }

}
