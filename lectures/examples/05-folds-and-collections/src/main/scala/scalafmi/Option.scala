package scalafmi

sealed trait Option[+A]:
  def get: A = this match
    case Some(a) => a
    case _ => throw new NoSuchElementException

  def isEmpty: Boolean = this match
    case Some(_) => true
    case _ => false

  def fold[B](ifEmpty: => B)(ifSome: A => B): B =
    this match
      case Some(a) => ifSome(a)
      case _ => ifEmpty

  def getOrElse[AA >: A](alternative: => AA): AA = fold(alternative)(identity)
  def orElse[AA >: A](alternative: => Option[AA]): Option[AA] = fold(alternative)(Some.apply)

  def isDefined: Boolean = !isEmpty

  def map[B](f: A => B): Option[B] = fold(None)(a => Some(f(a)))

  def flatMap[B](f: A => Option[B]): Option[B] = fold(None)(f)

  def filter(p: A => Boolean): Option[A] = fold(None)(a => if p(a) then this else None)
  def withFilter(p: A => Boolean): Option[A] = filter(p)

case class Some[A](a: A) extends Option[A]
object None extends Option[Nothing]

object Option:
  def apply[A](a: A): Option[A] = Some(a)

def sum(maybeA: Option[Int], maybeB: Option[Int]): Option[Int] =
  for
    a <- maybeA
    if isEven(a)
    b <- maybeB
  yield a + b

def sumTheJavaCppWay(maybeA: Option[Int], maybeB: Option[Int]): Option[Int] =
  if !maybeA.isEmpty && !maybeB.isEmpty then Some(maybeA.get + maybeB.get)
  else None

def compose[A, B, C, R](f: A => Option[B], g: B => Option[C], h: (B, C) => Option[R])(a: A): Option[R] =
  for
    b <- f(a)
    c <- g(b)
    r <- h(b, c)
  yield r

def composeJava[A, B, C, R](f: A => Option[B], g: B => Option[C], h: (B, C) => Option[R])(a: A): Option[R] =
  val maybeB = f(a)
  if maybeB.isDefined then
    val maybeC = g(maybeB.get)
    if maybeC.isDefined then h(maybeB.get, maybeC.get)
    else None
  else None

@main
def optionTest = println:
  sum(Option(42), Option(1000)).getOrElse(0)

  val optApple: Option[Apple] = Some(Apple("blue", 10))
  val optFruit: Option[Fruit] = optApple.orElse(Some(Apple("orange", 4)))

  val optApple2: Option[Apple] = None
  val optApple3: Option[Orange] = None
  val optApple4: Option[Fruit] = None

  optApple.orElse[Fruit](Some(Orange("orange", 4)))

  Option(42).orElse(Option(1000)).getOrElse(0)
