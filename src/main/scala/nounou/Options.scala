package nounou

///**
// * @author ktakagaki
// * // //@date 3/17/14.
// */
///**Base class for all options*/
//abstract class Opt

///Individual Options

/**Option values: window function for filter design.*/
abstract class OptSpikeDetectorFlush extends Opt
object OptSpikeDetectorFlush {

  case object All extends OptSpikeDetectorFlush {
    override def toString = "Flush all spikes of appropriate trode from XSpikes object before detecting."
  }
//  implicit def optSpikeDetectorFlush_None_specialize( nounou.None ): OptSpikeDetectorFlush = OptSpikeDetectorFlush.None
  case object None extends OptSpikeDetectorFlush {
    override def toString = "Do not flush any spikes prior to detecting, just add on top."
  }
//  case object Range extends OptSpikeDetectorFlush {
//    override def toString = "Just flush spikes within detection range, prior to adding new."
//  }

}
