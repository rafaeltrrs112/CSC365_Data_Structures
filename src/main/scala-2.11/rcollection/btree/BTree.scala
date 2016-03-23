package rcollection.btree

import readers.{BTEntry, BTNode, BTReader}

class BTree(reader: BTReader) extends Iterable[(String, Int)] {
  implicit val impReader = reader
  val M = reader.M
  var N = 0
  var root: BTNode = BTNode(0)
  var height = 0


  def put(key: String, value: Int): Unit = {
    val u = insert(root, key, value, height)
    N += 1

    u match {
      case None => ()
      case Some(up) =>
        val t = BTNode(2)
        t.children(0) = BTEntry(root.children(0).key, -1, Some(root), reader)
        t.children(1) = BTEntry(up.children(0).key, -1, Some(up), reader)
        root = t
        height += 1
    }

  }

  def get(key: String): Option[Int] = search(root, key, height)

  private def insert(h: BTNode, key: String, value: Int, height: Int): Option[BTNode] = {
    val t = BTEntry(key, value, None, reader)

    val atExternal = height == 0

    val j = if (atExternal) wedgePosit(h, key) else dropPosition(h, key)

    atExternal match {
      case true => {
        shiftInsert(h, t, j)
      }
      case false =>
        val dropPosit = j + 1
        val drop = continueTraverse(h, t, j, key, value, height - 1)
        drop match {
          case None => None
          case Some(upSent) =>
            t.key = upSent.children(0).key
            t.next = Some(upSent)
            shiftInsert(h, t, dropPosit)
        }
    }
  }

  private def wedgePosit(h: BTNode, key: String): Int = {
    (0 until h.childCount).find { (ind) =>
      val currKey = h.children(ind).key
      key.compareTo(currKey) < 0
    } getOrElse {
      h.childCount
    }
  }

  private def dropPosition(h: BTNode, key: String): Int = {
    val r = (0 until h.childCount).find { (ind) =>
      ind + 1 == h.childCount || key.compareTo(h.children(ind + 1).key) < 0
    } getOrElse {
      h.childCount
    }
    r
  }

  private def shiftInsert(h: BTNode, t: BTEntry, j: Int): Option[BTNode] = {
    for (i <- h.childCount until j by -1) {
      h.children(i) = h.children(i - 1)
    }

    h.children(j) = t
    h.childCount += 1
    if (h.childCount < M) None else Some(h.split)
  }

  private def continueTraverse(h: BTNode, t: BTEntry, j: Int, key: String, value: Int, height: Int): Option[BTNode] = {
    insert(h.children(j).next.get, key, value, height)
  }

  @scala.annotation.tailrec
  private def search(x: BTNode, key: String, height: Int): Option[Int] = {
    val children = x.children

    val externalNode = height == 0


    externalNode match {
      case true =>
        val kvMap = (0 until x.childCount).map { (ind) => (children(ind).key, children(ind).value) }
        val found = kvMap.find(_._1 == key)
        found match {
          case Some(r) => Some(r._2)
          case None => None
        }
      case false =>
        val travInd = findTraverseInd(x, key, height)
        travInd match {
          case Some(ind) => search(children(ind).next.get, key, height - 1)
          case None => None
        }
    }
  }

  private def findTraverseInd(x: BTNode, key: String, height: Int): Option[Int] = {
    if (x.childCount == 0) None
    else (0 until x.childCount).find { (ind) =>
      ind + 1 == x.childCount || (key.compareTo(x.children(ind + 1).key) < 0)
    }
  }


  override def toString: String = {
    toString(root, height, "") + "\n"
  }

  private def toString(h: BTNode, ht: Int, indent: String): String = {
    val s: StringBuilder = new StringBuilder
    val children: Array[BTEntry] = h.children
    if (ht == 0) {
      (0 until h.childCount).foreach { (ind) =>
        s.append(indent + children(ind).key + " " + children(ind).value + ", at height " + ht + "\n")
      }
    }
    else {
      (0 until h.childCount).foreach { (ind) =>
        s ++= toString(children(ind).next.get, ht - 1, indent + "     ")
      }
    }
    s.toString
  }

  override def iterator: Iterator[(String, Int)] = toSeq(root, height).iterator

  override def toSeq: Seq[(String, Int)] = toSeq(root, height)

  private def toSeq(h: BTNode, ht: Int): Seq[(String, Int)] = {
    val children: Array[BTEntry] = h.children
    if (ht == 0) {
      (0 until h.childCount).map { (ind) =>
        (children(ind).key, children(ind).value)
      }
    }
    else {
      (0 until h.childCount).flatMap { (ind) =>
        toSeq(children(ind).next.get, ht - 1)
      }
    }
  }

  private def iterFind(x: BTNode, key: String): Option[Int] = (0 until x.childCount).find(key == x.children(_).key)

}


object BTree {
  def apply(reader: BTReader): BTree = {
    new BTree(reader)
  }

}
