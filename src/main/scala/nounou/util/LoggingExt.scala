package nounou.util

import com.typesafe.scalalogging.slf4j.LazyLogging

/**
 * @author ktakagaki
 * @date 07/15/2014.
 */
trait LoggingExt extends LazyLogging {

  def loggerError(message: String, params: AnyRef*): IllegalArgumentException = {
    logger.error(message, params )
    new IllegalArgumentException(message)
  }

  @throws[IllegalArgumentException]
  def loggerRequire(boolean: Boolean, message: String, params: AnyRef*): Unit = {
    if(!boolean){
      logger.error(message, params )
      throw new IllegalArgumentException( "require:" +message)
    }
  }

}
