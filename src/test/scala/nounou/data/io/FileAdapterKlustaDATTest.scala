package nounou.data.io

import org.scalatest.FunSuite
import nounou.DataReader
import nounou.data.filters.{XDataFilterBuffer, XDataFilterMask}
import nounou.data.io._

/**
 * @author ktakagaki
 * @date 2/19/14.
 */
class FileAdapterKlustaDATTest extends FunSuite {

  val bigTestFilesHead = "V:/docs/k.VSDdata/project.SPP/Nlx/SPP010/2013-12-02_17-07-31/"
  val bigTestFiles = Array[String](
    bigTestFilesHead + "Tet4a.ncs",
    bigTestFilesHead + "Tet4b.ncs",
    bigTestFilesHead + "Tet4c.ncs",
    bigTestFilesHead + "Tet4d.ncs"
  )

  test("Speed Optimization") {
    val reader = new DataReader
    reader.load(bigTestFiles)
    println(reader.dataSummary())

    val writeData =  new XDataFilterMask( reader.dataORI, reader.mask)
    //val writeData =  new XDataFilterMask( new XDataFilterBuffer( reader.dataORI ), reader.mask)

    FileAdapterKlustaDAT.write( bigTestFilesHead+"test", writeData)

  }



}
