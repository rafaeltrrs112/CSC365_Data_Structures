package readers

/**
  * A node mapping.
  *
  * @param posit
  * Position on disk.

  * @param word
  * The word key.
  * @param occurrence
  * The word occurrence value.
  * @param reader
  * The reader that extracted this node from disk. And has access to the origin RAF
  * file of this node.
  */
case class BTNode(posit: Long, word: String, occurrence: Int, reader: BTReader) {

  override def toString: String = s"Node($posit, $word, $occurrence)"

}
