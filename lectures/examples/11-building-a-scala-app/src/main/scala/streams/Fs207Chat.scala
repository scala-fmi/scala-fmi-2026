package streams

import cats.effect.unsafe.IORuntime
import cats.effect.{IO, IOApp, Resource}
import cats.syntax.all.*
import com.comcast.ip4s.{ipv4, port}
import fs2.concurrent.{Channel, Topic}
import fs2.{Pipe, Stream, text}
import io.circe.Codec
import org.http4s.ember.server.EmberServerBuilder
import sttp.capabilities.fs2.Fs2Streams
import sttp.client4.httpclient.fs2.HttpClientFs2Backend
import sttp.client4.{UriContext, WebSocketStreamBackend}
import sttp.tapir.*
import sttp.tapir.client.sttp4.ws.WebSocketSttpClientInterpreter
import sttp.tapir.client.sttp4.ws.fs2.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.http4s.Http4sServerInterpreter

import scala.concurrent.duration.DurationInt

case class Message(fromUser: String, content: String) derives Schema, Codec

val chatWsEndpoint =
  endpoint
    .in("chat-ws")
    .out(webSocketBody[Message, CodecFormat.Json, Message, CodecFormat.Json](Fs2Streams[IO]))
    .get

object Fs207ChatServer extends IOApp.Simple:
  def chatWsLogic(chatTopic: Topic[IO, Message]) =
    chatWsEndpoint.serverLogicSuccess: _ =>
      val wsStream: Pipe[IO, Message, Message] = inputStream =>
        val publishMessageStream = inputStream.through(chatTopic.publish)
        val receiveMessageStream = chatTopic.subscribe(100)

        receiveMessageStream.mergeHaltBoth(publishMessageStream)

      wsStream.pure[IO]

  def chatServerApp =
    for
      chatTopic <- Resource.liftK(Topic[IO, Message])

      chatRoute = chatWsLogic(chatTopic)

      routes = Http4sServerInterpreter[IO]().toWebSocketRoutes(chatRoute)

      server <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpWebSocketApp(wsBuilder => routes(wsBuilder).orNotFound)
        .build
    yield server

  def run: IO[Unit] = chatServerApp.use(_ => IO.never)

class ChatClientApp(sttpBackend: WebSocketStreamBackend[IO, Fs2Streams[IO]]):
  private val interpreter = WebSocketSttpClientInterpreter()

  def startChat(userName: String): IO[fs2.Stream[IO, Message]] =
    interpreter
      .toClientThrowErrors(chatWsEndpoint, Some(uri"ws://localhost:8080"), sttpBackend)
      .apply(())
      .map: streamPipe =>
        fs2.io
          .stdinUtf8[IO](1024)
          .through(text.lines)
          .map(Message(userName, _))
          .through(streamPipe)

  def receiveMessage(userName: String, message: Message): IO[Unit] =
    IO.println(s"${message.fromUser} says: ${message.content}").whenA(message.fromUser != userName)

  def runChat(userName: String): IO[Unit] =
    startChat(userName).flatMap: chatStream =>
      chatStream
        .foreach(receiveMessage(userName, _))
        .compile
        .drain

  def chatClientAppLogic: IO[Unit] =
    for
      _ <- IO.print("Enter your name: ")
      name <- IO.readLine
      _ <- runChat(name)
    yield ()

object Fs207ChatClient extends IOApp.Simple:
  val chatClientApp =
    for sttpBackend <- HttpClientFs2Backend.resource[IO]()
    yield ChatClientApp(sttpBackend)

  def run: IO[Unit] =
    chatClientApp
      .use(app => app.chatClientAppLogic)
      .guarantee(IO.println("Thank you for chatting with us :)!"))

@main
def testChannel =
  Channel
    .unbounded[IO, String]
    .flatMap: channel =>
      val pub1 = Stream.repeatEval(IO("Hello")).evalMap(channel.send).metered(1.second)
      val pub2 = Stream.repeatEval(IO("World")).evalMap(channel.send).metered(2.seconds)
      val sub = channel.stream.evalMap(IO.println)
      Stream(pub1, pub2, sub).parJoinUnbounded.interruptAfter(6.seconds).compile.drain
    .unsafeRunSync()(using IORuntime.global)
