package lecture.tictactoe

enum Player:
  case X, O

case class Coordinate(x: Int, y: Int)

case class Board(cells: Map[Coordinate, Player])

object Board:
  val EmptyBoard = Board(Map.empty)

case class Move(player: Player, coordinate: Coordinate)

case class TicTacToe(board: Board, currentPlayer: Player):
  def makeMove(move: Move): TicTacToe = ???
