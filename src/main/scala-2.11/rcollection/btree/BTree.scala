package rcollection.btree

import readers.{BTEntry, BTNode, BTReader}

class BTree(reader: BTReader) {
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

  private def insert(h: BTNode, key: String, value: Int, height: Int): Option[BTNode] = {
    var j: Int = 0

    val t = BTEntry(key, value, None, reader)

    val atExternal = height == 0

    j = atExternal match {
      case true => {
        (0 until h.childCount).find { (ind) =>
          val currKey = h.children(ind).key
          key < currKey
        }.getOrElse(0)
      }
      case false => {
        val r = (0 until h.childCount).find { (ind) =>
          val currKey = h.children(ind + 1).key
          ind + 1 == h.childCount || key < currKey
        }.getOrElse(0) + 1
        r
      }
    }

    val throwUp = {
      val dropNode = h.children(j)
      val u: Option[BTNode] = if(dropNode.next.isDefined) insert(dropNode.next.get, key, value, height - 1) else None
      u match {
        case None => None
        case Some(upSent) => {
          val smallEntry = upSent.children(0)
          t.key = smallEntry.key

          t.next = Some(upSent)
          t.next
        }
      }
    }


    throwUp match {
      case None => None
      case Some(_) => {
        for (i <- h.childCount until 0 by -1; if i > j)
          h.children(i) = h.children(i - 1)

        h.children(j) = t
        h.childCount += 1
        if (h.childCount < M) None else Some(h.split)
      }

    }
  }

}

object BTree {
  val ROOT_POSIT = 0

  def apply(reader: BTReader): BTree = {
    new BTree(reader)
  }

}
