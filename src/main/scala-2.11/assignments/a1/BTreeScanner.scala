package assignments.a1

import readers.BTReader

import scala.annotation.tailrec


case class EntryFrag(key: String, value: Int, next: Option[Long], reader: BTReader)

case class NodeFrag(childCount: Int, children: Array[Long], reader: BTReader) {
  def childrenToEntries: IndexedSeq[EntryFrag] = {
    (0 until childCount).map { (ind) =>
      reader.getEntryFrag(children(ind))
    }
  }

  override def toString = {
    s"NodeFrag($childCount , $childrenToEntries)"
  }
}

/**
  * Scanner class used to read a btree from disk.
  */
class BTreeScanner(reader: BTReader) {

  val root = reader.sapRoot


  def get(key: String): Option[Int] = search(root, key, 2)


  @tailrec
  private def search(x: NodeFrag, key: String, height: Int): Option[Int] = {
    val children = x.children

    val externalNode = height == 0

    externalNode match {
      case true => {
        mapFromFrags(x).get(key)
      }
      case false =>
        val travInd = findTraverseInd(x, key, height)
        travInd match {
          case Some(ind) =>
            val pointerEntryPosit = children(ind)
            val nextNodePosit = reader.getEntryFrag(pointerEntryPosit).next.get
            val nextNode = reader.getNodeFrag(nextNodePosit)
            search(nextNode, key, height - 1)
          case None => None
        }
    }
  }

  def mapFromFrags(x: NodeFrag): Map[String, Int] = {
    val children = x.childrenToEntries
    children.map((ent) => (ent.key, ent.value)).toMap
  }

  private def findTraverseInd(x: NodeFrag, key: String, height: Int): Option[Int] = {
    val childFrags = x.childrenToEntries
    val r = if (childFrags.isEmpty) None
    else (0 until x.childCount).find { (ind) =>
      ind + 1 == x.childCount || (key.compareTo(childFrags(ind + 1).key) < 0)
    }

    r match {
      case Some(s) => Some(s)
      case None => if(childFrags.isEmpty) None else Some(x.childCount)
    }
  }

  def toString(h: NodeFrag, ht: Int, indent: String): String = {
    val s: StringBuilder = new StringBuilder
    val children = h.childrenToEntries
    if (ht == 0) {
      var j: Int = 0
      while (j < h.childCount) {
        {
          s.append(indent + children(j).key + " " + children(j).value + ", at height " + ht + "\n")
        }
        {
          j += 1
          j - 1
        }
      }
    }
    else {
      var j: Int = 0
      while (j < h.childCount) {
        {
          if (j > 0) s.append(indent + "(" + children(j).key + ")\n")
          val nextNodeFrag = reader.getNodeFrag(children(j).next.get)
          s.append(toString(nextNodeFrag, ht - 1, indent + "     "))
        }
        {
          j += 1
          j - 1
        }
      }
    }
    s.toString
  }


}

object BTreeScanner {
  def apply(reader: BTReader): BTreeScanner = new BTreeScanner(reader)
}
