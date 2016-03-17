package readers

import java.io.File

import breeze.io.RandomAccessFile

/**
  * Random access file abstractions.
  *
  * @param PATH
  * The path to the RAF file.
  */
case class BTreeRafReader(val PATH: String) {
  val CHAR_SIZE = 256
  val FIRST_OCCUR = 1

  val POSIT: String = "position"
  val READ_WRITE = "rw"

  val file: File = new File(PATH)

  val raf: RandomAccessFile = new RandomAccessFile(file, READ_WRITE)

  raf.seek(0)

  /**
    * Closes the inner RAF file.
    */
  def closeAll(): Unit = raf.close()

  /**
    * Creates an empty readers.Entry at the position of the current file pointer.
    *
    * @param file
    * The file to place the empty readers.Entry in.
    * @return
    * The position of the readers.Entry.
    */
  def emptyEntry(implicit file: RandomAccessFile = raf): Long = {
    val at = file.getFilePointer
    for (i <- 0 to 4) file.writeLong(-1)
    at
  }

  /**
    * Places an readers.Entry in the RAF file at a specific location.
    *
    * @param at
    * The position to place the readers.Entry at.
    * @param neighbors
    * The entries neighbors.
    * @param file
    * The file to place the readers.Entry in.
    * @return
    * The position of the readers.Entry.
    */
  def newEntryAt(at: Long, neighbors: Array[Long])(implicit file: RandomAccessFile = raf): Long = {
    val posit = file.getFilePointer
    file.seek(at)
    neighbors.foreach(file.writeLong)
    file.seek(posit)
    at
  }


  /**
    * Creates node on the RAF file.
    *
    * @param entry
    *              The file pointer to the entry the created node resides in.
    * @param key
    *              The key for the created readers.Node.
    * @param left
    *              The key for the left child if any.
    * @param right The key for the right child if any.
    * @param file  The RAF file to place node in.
    * @return
    * The position of the node.
    */
  def newNode(entry: Long, key: String, left: Option[Long] = None, right: Option[Long] = None)(implicit file: RandomAccessFile = raf): Long = {
    val at = file.getFilePointer

    file.writeLong(entry)

    val word = key.padTo(CHAR_SIZE, " ").mkString
    file.writeChars(word)

    file.writeInt(FIRST_OCCUR)

    file.writeLong(if (left.isDefined) left.get else -1)
    file.writeLong(if (right.isDefined) right.get else -1)

    at
  }

  /**
    * Creates node on the RAF file.
    *
    * @param entry
    *              The file pointer to the entry the created node resides in.
    * @param key
    *              The key for the created readers.Node.
    * @param left
    *              The key for the left child if any.
    * @param right The key for the right child if any.
    * @param file  The RAF file to place node in.
    * @return
    * The position of the node.
    */
  def newNodeAt(entry: Long, key: String, left: Option[Long], right: Option[Long], at: Long)(implicit file: RandomAccessFile = raf): Long = {
    val orig = file.getFilePointer
    file.seek(at)
    file.writeLong(entry)

    val word = key.padTo(CHAR_SIZE, " ").mkString
    file.writeChars(word)

    file.writeInt(FIRST_OCCUR)

    file.writeLong(if (left.isDefined) left.get else -1)
    file.writeLong(if (right.isDefined) right.get else -1)
    file.seek(orig)
    at
  }

  /**
    * Creates an object representation of a readers.Node from the RAF file tree.
    *
    * @param at
    * The starting file position of this readers.Node.
    * @param file
    * The file that contains the readers.Node.
    * @return
    * An object representation of the readers.Node from the RAF file.
    */
  def extractNode(at: Long)(implicit file: RandomAccessFile = raf): Node = {
    file.seek(at)
    val entryPosit = file.readLong
    val wordChars = file.readChar(CHAR_SIZE)
    val word = wordChars.mkString.filterNot(_.isSpaceChar)
    val occurrence = file.readInt

    val leftOpt = file.readLong
    val rightOpt = file.readLong

    val leftNeighbor = if (leftOpt == -1) None else Some(leftOpt)
    val rightNeighbor = if (rightOpt == -1) None else Some(rightOpt)

    val n = Node(at, entryPosit, word, occurrence, this)
    n.init(leftNeighbor, rightNeighbor)
    n
  }

  /**
    * Extracts an readers.Entry from the RAF file.
    *
    * @param at
    * The starting file position of this readers.Entry.
    * @param file
    * The file that contains the readers.Entry.
    * @return
    * An object representation of the readers.Entry from the RAF file
    */
  def extractEntry(at: Long)(implicit file: RandomAccessFile = raf): Entry = {
    file.seek(at)
    val neighbors = for (i <- 0 to 4) yield {
      file.readLong()
    }
    val existing = neighbors
    val e = Entry(at, existing.toArray, this)
    e
  }
}
