package readers

import breeze.io.RandomAccessFile

import scala.collection.mutable.ListBuffer

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
case class BTEntry(posit: Long, var nodes: ListBuffer[Long], reader: BTReader, var children: ListBuffer[Long]) {
  val SIZE = reader.ENTRY_SIZE

  def isFull = nodes.length >= 5

  /**
    * Adds a node to this entry.
    *
    * @param nodePosit
    * The position in RAF of the node being removed.
    * @param file
    * The file holding the data.
    *
    */
  def addNode(nodePosit: Long)(implicit file: RandomAccessFile = reader.raf): Unit = {
    nodes +== nodePosit
    sortNodes()
    update()
  }

  /**
    * Adds a sequence of nodes to this entry.
    *
    * @param nodePosits
    * The positions in RAF of the nodes being removed.
    * @param file
    * The file holding the data.
    *
    */
  def addNodes(nodePosits: Seq[Long])(implicit file: RandomAccessFile = reader.raf): Unit = {
    nodes ++== nodePosits
    sortNodes()
    update()
  }

  /**
    * Removes node from this entry.
    *
    * @param nodePosit
    * The position in RAF of the node being removed.
    * @param file
    * The file holding the data.
    *
    */
  def removeNode(nodePosit: Long)(implicit file: RandomAccessFile = reader.raf): Unit = {
    val index = nodes.indexOf(nodePosit)

    if (index == -1) throw new NoSuchElementException(s"No such child: $nodePosit")

    nodes -== nodePosit
    sortNodes()
    update()

  }

  /**
    * Removes a sequence of nodes from this entry.
    *
    * @param nodePosits
    * The positions in RAF of the nodes being removed.
    * @param file
    * The file holding the data.
    */
  def removeNodes(nodePosits: Seq[Long])(implicit file: RandomAccessFile = reader.raf): Unit = {
    val indices = nodePosits.map(nodes.indexOf)

    if (indices.contains(-1)) throw new NoSuchElementException(s"Invalid child posit in $nodePosits")

    nodes --== nodePosits
    sortNodes()
    update()
  }

  def addChild(entry: BTEntry): Unit = {
    children += entry.posit
    sortChildren()
    update()
  }

  /**
    * Splits this entry.
    *
    * @param node
    * The node being entered into this entry.
    * @return
    * A triple tuple containing this entry, the node being promoted, and the new entry
    * created to hold the right most nodes.
    */
  def doSplit(node: BTNode): (BTEntry, BTNode, BTEntry) = {
    nodes +== node.posit
    sortNodes()
    val bTNodes = nodes.map(reader.extractNode)

    println(s"All the nodes in the entry are $bTNodes")

    val right = bTNodes.drop(Math.floorDiv(SIZE, 2) + 1) // right most nodes
    val left = bTNodes.dropRight(Math.floorDiv(SIZE, 2) + 1) // left most nodes
    val middle = bTNodes(SIZE / 2) // middle node

    println(s"The right nodes are $right")
    println(s"The left nodes are $left")
    println(s"The center node is $middle")

    val remRightMiddle = (right :+ middle).map(_.posit)
    this removeNodes remRightMiddle // Remove the right and middle element from the currentEntry

    val rightEntry = reader.extractEntry(reader.insertEmptyEntry)

    rightEntry addNodes right.map(_.posit)
    sortNodes()
    update()
    (this, middle, rightEntry)
  }

  /**
    * Maps the node pointers containted in this entry to node objects.
    *
    * @return
    * A sequence of node objects representing the nodes contained in this entry.
    */
  def filteredNodes = nodes
    .filterNot(_ == -1)
    .map(reader.extractNode)
    .sortWith(sort)

  /**
    * The sorting method for strings.
    *
    * @return
    */
  def sort: (BTNode, BTNode) => Boolean = {
    (a, b) => a.word.compareTo(b.word) < 0
  }

  /**
    * The rank of this entry.
    *
    * @return
    */
  def rank = filteredNodes.last.word


  override def toString = s"Entry(at = $posit, Nodes [${nodes.mkString(", ")}], Children [${children.mkString(", ")}])"

  /**
    * Prints in detail description of the nodes contained in this entry. File IO is performed frequently May be
    * slow.
    *
    * @return
    * A detailed string representation of this entry.
    */
  def toStringR: String = s"Entry(at = $posit, Nodes [${nodes.map(reader.extractNode).mkString(", ")}])"

  /**
    * Sorts the children in this node according to their ranking node.
    */
  private def sortChildren(): Unit = children = children.sortWith { (a, b) =>
    val aRank = reader.extractEntry(a).rank
    val bRank = reader.extractEntry(b).rank
    aRank.compareTo(bRank) < 0
  }

  /**
    * Sorts the nodes contained in this entry lexicographically.
    */
  def sortNodes(): Unit = nodes = nodes.sortWith { (a, b) =>
    val aRank = reader.extractNode(a).word
    val bRank = reader.extractNode(b).word
    aRank.compareTo(bRank) < 0
  }

  def update(): Unit = reader.newEntryAt(posit, nodes, children)

}

object NodeOrd extends Ordering[BTNode] {
  override def compare(x: BTNode, y: BTNode): Int = x.word.compareTo(y.word)
}
