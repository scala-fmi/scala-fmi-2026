package sql

import cats.effect.{IO, IOApp}
import cats.syntax.flatMap.*
import doobie.*
import doobie.implicits.*

object DoobieApp extends IOApp.Simple:
  val dbTransactor = Transactor.fromDriverManager[IO](
    driver = "org.postgresql.Driver",
    url = "jdbc:postgresql:world",
    user = "postgres",
    password = "password",
    logHandler = None
  )

  def run: IO[Unit] = Doobie03Fragments.ex2.transact(dbTransactor).map(_.mkString("\n")) >>= IO.println
