package nounou.data.io

import nounou.data._
import java.io.File
import breeze.io.RandomAccessFile
import scala.collection.immutable.TreeMap

/**Reader for MiCAM unified form data (gsd/gsh).
  * Currently, it actually only reads from the gsd, and ignores the accompanying gsh file.
 * @author ktakagaki
 * @date 2/25/14.
 */
object FileAdapterGSDGSH extends FileAdapter {

  override val canWriteExt: List[String] = List[String]()
  override def writeImpl(file: File, data: X, options: OptFileAdapter) = writeCannotImpl(file, data, options)
  override val canLoadExt: List[String] = List[String]( "gsd", "gsh" )

  override def loadImpl(file: File): List[X] = {

    val gshfile = "(*).(gsh)".r//("strippedName","extension")

    val fileName =file.getName.toLowerCase
    if(fileName.matches("*.gsd")) loadImplGSD(file)
    else if(fileName.matches("*.gsh")){
      if( new File(gshfile.findAllIn(fileName).next + ".gsd").isFile ){
        loadImplGSD(file)
      } else {
        throw loggerError("cannot load gsh file ({}) with this FileAdapter, no accompanying .gsd file exists!", file.getName )
      }
    } else {
      throw loggerError("cannot load file ({}) with this format from this FileAdapter!", file.getName )
    }

  }

  private def loadImplGSD(file: File): List[X] = {

    val raf = new RandomAccessFile(file)
    raf.seek(256)
    val nDataXsize = raf.readInt16()     //256... number of pixels on X axis
    val nDataYsize = raf.readInt16()     //258... number of pixels on Y axis
    val nLeftSkip = raf.readInt16()      //260
    val nTopSkip = raf.readInt16()       //262
    val nImgXsize = raf.readInt16()      //264
    val nImgYsize = raf.readInt16()      //266
    val nFrameSize = raf.readInt16()     //268... number of frames
    val nOrgImgXsize = raf.readInt16()   //270
    val nOrgImgYsize = raf.readInt16()   //272
    val nOrgFrmSize = raf.readInt16()    //274
    val nShift = raf.readInt16()         //276
    val nDummy = raf.readInt16()         //278
    val dAverage = raf.readFloat()       //280
    val dSampleTime = raf.readFloat()    //284... sampling rate (msec)
        val sampleRate = 1000d/dSampleTime
    val dOrgSampleTime = raf.readFloat() //288
    val dDummy = raf.readFloat()         //292
    //val chDum32 = raf.readFloat()        //296

    raf.seek(328)
    val AUXnChanum = raf.readInt16()        //328
    val AUXnRate = raf.readInt16()          //330... temporal resolution
          val sampleRateAux = 1000d/AUXnRate
    val AUXnOfset = raf.readInt16()         //332
    val AUXnChNext = raf.readInt16()        //334
    val AUXnTimeNext = raf.readInt16()      //336
    val AUXnFrameSize = raf.readInt16()     //338... number of frames
    val AUXnShift = raf.readInt16()         //340
    val AUXnDummy1 = raf.readInt16()        //342
    val AUXnDummy2 = raf.readInt16()        //344
    val AUXnDummy3 = raf.readInt16()        //346

    val header = new XHeaderGSD(
      TreeMap[String, HeaderValue](
        "nDataXsize" -> HeaderValue(nDataXsize),
        "nDataYsize" -> HeaderValue(nDataYsize),
        "nLeftSkip" -> HeaderValue(nLeftSkip),
        "nTopSkip" -> HeaderValue(nTopSkip),
        "nImgXsize" -> HeaderValue(nImgXsize),
        "nImgYsize" -> HeaderValue(nImgYsize),
        "nFrameSize" -> HeaderValue(nFrameSize),
        "nOrgImgXsize" -> HeaderValue(nOrgImgXsize),
        "nOrgImgYsize" -> HeaderValue(nOrgImgYsize),
        "nOrgFrmSize" -> HeaderValue(nOrgFrmSize),
        "nShift" -> HeaderValue(nShift),
        "nDummy" -> HeaderValue(nDummy),
        "dAverage" -> HeaderValue(dAverage),
        "dSampleTime" -> HeaderValue(dSampleTime),
        "dOrgSampleTime" -> HeaderValue(dOrgSampleTime),
        "dDummy" -> HeaderValue(dDummy),

        "AUXnChanum" -> HeaderValue(AUXnChanum),
        "AUXnRate" -> HeaderValue(AUXnRate),
        "AUXnOfset" -> HeaderValue(AUXnOfset),
        "AUXnChNext" -> HeaderValue(AUXnChNext),
        "AUXnTimeNext" -> HeaderValue(AUXnTimeNext),
        "AUXnFrameSize" -> HeaderValue(AUXnFrameSize),
        "AUXnShift" -> HeaderValue(AUXnShift),
        "AUXnDummy1" -> HeaderValue(AUXnDummy1),
        "AUXnDummy2" -> HeaderValue(AUXnDummy2),
        "AUXnDummy3" -> HeaderValue(AUXnDummy3)
      )
    )

    val xBits = 1024
    val bitOffset: Short = - 8192
    val absOffset = 8192d
    val absUnit = "bit"
    val absGain = 1d

    raf.seek(972)
    val tempFrameShorts = nDataXsize.toInt * nDataYsize.toInt
    val bkgData =  raf.readInt16( tempFrameShorts )
    val data = raf.readInt16( tempFrameShorts * nFrameSize.toInt )
    val analogData = raf.readInt16( AUXnChanum * AUXnFrameSize )

    val dataReturn = new Array[Array[Int]](tempFrameShorts)
    for( ch <- 0 until tempFrameShorts ) dataReturn(ch) = new Array[Int]( nFrameSize + 1 )

    var bkgCnt = 0
    for( y <- 0 until nDataYsize.toInt )
      for( x <- 0 until nDataXsize.toInt ){
        dataReturn(bkgCnt)(0) = (bkgData(bkgCnt) + bitOffset).toInt
        bkgCnt += 1
      }

    var dataCnt = 0
    for( fr <- 1 until nFrameSize.toInt + 1 ) {
      var chCnt = 0
      for( y <- 0 until nDataYsize.toInt )
        for( x <- 0 until nDataXsize.toInt ){
          dataReturn(chCnt)(fr) = dataReturn(chCnt)(0) + data(dataCnt).toInt
          chCnt += 1
          dataCnt += 1
        }
    }

    val chNamesReturn = new Array[String](tempFrameShorts)
    var chNamesCnt = 0
    for( y <- 0 until nDataYsize.toInt )
      for( x <- 0 until nDataXsize.toInt ){
        chNamesReturn(chNamesCnt) = "(x, y) = ("+ x+", "+ y+"), pixel number = "+ chNamesCnt
        chNamesCnt += 1
      }

    val dataAuxReturn = new Array[Array[Int]](AUXnChanum)
    for( ch <- 0 until AUXnChanum.toInt ) dataAuxReturn(ch) = new Array[Int]( AUXnFrameSize )

    var dataAuxCnt = 0
    for( fr <- 0 until AUXnFrameSize.toInt ) {
      var chCnt = 0
      for( ch <- 0 until AUXnChanum.toInt ) {
          dataAuxReturn(chCnt)(fr) = analogData( dataAuxCnt ).toInt
          chCnt += 1
          dataAuxCnt += 1
        }
    }

    List(
      header,
      new XDataGSD( dataReturn.toVector.map( _.toVector ), xBits, absGain, absOffset, absUnit, chNamesReturn.toVector, 0L, sampleRate ),
      new XDataGSDAux( dataAuxReturn.toVector.map( _.toVector ), xBits, absGain, absOffset, absUnit, chNamesReturn.toVector, 0L, sampleRateAux )
    )
  }

}

class XHeaderGSD(override val header: TreeMap[String, HeaderValue]) extends XHeader(header)
class XDataGSD(
                data: Vector[Vector[Int]],
                xBits: Int,
                absGain: Double,
                absOffset: Double,
                absUnit: String,
                channelNames: Vector[String],
                segmentStartTS: Long,
                sampleRate: Double
                ) extends XDataPreloadedSingleSegment( data, xBits, absGain, absOffset, absUnit, channelNames, segmentStartTS, sampleRate)

class XDataGSDAux(
                data: Vector[Vector[Int]],
                xBits: Int,
                absGain: Double,
                absOffset: Double,
                absUnit: String,
                channelNames: Vector[String], // = Vector.tabulate[String](data.length)(i => "no channel name")
                segmentStartTS: Long,
                sampleRate: Double
                ) extends XDataPreloadedSingleSegment( data, xBits, absGain, absOffset, absUnit, channelNames, segmentStartTS, sampleRate) with XDataAux
