package effects.cats

import cats.data.OptionT
import cats.effect.IO
import cats.syntax.all.*
import cats.{Applicative, Functor, Monad, Traverse}

@main def runCompositionDemo =
  val listOfOptions: List[Option[Int]] = List(Some(1), None, Some(2))
  val anotherListOfOptions: List[Option[Int]] = List(None, Some(10), Some(20))

  val ex1 = listOfOptions.map(_.map(_ + 1))
  val ex2 = Functor[List].compose[Option].map(listOfOptions)(_ + 1)

  val ex3 = listOfOptions.map2(anotherListOfOptions)((a, b) => a.map2(b)(_ + _))
  val ex4 = Applicative[List].compose[Option].map2(listOfOptions, anotherListOfOptions)(_ + _)

  println(ex1)
  println(ex2)
  println(ex3)
  println(ex4)

  val ex5 = listOfOptions.nested.map(_ + 1).map(_.toString).value
  val ex6 = listOfOptions.nested.map2(anotherListOfOptions.nested)(_ + _).value

  println(ex5)
  println(ex6)

  // Monad composition cannot be defined generically like we did for Functor or Applicative
  // Monads can be composed only if G is a Traverse, i.e. the way composition is done depends on the nested effect
  def composedMonad[F[_]: Monad, G[_]: {Monad, Traverse}] =
    new Monad[[A] =>> F[G[A]]]:
      def pure[A](x: A): F[G[A]] = x.pure[G].pure[F]

      def flatMap[A, B](fga: F[G[A]])(f: A => F[G[B]]): F[G[B]] = 
        fga.flatMap: ga =>
          val gfgb = Monad[G].map(ga)(f)
          val fggb = gfgb.sequence
          val fgb = fggb.map(_.flatten)

          fgb

      def tailRecM[A, B](a: A)(f: A => F[G[Either[A, B]]]): F[G[B]] = ???

  // OptionT allow us to compose any monad with an Option
  // There is also a variant called EitherT for Eithers
  def monadComposition: IO[Option[String]] =
    val greetingIOO: IO[Option[String]] = "Hello".some.pure

    val firstnameIO: IO[String] = "Jane".pure

    val lastnameO: Option[String] = "Doe".some

    val greeting: OptionT[IO, String] =
      for
        g <- OptionT(greetingIOO)
        f <- OptionT.liftF(firstnameIO)
        l <- lastnameO.toOptionT
      yield s"$g $f $l"

    greeting.value
