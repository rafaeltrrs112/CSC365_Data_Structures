package readers


class BTNode(var childCount: Int, implicit val reader: BTReader){
  var children: Array[BTEntry] = new Array[BTEntry](reader.M)

  /**
    * Splits this node in two and returns the spawned node (contains R greatest values).
    *
    * @return
    *          The R node spawned from a split.
    */
  def split : BTNode = {
    val halfM = reader.M / 2
    val rightNode = BTNode(halfM)
    childCount = halfM

    for(i <- 0 until halfM){
      rightNode.children(i) = children(halfM + i)
    }
    rightNode
  }

}

object BTNode {
  def apply(childCount : Int)(implicit reader : BTReader) : BTNode = new BTNode(childCount, reader)
}