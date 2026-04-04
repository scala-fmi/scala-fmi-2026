package scalafmi.generalizedadts

enum Expression[+A]:
  case LiteralInteger(n: Int) extends Expression[Int]
  case Sum(a: Expression[Int], b: Expression[Int]) extends Expression[Int]
  case Mult(a: Expression[Int], b: Expression[Int]) extends Expression[Int]
  case BooleanLiteral(b: Boolean) extends Expression[Boolean]
  case If(condition: Expression[Boolean], ifTrue: Expression[A], ifFalse: Expression[A]) extends Expression[A]
  case Eq(left: Expression[A], right: Expression[A]) extends Expression[Boolean]

extension (left: Expression[Int])
  def +(right: Expression[Int]) = Expression.Sum(left, right)
  def *(right: Expression[Int]) = Expression.Mult(left, right)

import scalafmi.generalizedadts.Expression.*

def evaluate[A](expression: Expression[A]): A = expression match
  case LiteralInteger(n) => n
  case Sum(a, b) => evaluate(a) + evaluate(b)
  case Mult(a, b) => evaluate(a) * evaluate(b)
  case BooleanLiteral(b) => b
  case If(condition, ifTrue, ifFalse) =>
    if evaluate(condition) then evaluate(ifTrue) else evaluate(ifFalse)
  case Eq(left, right) => evaluate(left) == evaluate(right)

def asString[A](expression: Expression[A]): String = expression match
  case LiteralInteger(n) => n.toString
  case Sum(a, b) => s"${asString(a)} + ${asString(b)}"
  case mult @ (Mult(_: Sum, _) | Mult(_, _: Sum)) =>
    s"(${asString(mult.a)}) * (${asString(mult.b)})"
  case Mult(a, b) => s"${asString(a)} * ${asString(b)}"
  case BooleanLiteral(b) => b.toString
  case If(condition, ifTrue, ifFalse) =>
    s"if ${asString(condition)}\nthen ${asString(ifTrue)}\nelse ${asString(ifFalse)}"
  case Eq(left, right) => s"${asString(left)} == ${asString(right)}"

@main
def testExpressions(): Unit =
  val expression =
    If(
      Eq(LiteralInteger(1) + LiteralInteger(1), LiteralInteger(-2) + LiteralInteger(4)),
      ifTrue = (LiteralInteger(10) + LiteralInteger(1)) * (LiteralInteger(5) + LiteralInteger(33)),
      ifFalse = LiteralInteger(42) * LiteralInteger(10)
    )

  println(s"Expression: \n${asString(expression)}")
  println(s"Value: ${evaluate(expression)}")

import scala.compiletime.ops.int.S

enum FixedList[+A, L <: Int]:
  case Cons[+A, TL <: Int](head: A, tail: FixedList[A, TL]) extends FixedList[A, S[TL]]
  case Nil extends FixedList[Nothing, 0]

  // We will learn about 'using' later in the course
  def size(using v: ValueOf[L]): Int = v.value

def zip[A, B, L <: Int](a: FixedList[A, L], b: FixedList[B, L]): FixedList[(A, B), L] = (a, b) match
  case (FixedList.Nil, FixedList.Nil) => FixedList.Nil
  case (FixedList.Cons(a, aTail), FixedList.Cons(b, bTail)) => FixedList.Cons((a, b), zip(aTail, bTail))

@main
def testFixedList(): Unit =
  import FixedList.*

  val l = Cons(2, Cons(1, Nil))
  val r = Cons("A", Cons("B", Nil))

  println(zip(l, r))

  println(Cons("A", Cons("B", Nil)).tail)
  // Does not compile:
  // println(Nil.tail)

  println(l.size)
  println(Nil.size)
