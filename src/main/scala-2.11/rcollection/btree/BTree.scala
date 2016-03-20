package rcollection.btree

import rcollection.map.KVPair
import readers.{BTEntry, BTNode, BTReader}

class BTree[K, V](reader: BTReader, SIZE: Int)(var root: BTEntry) {

  def insert(node: KVPair[K, V]): Unit = {

  }

  def tryInsert(entry: BTEntry, addNode: BTNode): Option[(BTEntry, BTNode, BTEntry)] = {
    entry.isFull match {
      case true => Some(entry.doSplit(addNode))
      case false =>
        entry addNode addNode.posit
        None
    }
  }

}

object BTree {
  val ROOT_POSIT = 0

  def apply[K, V](reader: BTReader, entrySize: Int): BTree[K, V] = {
    val root = reader.extractEntry(ROOT_POSIT)
    new BTree[K, V](reader, entrySize)(root)
  }

}
