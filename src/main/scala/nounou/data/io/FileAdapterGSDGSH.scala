package nounou.data.io

import nounou._
import nounou.data._
import java.io.File
import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}
import scala.collection.immutable.{TreeMap}
import breeze.linalg.{DenseMatrix => DM, DenseVector => DV}

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

    val gshfile = """([^ \t\r\n\v\f]*).(gsh)""".r//("strippedName","extension")

    val fileName =file.getName.toLowerCase
    if(fileName.endsWith(".gsd")) loadImplGSD(file)
    else if(fileName.endsWith(".gsh")){
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

    val raf = new RandomAccessFile(file)(ByteConverterLittleEndian)
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
          val sampleRateAux = sampleRate * 20d //1000d/AUXnRate *10d
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

    //================
    //Read actual data
    //================
    raf.seek(972)
    val tempFrameShorts = nDataXsize.toInt * nDataYsize.toInt
    //Read background frames and scale
    val backgroundOri = raf.readInt16( tempFrameShorts )
    //Read data frames
    val dataOri = raf.readInt16( tempFrameShorts * nFrameSize.toInt )
    //Read analog data
    val analogOri = raf.readInt16( AUXnChanum * AUXnFrameSize * AUXnRate  )


    //Handle Background Frame
    var tempFrCnt = 0
    var tempChCnt = 0
    var tempDataCnt = 0

    val backgroundReturn = DV.zeros[Int](tempFrameShorts)
    forJava(0, tempFrameShorts, 1, (p: Int) => (backgroundReturn(p) = xBits * backgroundOri(p)) )

    //Handle Data Frames
    tempFrCnt = 0
    tempDataCnt = 0

    val dataReturn = DM.zeros[Int](tempFrameShorts, nFrameSize)
    while(tempFrCnt < nFrameSize){
      tempChCnt = 0
      while( tempChCnt < tempFrameShorts){
        dataReturn(tempChCnt, tempFrCnt) = backgroundReturn(tempChCnt) + xBits * dataOri(tempDataCnt)
        tempDataCnt += 1;
        tempChCnt += 1;
      }
      tempFrCnt += 1
    }

    val chNamesAuxReturn = (for( ch <- 0 until AUXnChanum ) yield "A-In " + (ch +1).toString )

    //Handle Auxiliary Channels
    tempFrCnt = 0
    tempDataCnt = 0

    val dataAuxReturn = DM.zeros[Int](AUXnChanum, nFrameSize * AUXnRate)
    while(tempFrCnt < nFrameSize * AUXnRate){
      tempChCnt = 0
      while( tempChCnt < AUXnChanum){
        dataAuxReturn(tempChCnt, tempFrCnt) = xBits * analogOri(tempDataCnt)    //ToDo 1: scale??
        tempDataCnt += 1;
        tempChCnt += 1;
      }
      tempFrCnt += 1
    }

    //Create layout
    val layout = new XLayoutSquare(nDataXsize, nDataYsize)


    //Return Results
    List(
      header,
      new XDataGSD( dataReturn, xBits, absGain, absOffset, absUnit, layout.channelNames, 0L, sampleRate, layout, backgroundReturn ),
      new XDataGSDAux( dataAuxReturn, xBits, absGain, absOffset, absUnit, chNamesAuxReturn.toVector, 0L, sampleRateAux, XLayoutNull ),
      layout
    )
  }

}

class XHeaderGSD(override val header: TreeMap[String, HeaderValue]) extends XHeader(header)
class XDataGSD(
                data: DM[Int],
                xBits: Int,
                absGain: Double,
                absOffset: Double,
                absUnit: String,
                channelNames: Vector[String],
                segmentStartTS: Long,
                sampleRate: Double,
                layout: XLayout,
                val backgroundFrame: DV[Int]
                ) extends XDataPreloadedSingleSegment( data, xBits, absGain, absOffset, absUnit, channelNames, segmentStartTS, sampleRate, layout)

class XDataGSDAux(
                data: DM[Int],
                xBits: Int,
                absGain: Double,
                absOffset: Double,
                absUnit: String,
                channelNames: Vector[String], // = Vector.tabulate[String](data.length)(i => "no channel name")
                segmentStartTS: Long,
                sampleRate: Double,
                layout: XLayout
                   ) extends XDataPreloadedSingleSegment( data, xBits, absGain, absOffset, absUnit, channelNames, segmentStartTS, sampleRate, layout) with XDataAux
