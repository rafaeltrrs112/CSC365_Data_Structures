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

