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


  def testDrops(l: List[String]) {
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

    btree.put("www.cs.princeton.edu", 1)
    btree.put("www.princeton.edu", 2)
    btree.put("www.yale.edu", 3)
    btree.put("www.simpsons.com", 4)
    btree.put("www.apple.com", 5)
    btree.put("www.amazon.com", 6)
    btree.put("www.ebay.com", 7)
    btree.put("www.cnn.com", 8)
    btree.put("www.google.com", 9)
    btree.put("www.nytimes.com", 10)
    btree.put("www.microsoft.com", 11)
    btree.put("www.dell.com", 12)
    btree.put("www.slashdot.org", 13)
    btree.put("www.espn.com", 14)
    btree.put("www.weather.com", 15)
    btree.put("www.yahoo.com", 16)

//    println("cs.princeton.edu:  " + btree.get("www.cs.princeton.edu"))
//    println("hardvardsucks.com: " + btree.get("www.harvardsucks.com"))
//    println("simpsons.com:      " + btree.get("www.simpsons.com"))
//    println("apple.com:         " + btree.get("www.apple.com"))
//    println("ebay.com:          " + btree.get("www.ebay.com"))
//    println("dell.com:          " + btree.get("www.dell.com"))
//    println("yahoo.com          " + btree.get("www.yahoo.com"))

    println(btree.toSeq.sortWith(_._2 < _._2))

  }
}
