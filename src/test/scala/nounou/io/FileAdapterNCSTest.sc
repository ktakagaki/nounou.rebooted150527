import nounou.NN
import nounou.elements.data.NNDataChannel
import nounou.io.neuralynx.{FileAdapterNCS, NNDataChannelNCS}

val testFileE04LC_CSC1 = getClass.getResource("/neurophysiology-test-files/nounou/Neuralynx/E04LC/CSC1.ncs").getPath()
//new File( "C:\\prog\\_gh\\_kt\\nounou.testfiles\\Neuralynx\\E04LC\\CSC1.ncs" )
val data = FileAdapterNCS.load( testFileE04LC_CSC1 ).apply(0)
assert( data.isInstanceOf[NNDataChannelNCS] )
val dataObj = data.asInstanceOf[NNDataChannelNCS]

dataObj.
