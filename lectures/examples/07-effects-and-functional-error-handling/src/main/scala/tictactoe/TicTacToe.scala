package tictactoe

enum Player:
  case X, O

  def nextPlayer: Player = this match
    case X => O
    case O => X

case class Cell private (x: Int, y: Int):
  override def toString = s"($x, $y)"

object Cell:
  val columnsRange: Range = 1 to 3
  val rowsRange: Range = 1 to 3

  val allCells: Set[Cell] =
    (
      for
        column <- columnsRange
        row <- rowsRange
      yield Cell(column, row)
    ).toSet

  def applyOption(x: Int, y: Int): Option[Cell] =
    if columnsRange.contains(x) && rowsRange.contains(y) then Some(Cell(x, y))
    else None

  val columns: List[List[Cell]] = columnsRange.toList.map: column =>
    rowsRange.toList.map(y => Cell(column, y))

  val rows: List[List[Cell]] = rowsRange.toList.map: row =>
    columnsRange.toList.map(x => Cell(x, row))

  val diagonals: List[List[Cell]] = List(
    allCells.toList.filter { case Cell(x, y) => x == y },
    allCells.toList.filter { case Cell(x, y) => y == columnsRange.end - x + 1 }
  )

  val allWinningPositions: List[List[Cell]] = rows ++ columns ++ diagonals

case class Move(player: Player, targetCell: Cell)

sealed trait TicTacToeMoveError
case class PlayerNotInTurn(attemptedToMove: Player, currentPlayer: Player) extends TicTacToeMoveError
case class CellIsAlreadyTaken(cell: Cell, takenBy: Player) extends TicTacToeMoveError
case object GameFinished extends TicTacToeMoveError

enum TicTacToeOutcome:
  case Winner(player: Player)
  case Tie

case class TicTacToe(board: Map[Cell, Player], currentPlayer: Player):
  def winner: Option[Player] =
    val markedWinningPositions = Cell.allWinningPositions.map(_.map(board.get))

    List(Player.X, Player.O).find: player =>
      markedWinningPositions.exists(_.forall(_ == Some(player)))

  def isFull: Boolean = board.keySet == Cell.allCells
  def isTie: Boolean = isFull && winner.isEmpty
  def isFinished: Boolean = winner.isDefined || isTie

  def outcome: Option[TicTacToeOutcome] =
    if isFinished then
      Some:
        winner.map(TicTacToeOutcome.Winner.apply).getOrElse(TicTacToeOutcome.Tie)
    else None

  def makeMove(move: Move): Either[TicTacToeMoveError, TicTacToe] =
    if isFinished then Left(GameFinished)
    else if move.player != currentPlayer then Left(PlayerNotInTurn(move.player, currentPlayer))
    else
      board
        .get(move.targetCell)
        .map(occupiedBy => Left(CellIsAlreadyTaken(move.targetCell, occupiedBy)))
        .getOrElse(
          Right(
            TicTacToe(
              board + (move.targetCell -> move.player),
              currentPlayer.nextPlayer
            )
          )
        )

object TicTacToe:
  def initial: TicTacToe = TicTacToe(Map.empty, Player.X)
