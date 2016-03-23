package readers

/**
  * A node mapping.
  *
  * Position on disk.
 * @param key
  * The word key.
  * @param value
  * The word occurrence value.
  * @param reader
  * The reader that extracted this node from disk. And has access to the origin RAF
  * file of this node.
  */
case class BTEntry(var key: String, var value: Int, var next : Option[BTNode] = None, reader : BTReader)

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

