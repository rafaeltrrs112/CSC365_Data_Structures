package readers

import java.io.File

import assignments.a1.{EntryFrag, NodeFrag}
import breeze.io.RandomAccessFile


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

  private var _root: Long = {
//    raf.writeLong(-1)
    raf.readLong()
  }


  def root: Long = _root

  def root_=(posit: Long){
    _root = posit
    updateRoot(posit)
  }

  //Used to represent the key and value pairs of the child entry's of
  //a leaf node when the search algorithm reaches that point.
  def getEntryFrag(at: Long)(implicit file: RandomAccessFile = raf): EntryFrag = {
    val origPosit = file.getFilePointer
    file.seek(at)
    val wordChars = file.readChar(CHAR_SIZE)

    val key = wordChars.mkString.filterNot(_.isSpaceChar)
    val value = file.readInt
    val nextPosit = file.readLong
    val next = nextPosit match {
      case -1L => None
      case _ => Some(nextPosit)
    }

    file.seek(origPosit)
    EntryFrag(key, value, next, this)
  }

  def getNodeFrag(at: Long)(implicit file: RandomAccessFile = raf): NodeFrag = {
    val origPosit = file.getFilePointer
    file.seek(at)
    val childCount = file.readInt
    val childPosits = file.readLong(M)
    val existingChildren = childPosits.filterNot(_ == -1L)

    file.seek(origPosit)
    NodeFrag(childCount, existingChildren, this)
  }

  def sapRoot: NodeFrag = {
    raf.seek(0)
    val rootAt = raf.readLong
    println(rootAt)
    getNodeFrag(rootAt)
  }

  /**
    * Closes the inner RAF file.
    */
  def closeAll(): Unit = raf.close()

  private def makeNode(children : Int)(implicit file: RandomAccessFile = raf): Long = {
    val at = file.getFilePointer
    file.writeInt(children)
    for (i <- 0 until M) file.writeLong(-1)
    at
  }

  def writeNode(at: Long, childCount: Int, children: Array[Long])(implicit file: RandomAccessFile = raf): Long = {
    val posit = file.getFilePointer
    file.seek(at)

    file.writeInt(childCount) //Write the number of children

    for (i <- 0 until childCount) file.writeLong(children(i)) // write the position of the children
    for (i <- 0 until (M - childCount)) file.writeLong(-1) // pad the position of the children up to M positions with bad pointers

    // [childCount, [children ... ]]
    file.seek(posit)
    at
  }

  private def makeEntry(key: String, value: Int, next: Option[BTNode])(implicit file: RandomAccessFile = raf): Long = {
    val at = file.getFilePointer
    file.writeChars(key.padTo(CHAR_SIZE, ' '))
    file.writeInt(value)
    file.writeLong {
      next match {
        case Some(n) => n.at
        case None => -1L
      }
    }
    at
  }

  def writeEntry(at: Long, key: String, value: Int, next: Option[BTNode])(implicit file: RandomAccessFile = raf): Long = {
    val posit = file.getFilePointer
    file.seek(at)

    file.writeChars(key.padTo(CHAR_SIZE, ' ')) //Write the number of children
    file.writeInt(value)
    file.writeLong {
      next match {
        case Some(n) => n.at
        case None => -1L
      }
    }
    file.seek(posit)
    at
  }

  def +=(node: BTNode): Long = makeNode(node.childCount)

  def +=(entry: BTEntry): Long = makeEntry(entry.key, entry.value, entry.next)

  def ~>(node: BTNode): Unit = writeNode(node.at, node.childCount, node.childPosits)

  def ~>(entry: BTEntry): Unit = writeEntry(entry.at, entry.key, entry.value, entry.next)

  private def updateRoot(rootPosit: Long) = {
    val origPosit = raf.getFilePointer

    raf.seek(0)
    raf.writeLong(rootPosit)

    raf.seek(origPosit)
  }
}
