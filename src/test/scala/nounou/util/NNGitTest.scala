package nounou.util

import org.scalatest.FunSuite

/**
 * Created by ktakagaki on 15/04/05.
 */
class NNGitTest extends FunSuite {

  test("get json file"){
    val current = (new java.io.File( "." )).getCanonicalPath
    println( "Current directory is:" + current )
    println( "jsonFile = " + (NNGit.jsonFile.getAbsolutePath) )
    assert(NNGit.jsonFile.exists(), "jsonFile does not exist!")
  }

  test("get current repo"){
    //println( "jsonResource.exists = " + NNGit.jsonResource.exists )
    println( NNGit.infoPrintout() )
  }

}
