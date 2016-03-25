import assignments.a1.BTreeScanner
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
//    val btree = BTree(reader)
//
//    btree.put("www.apple.com", 5)
//    btree.put("www.amazon.com", 6)
//    btree.put("www.cs.princeton.edu", 1)
//    btree.put("www.princeton.edu", 2)
//    btree.put("www.yale.edu", 3)
//    btree.put("www.simpsons.com", 4)
//    btree.put("www.ebay.com", 7)
//    btree.put("www.cnn.com", 8)
//    btree.put("www.google.com", 9)
//    btree.put("www.nytimes.com", 10)
//    btree.put("www.microsoft.com", 11)
//    btree.put("www.dell.com", 12)
//    btree.put("www.slashdot.org", 13)
//    btree.put("www.espn.com", 14)
//    btree.put("www.weather.com", 15)
//    btree.put("www.yahoo.com", 16)

    val scanner = BTreeScanner(reader)


//    println(btree.root.at)
//
//    println(s"Height is ${btree.height}")
//
//    println(btree.get("www.apple.com"))
//    println(btree.get("www.amazon.com"))
//    println(btree.get("www.cs.princeton.edu"))
//    println(btree.get("www.princeton.edu"))
//    println(btree.get("www.yale.edu"))
//    println(btree.get("www.simpsons.com"))
//    println(btree.get("www.ebay.com"))
//    println(btree.get("www.cnn.com"))
//    println(btree.get("www.google.com"))
//    println(btree.get("www.nytimes.com"))
//    println(btree.get("www.microsoft.com"))
//    println(btree.get("www.dell.com"))
//    println(btree.get("www.slashdot.org"))
//    println(btree.get("www.espn.com"))
//    println(btree.get("www.weather.com"))
//    println(btree.get("www.yahoo.com"))

    println(scanner.get("www.apple.com"))
//    println(scanner.get("www.apple.com"))
//    println(scanner.get("www.amazon.com"))

    //    println(scanner.get("www.apple.com"))
//    println(scanner.get("www.amazon.com"))
//    println(scanner.get("www.cs.princeton.edu"))
//    println(scanner.get("www.princeton.edu"))
//    println(scanner.get("www.yale.edu"))
//    println(scanner.get("www.simpsons.com"))
//    println(scanner.get("www.ebay.com"))
//    println(scanner.get("www.cnn.com"))
//    println(scanner.get("www.google.com"))
//    println(scanner.get("www.nytimes.com"))
//    println(scanner.get("www.microsoft.com"))
//    println(scanner.get("www.dell.com"))
//    println(scanner.get("www.slashdot.org"))
//    println(scanner.get("www.espn.com"))
//    println(scanner.get("www.weather.com"))
//    println(scanner.get("www.yahoo.com"))

//    println(btree.entryPointers.map(reader.getEntryFrag).map((ent) => (ent.key, ent.value)))

//    println(scanner.toString(reader.getNodeFrag(reader.root), 2, "   "))

  }
}
