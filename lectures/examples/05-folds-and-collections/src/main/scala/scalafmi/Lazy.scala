package scalafmi

trait LazyIntList:
  def head: Int
  def tail: LazyIntList

case object Nil extends LazyIntList:
  def head: Int = throw new NoSuchElementException
  def tail: LazyIntList = throw new UnsupportedOperationException

case class LazyCons(head: Int, tail: LazyIntList) extends LazyIntList

def doubleLazyList(s: Seq[Int]) =
  s.to(LazyList)
    .map: x =>
      println("Calculating " + x)
      x * 2

val fibs: LazyList[Int] = 0 #:: 1 #:: fibs.zip(fibs.tail).map(_ + _)

@main def lazyExample: Unit =
  val firstFive = fibs.take(50)
  println(fibs)
  println(firstFive.toList)
  println(fibs)
