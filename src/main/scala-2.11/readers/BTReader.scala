package readers

import java.io.File

import breeze.io.RandomAccessFile

import scala.collection.mutable.ListBuffer

/**
  * Random access file abstractions.
  *
  * @param filePath
  * The path to the RAF file.
  */
case class BTReader(filePath: String, ENTRY_SIZE: Int) {
  val CHAR_SIZE = 256
  val FIRST_OCCUR = 1

  val POSIT: String = "position"
  val READ_WRITE = "rw"

  val file: File = new File(filePath)

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
    * The file to place the empty  in.
    * @return
    * The position of the readers.Entry.
    */
  def insertEmptyEntry(implicit file: RandomAccessFile = raf): Long = {
    val at = file.getFilePointer
    for (i <- 0 until ENTRY_SIZE) file.writeLong(-1)
    for (i <- 0 to ENTRY_SIZE) file.writeLong(-1)
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
  def newEntryAt(at: Long, neighbors: ListBuffer[Long], children: ListBuffer[Long])(implicit file: RandomAccessFile = raf): Long = {
    val posit = file.getFilePointer
    file.seek(at)
    neighbors.padTo(ENTRY_SIZE - 1, -1L).foreach(file.writeLong)
    children.padTo(ENTRY_SIZE, -1L).foreach(file.writeLong)
    file.seek(posit)
    at
  }


  /**
    * Creates node on the RAF file.
    *
    * @param key
    *              The key for the created readers.Node.
    * @param left
    *              The key for the left child if any.
    * @param right The key for the right child if any.
    * @param file  The RAF file to place node in.
    * @return
    * The position of the node.
    */
  def newNode(key: String, left: Option[Long] = None, right: Option[Long] = None)(implicit file: RandomAccessFile = raf): Long = {
    val at = file.getFilePointer

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
    * @param key
    *              The key for the created readers.Node.
    * @param left
    *              The key for the left child if any.
    * @param right The key for the right child if any.
    * @param file  The RAF file to place node in.
    * @return
    * The position of the node.
    */
  def newNodeAt(key: String, left: Option[Long], right: Option[Long], at: Long)(implicit file: RandomAccessFile = raf): Long = {
    val orig = file.getFilePointer
    file.seek(at)

    val word = key.padTo(CHAR_SIZE, " ").mkString
    file.writeChars(word)

    file.writeInt(FIRST_OCCUR)

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
  def extractNode(at: Long)(implicit file: RandomAccessFile = raf): BTNode = {
    file.seek(at)
    val wordChars = file.readChar(CHAR_SIZE)
    val word = wordChars.mkString.filterNot(_.isSpaceChar)
    val occurrence = file.readInt

    BTNode(at, word, occurrence, this)
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
  def extractEntry(at: Long)(implicit file: RandomAccessFile = raf): BTEntry = {
    file.seek(at)
    val neighbors = for (i <- 0 until ENTRY_SIZE) yield {
      file.readLong()
    }
    val children = for(i <- 0 to ENTRY_SIZE) yield {
      file.readLong()
    }
    val existing = neighbors.filterNot(_ == -1)
    val born = children.filterNot(_ == -1)
    val e = BTEntry(at, existing.to[ListBuffer], this, born.to[ListBuffer])
    e
  }

}
