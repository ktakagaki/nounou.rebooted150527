package nounous.data

import java.io.File
import nounous.io.RandomAccessFileLE
import scala.collection.mutable.ArrayBuffer

/**Reads in single NEX files. Partial implementation, with only waveforms processed. 
 *
 * @author K. Takagaki
 */
class ReaderNEX() extends Reader {
  
  def read(file: File): Source = readImpl(file)
  
//  //ToDo remove once dialog box programmed in Reader
//  def read() = List(read(null))
 
  
  private def readImpl(file: File): Source = {

    val fHand = new RandomAccessFileLE(file, "r")
    
    var tempret: Source = null
    var data: Array[Int] = null
  

    val magic = fHand.readInt()
    
    if ( magic != 827868494 ) throw new InvalidFileFormatException("Invalid NEX file!", null)
    
    val nexFileVersion: Int = fHand.readInt
    val nexFileComment: String = new String(fHand.readByte(256))
    val nexFileFreq: Double = fHand.readDouble 
    val nexFileTBeg: Double = fHand.readInt / nexFileFreq 
    val nexFileTEnd: Double = fHand.readInt / nexFileFreq 
    val nexFileNVar: Int = fHand.readInt
    
    fHand.skipBytes(260)
    
    if(nexFileNVar != 1){
	  throw new InvalidFileFormatException(file + " has a waveform with more than one wave, this is unsupported", null)//ToDo
    }

//	for(i <- 1 to nexFileNVar) {
      val recType = 	fHand.readInt
      val varVersion = 	fHand.readInt
      val name = new String(fHand.readByte(64))
      val offset = 		fHand.readInt
      val n = 			fHand.readInt
      val wireNumber = 	fHand.readInt
      val unitNumber = 	fHand.readInt
      val gain = 		fHand.readInt
      val filter =		fHand.readInt
      val xPos = 		fHand.readDouble
      val yPos = 		fHand.readDouble
      val wFrequency =  fHand.readDouble
      val ADtoMV =		fHand.readDouble
      val NPointsWave = fHand.readInt
      val NMarkers =	fHand.readInt
      val MarkerLength = fHand.readInt
      val MVOffset =	fHand.readDouble
      val filePosition =  fHand.getFilePointer
      
      recType match {
        //case 0 //neurons 
        //case 1 //events
        //case 2 //intervals 
        case 5 => {
        	
            val extraBits = 1024
            
        	fHand.seek(offset)
            fHand.skipBytes(n*4) //timestamps, discard
            fHand.skipBytes(n*4) //fragmetStarts, discard
        	data = Array.fill(NPointsWave){fHand.readShort.toInt * extraBits}
            
            class SourceDataNEX extends SourceData {
              val channelCount = 1 //ToDo !!!
              override var _extraBits = extraBits;
            	override var _extraBitsD = _extraBits.toDouble
            	override val absoluteGain = ADtoMV / _extraBitsD
                override val absoluteUnit = "mV"
                override var  _absoluteOffset = MVOffset
        	    override var  _samplingRate = wFrequency
        	    override var  _channelNames = Array(name)   
        	    override var _startTime =  nexFileTBeg

        	    data = this.data
            }
            
            val tempretSD = new SourceDataNEX()
        	tempret =  tempretSD
         }/*case 5*/
       } /*recType match*/
//    } /*for(i <- 1 to nexFileNVar)*/
        
  tempret   
  }//readImpl
  

}
      
//        } catch {
//    case ex: FileNotFoundException => {
//      println("File name " + fileName + " was not found! Loading failed")
//      ex.printStackTrace()
//      //DataSourceEmpty
//    }
//    case ex: Throwable => {
//      println("Non-handled exception was thrown on load: " + ex.toString())
//      //DataSourceEmpty
//    }
//  }