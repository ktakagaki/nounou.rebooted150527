package nounou.elements

import nounou.util.LoggingExt

/** Base class for all data elements.
  */
abstract class NNElement extends LoggingExt {
  //ToDo3: consider add: pt info, rec info, rec start time/date, etc

  def isCompatible(that: NNElement): Boolean
  final def isCompatible(that: Seq[NNElement]): Boolean = that.forall( this.isCompatible(_) )

}