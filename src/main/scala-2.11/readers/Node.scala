package readers

import breeze.io.RandomAccessFile

/**
  * A node mapping.
  *
  * @param posit
  * Position on disk.
  * @param entryPosit
  * readers.Entry's position on disk.
  * @param word
  * The word key.
  * @param occurrence
  * The word occurrence value.
  * @param reader
  * The reader that extracted this node from disk. And has access to the origin RAF
  * file of this node.
  */
case class Node(posit: Long, var entryPosit: Long, word: String, occurrence: Int, reader: BTreeRafReader) {
  private var _left: Option[Long] = None
  private var _right: Option[Long] = None

  def left(implicit file: RandomAccessFile): Option[Long] = _left

  def left_=(newLeft: Option[Long])(implicit file: RandomAccessFile): Unit = {
    _left = newLeft
    reader.newNodeAt(entryPosit, word, _left, _right, posit)
  }

  def right(implicit file: RandomAccessFile): Option[Long] = _right

  def right_=(newRight: Option[Long])(implicit file: RandomAccessFile): Unit = {
    _right = newRight
    reader.newNodeAt(entryPosit, word, _left, _right, posit)
  }

  def init(leftInit: Option[Long], rightInit: Option[Long]) = {
    _left = leftInit
    _right = rightInit

  }

  def >>(ePosit: Long): Unit = {
    entryPosit = ePosit
    reader.newNodeAt(entryPosit, word, _left, _right, posit)
  }

  override def toString: String = s"Node($posit, $entryPosit, $word, $occurrence, ${_left}, ${_right})"

}
