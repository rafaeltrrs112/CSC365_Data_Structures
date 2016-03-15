package rcollection.map

/**
  * Created by rotor on 3/14/2016.
  */
case class BTNode[K, V](val key: K, val value: V,
                        var left: Option[BTNode[K, V]] = None,
                        var right: Option[BTNode[K, V]] = None)

case class BTEntry[K <: String, V](nodes: List[BTNode[K, V]] = List()) {
  def isFull: Boolean = nodes.size == 2

  def sort(): Unit = nodes.sortWith { (a, b) =>
    a.key.compareTo(b.key) < 0
  }
}

class BTree[K, V] {
  var root = BTEntry()

  def insert(node: KVPair[K, V]): Unit = {

  }

}
