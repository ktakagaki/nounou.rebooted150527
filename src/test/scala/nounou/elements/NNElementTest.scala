package nounou.elements

import org.scalatest.FunSuite

/**
 * Created by ktakagaki on 15/04/05.
 */
class NNElementTest extends FunSuite {

  test("deserialization"){
    val deserialized = nounou.gson.fromJson(
      """ { "className":"nounou.elements.tr.lay.NND",
        |  "gitHead":"4709fd6a",
        |  "random":"1234" }""".stripMargin,
      classOf[NNElementDeserializeIntermediate]).asInstanceOf[NNElementDeserializeIntermediate]

    assert(deserialized.className=="nounou.elements.tr.lay.NND")
    assert(deserialized.gitHead=="4709fd6a")
  }
}
