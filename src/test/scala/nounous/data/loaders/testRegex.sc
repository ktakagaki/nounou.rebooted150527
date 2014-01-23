/**
 * Created by Kenta on 12/16/13.
 */
val pat = """-AcqEntName[ ]*(\S+)""".r            //[ \t\n]?



val pat(ent) = "-AcqEntName Hello"
ent

pat.findFirstIn("dsfe  \n -AcqEntName Hello\n   dsfe") match {
  case Some(pat(entName)) => entName
  case _ => "NoName"
}


