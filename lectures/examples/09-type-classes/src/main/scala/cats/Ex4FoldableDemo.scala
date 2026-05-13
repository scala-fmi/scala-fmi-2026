package cats

import cats.syntax.foldable.*

@main def runFoldableDemo =
  def doSomething[F[_]: Foldable, A: Monoid](f: F[A]): A =
    println:
      f.forall(_ != Monoid[A].empty)

    println:
      f.foldMap(_.toString)

    f.combineAll

  println:
    doSomething(List(10, 20, 30, 42))

  def forAll[F[_]: Foldable, A](f: F[A])(predicate: A => Boolean): Boolean =
    f.foldRight(Eval.True)((next, acc) =>
      println(s"Evaluating for $next")

      if !predicate(next) then Eval.False
      else acc
    ).value

//  println(forAll(List(2, 4, 3, 8, 0))(_ % 2 == 0))
//  println(forAll(LazyList.from(0))(_ % 2 == 0))
