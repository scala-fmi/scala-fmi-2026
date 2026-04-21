package io

import concurrent.ExecutionContexts
import product.ProductFactory

import java.util.concurrent.Executor
import scala.concurrent.ExecutionContext
import scala.io.StdIn
import scala.util.{Success, Failure, Try}

//case class IORuntime(defaultEc: ExecutionContext, blockingEc: ExecutionContext, shutdown: () => Unit)
//
//object IORuntime:
//  val default = IORuntime(
//    ExecutionContexts.default,
//    ExecutionContexts.blocking,
//    () => ExecutionContexts.blocking.shutdown()
//  )

sealed trait IO[+A]:
  def map[B](f: A => B): IO[B] = flatMap(a => IO.of(f(a)))
  def flatMap[B](f: A => IO[B]): IO[B] = FlatMap(this, f)

  def >>=[B](f: A => IO[B]): IO[B] = flatMap(f)
  def >>[B](nextIO: IO[B]): IO[B] = flatMap(_ => nextIO)

  infix def zip[B](other: IO[B]): IO[(A, B)] = ???

  def unsafeRun(): A = this match
    case Pure(value) => value
    case Delay(delayedValue) => delayedValue()
    case FlatMap(io, f) => f(io.unsafeRun()).unsafeRun()

  def unsafeRunAsync(callback: Callback[A])(ec: ExecutionContext): Unit =
    def execute(work: => Any): Unit = ec.execute(() => work)

    this match
      case Pure(value) => execute(callback(Success(value)))
      case Delay(delayedValue) => execute(callback(Success(delayedValue())))
      case FlatMap(prevIO, f) =>
        execute:
          prevIO.unsafeRunAsync {
            case Success(value) => f(value).unsafeRunAsync(callback)(ec)
            case Failure(exception) => callback(Failure(exception))
          }(ec)

case class Pure[A](value: A) extends IO[A]
case class Delay[A](delayedValue: () => A) extends IO[A]
case class FlatMap[A, B](ioA: IO[A], f: A => IO[B]) extends IO[B]

type Callback[-A] = Try[A] => Unit

object IO:
  def apply[A](delayedValue: => A): IO[A] = Delay(() => delayedValue)
  def of[A](value: A): IO[A] = Pure(value)

  def println(str: String): IO[Unit] = IO(Predef.println(str))
  def readln: IO[String] = IO(StdIn.readLine())

@main
def bookProducingExample(): Unit =
  val produceBook: IO[Product] = IO(ProductFactory.produceProduct("book"))
  def produce2Books: IO[(Product, Product)] = produceBook zip produceBook

  val program = for
    book <- produceBook
    book2 <- produceBook
    _ <- IO.println(book.toString)
    _ <- IO.println(book2.toString)
  yield ()

  program.unsafeRunAsync(println)(ExecutionContexts.default)

  Thread.sleep(5000)

//@main
//def largerComposition(): Unit =
//  val task1: IO[Int] =
//    val computation = IO:
//      Thread.sleep(2000)
//      42
//
//    IO.println("Running task 1") >> computation
//
//  val task2: IO[Int] =
//    val computation = IO:
//      Thread.sleep(2000)
//      10
//
//    IO.println("Running task 2") >> computation
//
//  def double(n: Int): IO[Int] =
//    val computation = IO:
//      Thread.sleep(1000)
//      n * 2
//
//    IO.println("Running double") >> computation
//
//  val composedResult = for
//    (result1, result2) <- task1 zip task2
//    doubledSum <- double(result1 + result2)
//    _ <- IO.println(s"Result: $doubledSum")
//  yield doubledSum
//
//  composedResult.unsafeRunSync(IORuntime.default)
//  IORuntime.default.shutdown()
