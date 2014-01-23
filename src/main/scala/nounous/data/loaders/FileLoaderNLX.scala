package nounous.data.loaders

import nounous.reader.FileLoader

/**
 * Created by Kenta on 12/16/13.
 */
trait FileLoaderNLX extends FileLoader {
  val headerBytes = 16384
//  val neuralynxTextHeader: String
}
