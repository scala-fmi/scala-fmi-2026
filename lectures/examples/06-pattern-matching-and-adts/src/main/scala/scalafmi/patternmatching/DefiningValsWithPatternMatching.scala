package scalafmi.patternmatching

import scala.util.control.NonFatal

@main def test =
  val (a, b, c) = (1, 2, 3)
  
  // This one cannot be verified at compile time
  // Requires @unchecked annotation to suppress the warning
  // But will crash on runtime
  val myList @ first :: second :: rest = List(1): @unchecked

  try 2 / 0
  catch
    case e: NullPointerException => println("Caught a NullPointerException")
    case e @ (_: UnsupportedOperationException | _: IllegalArgumentException) =>
      println(s"Caught one of two special exceptions: ${e.getMessage}")
    case e: Exception => println(s"Caught a general exception: ${e.getMessage}")
    case NonFatal(e) => println(s"Caught a non-fatal exception: ${e.getMessage}")
