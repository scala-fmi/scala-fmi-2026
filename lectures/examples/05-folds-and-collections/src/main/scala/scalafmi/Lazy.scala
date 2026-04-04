package scalafmi

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

trait FmiLazyList[+A]:
  def head: A
  def tail: FmiLazyList[A]
class LazyCons[A](h: => A, t: => FmiLazyList[A]) extends FmiLazyList[A]:
  lazy val head = h
  lazy val tail = t

  println(s"LazyCons $head")

case object LazyNil extends FmiLazyList[Nothing]:
  def head: Nothing = throw new NoSuchElementException()

  def tail: FmiLazyList[Nothing] = throw new UnsupportedOperationException()

def from(initial: Int, step: Int = 1): FmiLazyList[Int] = LazyCons(initial, from(initial + step, step))
val naturalNumbers = from(0)

@main
def fmilazylistexample =
//  val list = LazyCons(0, LazyCons(1, LazyCons(2, LazyNil)))
  println(naturalNumbers.head)
  println(naturalNumbers.tail.tail.tail.head)
  println(naturalNumbers.tail.tail.tail.head)
