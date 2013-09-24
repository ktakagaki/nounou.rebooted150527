package data

import nounous.data.Source

/**
  * Created with IntelliJ IDEA.
  * User: takagaki
  * Date: 23.09.13
  * Time: 13:14
  * To change this template use File | Settings | File Templates.
  */
class SourceSpikes extends SourceDiscrete {


   def isCompatible(that: Source): Boolean = {
     that match {
       case t: SourceSpikes => {
         //ToDo
         super.isCompatible(that)
       }
       case _ => false
     }
   }

 }
