import org.scalatest.flatspec.AnyFlatSpec

class elasticsearch extends AnyFlatSpec {
  "elasticsearch" should "correctly download and transform the data" in {
    RetweetGraph.main(Array.empty)
  }
}
