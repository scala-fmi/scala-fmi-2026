package io

import scala.io.StdIn

enum IO[+A]:
  case Pure(a: A)
  case Delayed(delayedValue: () => A)
  case FlatMap[A, B](ioA: IO[A], f: A => IO[B]) extends IO[B]
  case Println(str: String) extends IO[Unit]
  case Readln extends IO[String]

  def map[B](f: A => B): IO[B] = flatMap(a => IO.of(f(a)))
  def flatMap[B](f: A => IO[B]): IO[B] = FlatMap(this, f)

  def >>=[B](f: A => IO[B]): IO[B] = flatMap(f)
  def >>[B](continuation: => IO[B]): IO[B] = flatMap(_ => continuation)

  def unsafeRun(): A = this match
    case IO.Pure(a) => a
    case IO.Delayed(delayedValue) => delayedValue()
    case IO.Println(str) => println(str)
    case IO.Readln => StdIn.readLine()
    case IO.FlatMap(io, f) => f(io.unsafeRun()).unsafeRun()

object IO:
  def apply[A](a: => A): IO[A] = IO.Delayed(() => a)
  def of[A](a: A): IO[A] = IO.Pure(a)

  def println(str: String): IO[Unit] = IO.Println(str)
  def readln: IO[String] = IO.Readln

@main
def testIO(): Unit =
  val print = IO.println("Hello World")

  val l1 = List(print, print)

  val l2 = List(IO.println("Hello World"), IO.println("Hello World"))

//  l1.foreach(_.unsafeRun())
//  l2.foreach(_.unsafeRun())

  val program = for
    _ <- IO.println("Hello!!! What's your name:")
    name <- IO.readln
    age <- IO.of(20)
    _ <- IO.println(s"Hey $name!!! Good to see you at $age years old :)!")
  yield ()

  val program2 =
    IO.println("Hello!!! What's your name:")
      >> IO.readln
      >>= { name => IO.println(s"Hey $name!!! Good to see you :)!") }

  val program3 =
    for
      _ <- IO.println("Enter file name:")
      fileName <- IO.readln
      fileLines <- FileIO.readFileLines(fileName)
      numberOfLines = fileLines.size
      _ <- IO.println(s"The file $fileName has $numberOfLines:")
      _ <- IO.println(fileLines.mkString("\n"))
    yield ()

//  program3.unsafeRun()

  def loop(n: Int): IO[Unit] =
    for
      _ <- IO.println(n.toString)
      _ <- loop(n + 1)
    yield ()

  loop(0).unsafeRun()

// Advantages of IO
//
// * It's a value - can combine, optimize, etc.
// * Asynchronicity
// * Cancellation
// * Scheduling and retrying
// * Test and production instances
// * Different frontends

// associativity:
// (a + b) + c == a + (b + c)
//
// FlatMap(FlatMap(io, f), g)
// ==
// FlatMap(io, a => FlatMap(f(a), g))

// Option(x).flatMap(f).flatMap(g)
// ==
// Option(x).flatMap(x => f(x).flatMap(g))
