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

  def testReader(): Unit ={
    val reader = BTReader("test.txt", 5)
    val entryPosit = reader.insertEmptyEntry
    val entry = reader.extractEntry(entryPosit)

    val nodePosits = for(i <- List(2,3,5,7)) yield reader.newNode(i.toString)

    nodePosits.map(reader.extractNode).foreach(println)
    val flowNode = reader.newNode("6")

    entry addNodes nodePosits
    println(entry.toStringR)
    val splitResult = entry.doSplit(reader.extractNode(flowNode))

    println(s"The left entry is ${splitResult._1.toStringR}")
    println(s"The middle node is ${splitResult._2}")
    println(s"The right entry is ${splitResult._3.toStringR}")

  }
}
