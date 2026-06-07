package effects.cats

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.catsSyntaxTuple2Semigroupal
import cats.syntax.applicative.*
import cats.syntax.flatMap.*
import cats.syntax.monad.*
import effects.cats.user.{Email, User}

import scala.concurrent.duration.DurationInt

@main def runFlatMapMonadMonadErrorDemo =
  // FlatMap

  // flatten, flatMap, ...

  import cats.syntax.either.*
  import cats.syntax.applicativeError.*
  val error: Either[Int, String] = 2.raiseError.recoverWith:
    case 1 => "It's one".asRight
  println(error)

  def verifyUser(user: User): IO[Boolean] = ???
  def acceptUser(user: User): IO[String] = ???
  def storeUserForReview(user: User): IO[String] = ???

  def registerUser(user: User): IO[String] =
    verifyUser(user).ifM(
      ifTrue = acceptUser(user),
      ifFalse = storeUserForReview(user)
    )
  val user = User("A", Email("a", "gmail.com"), "123")
  //  registerUser(user)

  (1.pure[IO] >>= IO.println) >> IO.println("Second thing to print")

  // Monad

  def asyncIncrement(n: Int) =
    IO.println(s"Contacting our incremental service to increment $n...")
      *> IO(n + 1)

  val asyncCalculation = 1.iterateWhileM(asyncIncrement)(_ < 10)

  println(asyncCalculation.unsafeRunSync())

  def doSomething: IO[Int] = IO.sleep(2.seconds) *> 42.pure

  import cats.~>

  println:
    (doSomething, doSomething).tupled.timed.unsafeRunSync()

  // ApplicativeError & MonadError
  // They add things like recoverWith, orElse, ...
