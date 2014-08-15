/**Kenta on 12/16/13. */
val pat = """-AcqEntName[ ]*(\S+)""".r            //[ \t\n]?

val pat(ent) = "-AcqEntName  Hello"
ent
pat.findFirstIn("dsfe  \n -AcqEntName Hello\n   dsfe") match {
  case Some(pat(entName)) => entName
  case _ => "NoName"
}


val pat2 =
  """TTL Input on AcqSystem(\d+)_(\d+) board (\d+) port (\d+) value.*""".r
val pat2(as1, as2, b, p) = "TTL Input on AcqSystem1_0 board 0 port 1 value (0x0001)."
//"TTL Input on AcqSystem1_2 board 200 port 4 value"
as1
as2
b
p


pat2.findFirstIn("TTL Input on AcqSystem1_2 board 3 port 4 value")
