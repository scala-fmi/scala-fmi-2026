package scalafmi.patternmatching

@main def test =
  val myList @ first :: second :: rest = List(1)

  println(first)
  println(second)
  println(rest)
  println(myList)
