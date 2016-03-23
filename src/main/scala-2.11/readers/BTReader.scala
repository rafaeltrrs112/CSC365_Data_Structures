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
case class BTReader(filePath: String, M: Int) {
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
  def makeNode(implicit file: RandomAccessFile = raf): Long = {
    val at = file.getFilePointer
    for (i <- 0 until M) file.writeLong(-1)
    for (i <- 0 to M) file.writeLong(-1)
    at
  }

  def writeNode(at: Long, childCount: Int, children: Array[Long])(implicit file: RandomAccessFile = raf): Long = {
    val posit = file.getFilePointer
    file.seek(at)

    file.writeInt(childCount) //Write the number of children

    for(i <- 0 until childCount) file.writeLong(children(i)) // write the position of the children
    for(i <- 0 until (M - childCount)) file.writeLong(-1) // pad the position of the children up to M positions with bad pointers

    // [childCount, [children ... ]]
    file.seek(posit)
    at
  }

  def toNode(at : Long) : BTNode = ???

  def toEntry(at : Long) : BTEntry = ???

//  /**
//    * Creates node on the RAF file.
//    *
//    * @param key
//    *              The key for the created readers.Node.
//    * @param left
//    *              The key for the left child if any.
//    * @param right The key for the right child if any.
//    * @param file  The RAF file to place node in.
//    * @return
//    * The position of the node.
//    */
//  def newNode(key: String, left: Option[Long] = None, right: Option[Long] = None)(implicit file: RandomAccessFile = raf): Long = {
//    val at = file.getFilePointer
//
//    val word = key.padTo(CHAR_SIZE, " ").mkString
//    file.writeChars(word)
//
//    file.writeInt(FIRST_OCCUR)
//
//    file.writeLong(if (left.isDefined) left.get else -1)
//    file.writeLong(if (right.isDefined) right.get else -1)
//
//    at
//  }
//
//  /**
//    * Creates node on the RAF file.
//    *
//    * @param key
//    *              The key for the created readers.Node.
//    * @param left
//    *              The key for the left child if any.
//    * @param right The key for the right child if any.
//    * @param file  The RAF file to place node in.
//    * @return
//    * The position of the node.
//    */
//  def newNodeAt(key: String, left: Option[Long], right: Option[Long], at: Long)(implicit file: RandomAccessFile = raf): Long = {
//    val orig = file.getFilePointer
//    file.seek(at)
//
//    val word = key.padTo(CHAR_SIZE, " ").mkString
//    file.writeChars(word)
//
//    file.writeInt(FIRST_OCCUR)
//
//    file.seek(orig)
//    at
//  }
//
//  /**
//    * Creates an object representation of a readers.Node from the RAF file tree.
//    *
//    * @param at
//    * The starting file position of this readers.Node.
//    * @param file
//    * The file that contains the readers.Node.
//    * @return
//    * An object representation of the readers.Node from the RAF file.
//    */
//  def extractNode(at: Long)(implicit file: RandomAccessFile = raf): BTEntry = {
//    file.seek(at)
//    val wordChars = file.readChar(CHAR_SIZE)
//    val word = wordChars.mkString.filterNot(_.isSpaceChar)
//    val occurrence = file.readInt
//
//    BTEntry(at, word, occurrence, this)
//  }
//
//  /**
//    * Extracts an readers.Entry from the RAF file.
//    *
//    * @param at
//    * The starting file position of this readers.Entry.
//    * @param file
//    * The file that contains the readers.Entry.
//    * @return
//    * An object representation of the readers.Entry from the RAF file
//    */
//  def extractEntry(at: Long)(implicit file: RandomAccessFile = raf): BTNode = {
//    file.seek(at)
//    val neighbors = for (i <- 0 until M) yield {
//      file.readLong()
//    }
//    val children = for(i <- 0 to M) yield {
//      file.readLong()
//    }
//    val existing = neighbors.filterNot(_ == -1)
//    val born = children.filterNot(_ == -1)
//    val e = BTNode(at, existing.to[ListBuffer], this, born.to[ListBuffer])
//    e
//  }

//  case class NodeWriter(reader: BTReader){
//    def ~> (node: BTNode): Unit = {
//      reader.toN
//    }
//  }

}
