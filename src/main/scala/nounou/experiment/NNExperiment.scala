//package nounou.experiment
//
//import nounou.elements.{NNTrodes, NNEvents, NNSpikes}
//import nounou.elements.data.{NNDataAux, NNData}
//import nounou.elements.traits.layouts.NNLayout
//
///**This trait encapsulates the output of an electrophysiological experiment, which consists
//  * of various [[nounou.elements.NNElement]] objects.
//  *
//  * Filters implementing this trait can send different [[nounou.elements.NNElement]] objects
//  * downstream, for example, combining an [[nounou.elements.data.NNData]] and an
//  * [[nounou.elements.NNTrode]] object to create a new [[nounou.elements.NNSpikes]]
//  * object to send downstream.
//  *
// * Created by ktakagaki on 15/03/02.
// */
//trait NNExperiment {
//
//  protected var nnData: NNData = null
//  def getNNData(): NNData = nnData
//
//  protected var nnDataAux: NNDataAux = null
//  def getNNDataAux(): NNDataAux = nnDataAux
//
//
//  protected var nnLayout: NNLayout = null
//  def getNNLayout(): NNLayout = nnLayout
//
//  protected var nnTrodes: NNTrodes = null
//  def getNNTrodes(): NNTrodes = nnTrodes
//
//
//
//  protected var nnSpikes: NNSpikes = null
//  def getNNSpikes(): NNSpikes = nnSpikes
//
//  protected var nnEvents: NNEvents = null
//  def getNNEvents(): NNEvents = nnEvents
//
//
//}
