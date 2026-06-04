package streams

import cats.effect.{IO, IOApp}
import com.comcast.ip4s.{ipv4, port}
import fs2.{Stream, text}
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import sttp.tapir.*
import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir.server.http4s.Http4sServerInterpreter

import java.nio.charset.StandardCharsets
import scala.concurrent.duration.DurationInt

object Fs203Http extends IOApp.Simple:
  val countToTen: Stream[IO, String] =
    Stream
      .awakeEvery[IO](1.second)
      .map(_.toString + "\n")
      .take(10)

  val counterRoutes = HttpRoutes.of[IO]:
    case GET -> Root / "counter" =>
      Ok(countToTen)

  val httpApp = counterRoutes.orNotFound

  val serverBuilder =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(httpApp)
      .build

  def run: IO[Unit] = serverBuilder.use(_ => IO.never)

object Fs203HttpTapir extends IOApp.Simple:
  val countToTen: Stream[IO, String] =
    Stream
      .awakeEvery[IO](1.second)
      .map(_.toString + "\n")
      .take(10)

  val countToTenEndpoint: Endpoint[Unit, Unit, Unit, Stream[IO, Byte], Fs2Streams[IO]] =
    endpoint
      .in("counter")
      .out(streamTextBody(Fs2Streams[IO])(CodecFormat.TextPlain(), Some(StandardCharsets.UTF_8)))

  val countToTenServerEndpoint = countToTenEndpoint.serverLogicSuccessPure[IO]: _ =>
    countToTen.through(text.utf8.encode)

  val routes = Http4sServerInterpreter[IO]().toRoutes(countToTenServerEndpoint)

  val httpApp = routes.orNotFound

  val serverBuilder =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(httpApp)
      .build

  def run: IO[Unit] = serverBuilder.use(_ => IO.never)
