package streams.tictactoe

import cats.effect.std.Queue
import cats.effect.{IO, IOApp, Resource}
import fs2.{Pipe, Stream}
import streams.tictactoe.TicTacToeWebSocketData.*
import sttp.capabilities.fs2.Fs2Streams
import sttp.client4.WebSocketStreamBackend
import sttp.client4.httpclient.fs2.HttpClientFs2Backend
import sttp.client4.quick.UriContext
import sttp.tapir.client.sttp4.ws.WebSocketSttpClientInterpreter
import sttp.tapir.client.sttp4.ws.fs2.*

case class GameDetails(roomId: String, player: Player)

object TicTacToeClient extends IOApp.Simple:
  import ConsoleUtils.*

  type StreamingSttpBackend = WebSocketStreamBackend[IO, Fs2Streams[IO]]

  val enterGameDetails: IO[GameDetails] =
    for
      roomId <- prompt("Enter room id:")
      player <- TicTacToeConsole.promptForPlayer("Which player are you – X or O:")
    yield GameDetails(roomId, player)

  def playTicTacToe(
    backend: StreamingSttpBackend
  )(
    gameDetails: GameDetails,
    gameDataQueue: Queue[IO, TicTacToeWebSocketData]
  ): Stream[IO, Unit] =
    Stream
      .eval(connectToGame(backend)(gameDetails.roomId))
      .flatMap: pipe =>
        Stream
          .fromQueueUnterminated(gameDataQueue)
          .evalMapFilter:
            case NewState(board) =>
              TicTacToeConsole.printBoard(board) >>
                TicTacToeConsole.actOnBoard(gameDetails.player, board)
            case MoveError(moveError) =>
              IO.println(TicTacToeConsole.ticTacToeMoveErrorDescription(moveError)) >>
                TicTacToeConsole.enterMove(gameDetails.player).map(Option.apply)
          .through(pipe)
          .evalMap(data => gameDataQueue.offer(data))
  end playTicTacToe

  def connectToGame(backend: StreamingSttpBackend)(roomId: String): IO[Pipe[IO, Move, TicTacToeWebSocketData]] =
    WebSocketSttpClientInterpreter()
      .toClientThrowErrors(TicTacToeEndpoints.ticTacToeWsEndpoint, Some(uri"ws://localhost:8080"), backend)
      .apply(roomId)

  def ticTacToeGame(backend: StreamingSttpBackend): IO[Unit] =
    for
      gameDetails <- enterGameDetails
      gameDataQueue <- Queue.unbounded[IO, TicTacToeWebSocketData]
      _ <- playTicTacToe(backend)(gameDetails, gameDataQueue).compile.drain
    yield ()

  def run: IO[Unit] =
    val app =
      for
        sttpBackend <- HttpClientFs2Backend.resource[IO]()
        result <- Resource.liftK(ticTacToeGame(sttpBackend))
      yield result

    app.use(_ => IO.unit)
