package scalafmi.generalizedadts

enum Direction:
  case East, South, West, North

enum State:
  case Idle()
  case Moving()

enum Command[Before, After]:
  case Face(dir: Direction) extends Command[State.Idle, State.Idle]
  case Start extends Command[State.Idle, State.Moving]
  case Stop extends Command[State.Moving, State.Idle]
  case Chain[A, B, C](
    cmd1: Command[A, B],
    cmd2: Command[B, C]
  ) extends Command[A, C]

extension [A, B](cmd1: Command[A, B])
  def ~>[C](cmd2: Command[B, C]): Command[A, C] =
    Command.Chain(cmd1, cmd2)

def movingLabel(cmd: Command[State.Moving, ?]) = cmd match
  case Command.Stop => "stop"
  case _: Command.Chain[?, ?, ?] => "chain"
  // Unreachable case because the first type parameter of Command.Start is State.Idle
  // case Command.Start => "from-idle"
