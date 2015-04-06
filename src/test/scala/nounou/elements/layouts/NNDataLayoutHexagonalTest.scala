package nounou.elements.layouts

import org.scalatest.FunSuite

/**
 * Created by ktakagaki on 15/04/04.
 */
class NNDataLayoutHexagonalTest extends FunSuite {

  test("basic initialization"){

    val layout = Array(
      Array( -1, 15, 14, 13, 12, 11 ),
        Array( 10,  9,  8,  1,  0 ),
          Array(  7,  6,  5,  4 ),
            Array(  -1,  3,  2 )
    )
    val hexLayout = new NNDataLayoutHexagonal( layout )

    println(hexLayout.toJsonString)

  }
}
