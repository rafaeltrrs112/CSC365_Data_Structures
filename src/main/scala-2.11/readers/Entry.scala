package readers

import breeze.io.RandomAccessFile

/**
  * A BTree entry mapping.
  *
  * @param posit
  * The entry's position on disk.
  * @param nodes
  * The nodes contained in this entry
  * @param reader
  * The reader that extracted this entry from disk. And has access to the origin RAF
  * file of this entry.
  */
case class Entry(posit: Long, nodes: Array[Long], reader: BTreeRafReader) {
  var population = 0

  def isFull = nodes.length >= 5

  def +=(childPosit: Long)(implicit file: RandomAccessFile = reader.raf): Unit = {
    nodes(population) = childPosit
    population += 1
    reader.newEntryAt(posit, nodes)

    reader.extractNode(childPosit) >> posit
  }

  def -=(childPosit: Long)(implicit file: RandomAccessFile = reader.raf): Unit = {
    val index = nodes.indexOf(childPosit)

    if (index == -1) throw new NoSuchElementException(s"No such child: $childPosit")

    nodes(index) = -1
    population -= 1
    reader.newEntryAt(posit, nodes)

    reader.extractNode(childPosit) >> -1
  }

  override def toString = s"Entry($posit, ${nodes.filterNot(_ == -1).mkString(", ")})"
}
