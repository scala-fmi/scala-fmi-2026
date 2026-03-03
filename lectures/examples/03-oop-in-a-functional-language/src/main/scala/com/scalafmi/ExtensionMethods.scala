package com.scalafmi

object NumberSyntax:
  extension (n: Int)
    def squared = n * n
    def **(exp: Double) = math.pow(n, exp)

  extension (n: Double)
    def squared = n * n
    def **(exp: Double) = math.pow(n, exp)

object ExtensionMethods:
  @main def extensions(): Unit =
    import NumberSyntax.*

    3.squared // 9
    2 ** 3 // 8.0

    3.14.squared
    2.71 ** 4
