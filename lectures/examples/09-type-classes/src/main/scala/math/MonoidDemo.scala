package math

import Monoid.given

def alabala[A](using monoid: Monoid[A]) =
  monoid.identity |+| monoid.identity

def sumNonEmpty[A: Semigroup](as: List[A]): A =
  as.reduce(_ |+| _)

def sum[A: Monoid](as: List[A]): A =
  as.fold(Monoid[A].identity)(_ |+| _)

@main
def runMonoidDemo: Unit =
  sum(List(1, 3, 4))
  sum(List("a", "b", "c"))

  println(Rational(1) |+| Rational(2))
  println("a" |+| "b")

  println(sum(List.empty[Rational]))
  println {
    sum(List(Rational(1, 2), Rational(3, 4)))(using Rational.multiplicativeRationalMonoid)
  }

  println:
    Monoid[(Int, String)].combine((1, "a"), (10, "bcd"))

  println:
    sum(List(Some(1), Some(100), None, Some(2312312)))

  val map1 = Map(1 -> (2, Rational(3, 2)), 2 -> (3, Rational(4)))
  val map2 = Map(2 -> (5, Rational(6)), 3 -> (7, Rational(8, 3)))

  sumNonEmpty(List(map1, map2))
