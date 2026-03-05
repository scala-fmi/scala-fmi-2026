package scalafmi.mathematical

object MathUtils:
  def gcd(a: Int, b: Int): Int =
    def nonNegativeGcd(a: Int, b: Int): Int =
      if b == 0 then a else nonNegativeGcd(b, a % b)

    nonNegativeGcd(a.abs, b.abs)