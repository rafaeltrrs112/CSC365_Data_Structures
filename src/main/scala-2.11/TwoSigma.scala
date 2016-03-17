/**
  * Created by rotor on 3/15/2016.
  */
trait Facing {
  def move(current: (Int, Int)): Int
}


object TwoSigma extends App {
  val GO = "G"

  val L = "L"
  val R = "R"

  val N = "N"
  val S = "S"
  val E = "E"
  val W = "W"
  val start = (5, 5)

  var current = (5, 5)
  var facing = N

  run()


  def run(): Unit = {
    val result = isCircle("GLRL")
    println(result)
  }

  def isCircle(s: String): (Int, Boolean) = {
    var trys = 0
    var found = false
    while (trys != 5000 && !found) {
      val test = s.iterator

      for (step <- test) {
        println(current)
        println(facing)
        current = nextStep(step.toString)
      }

      found = current == start
      trys += 1
    }
    (trys, found)
  }

  def nextStep(s: String): (Int, Int) = {
    s match {
      case GO => facing match {
        case N => (current._1, current._2 + 1)
        case S => (current._1, current._2 - 1)
        case E => (current._1 + 1, current._2)
        case W => (current._1 - 1, current._2)
      }
      case L => facing match {
        case N =>
          facing = W
          (current._1 - 1, current._2)
        case S =>
          facing = E
          (current._1 + 1, current._2)
        case E =>
          facing = N
          (current._1, current._2 + 1)
        case W =>
          facing = S
          (current._1, current._2 - 1)
      }
      case R => facing match {
        case N =>
          facing = E
          (current._1 + 1, current._2)
        case S =>
          facing = W
          (current._1 - 1, current._2)
        case E =>
          facing = S
          (current._1, current._2 - 1)
        case W =>
          facing = N
          (current._1, current._2 + 1)
      }
    }
  }

  /*
   * Sub strings can be of size 1 -> l-1.
   *
   * abcd
   */
  def palindrome(str: String): Int = {
    val posits = allSlices(str)
    val words = posits.map(s => str.substring(s._1, s._2))
    val uniques = words.distinct
    val palindromes = uniques.filter(isPalindrome)
    println(palindromes)
    palindromes.size
  }

  def allSlices(s: String): Seq[(Int, Int)] = {
    val sliceList = for (i <- 1 to s.length) yield slices(s, i)
    sliceList.flatten
  }

  def slices(s: String, cut: Int): Seq[(Int, Int)] = {
    val prev = 0
    val indices = s.indices
    val r = for (i <- indices; if (i + cut - 1) <= indices.last) yield (i, i + cut)
    r
  }


  def isPalindrome(s: String): Boolean = {
    val backWards = s.reverse
    val stringIt = s.iterator

    !backWards.exists(_ != stringIt.next())
  }
}
