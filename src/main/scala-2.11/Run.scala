import rcollection.btree.BTree
import readers.BTReader

/**
  * Created by rotor on 3/17/2016.
  */
object Run extends App {

  val SIZE = 8
  /**
    * Always keep entry ordered.
    * An entry's value is the value of it's largest node key value.
    */
  val list = List("abra", "centaur", "zebra", "kadabra", "boo", "Hello", "how", "are")

  testReader()


  def testDrops(l : List[String]){
    println(Math.floorDiv(SIZE, 2))
    val right = l.drop(Math.floorDiv(SIZE, 2))
    val left = l.dropRight(Math.floorDiv(SIZE, 2))
    val middle = l(SIZE / 2)
    println(right)
    println(left)
    println(middle)
  }

  def testReader(): Unit = {
    val reader = BTReader("test.txt", 4)
    val btree = BTree(reader)
    btree.put("Hello", 22)

  }
}
