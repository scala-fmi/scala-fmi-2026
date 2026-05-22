package io

import cats.effect.std.Console
import cats.effect.{Concurrent, IO, IOApp, Ref}
import cats.syntax.all.*

class Worker[F[_]: {Concurrent, Console}](number: Int, ref: Ref[F, Int]):
  def work: F[Unit] =
    for
      c1 <- ref.get
      _ <- Console[F].println(s"Get #$number >> $c1")
      c2 <- ref.modify: x =>
        println(s"Fiber $number modifying $x")
        (x + 1, x)
      _ <- Console[F].println(s"Finished #$number >> $c2")
    yield ()

val program: IO[Int] =
  for
    ref <- Ref[IO].of(0)
    w1 = new Worker[IO](1, ref)
    w2 = new Worker[IO](2, ref)
    w3 = new Worker[IO](3, ref)
    _ <- List(
      w1.work,
      w2.work,
      w3.work
    ).parSequence.void
    refFinal <- ref.get
  yield refFinal

object Ex07Refs extends IOApp.Simple:
  def run: IO[Unit] = program >>= IO.println
