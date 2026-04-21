package console

import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

class Console(blockingEc: ExecutionContext):
  def getStringLine: Future[String] = Future(StdIn.readLine())(using blockingEc)
  def putStringLine(str: String): Future[Unit] = Future(println(str))(using blockingEc)
