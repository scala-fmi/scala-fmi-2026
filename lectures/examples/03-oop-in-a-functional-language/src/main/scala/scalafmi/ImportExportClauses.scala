package scalafmi

object IntUtils:
  def twice(n: Int): Int = 2 * n
  def squared(n: Int): Int = n * n

object DoubleUtils:
  def twice(n: Double): Double = 2 * n
  def squared(n: Double): Double = n * n

object MathUtils:
  export IntUtils.*
  export DoubleUtils.*

// @main def main(): Unit =
