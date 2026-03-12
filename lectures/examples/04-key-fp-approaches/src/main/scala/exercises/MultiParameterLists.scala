package exercises

@main def multiParameterListsExamples =
  def min[T](compare: (T, T) => Int)(a: T, b: T) =
    if compare(a, b) <= 0 then a
    else b

  def compareByAbsoluteValue(a: Int, b: Int) = a.abs - b.abs

  val minByAbsoluteValue = min(compareByAbsoluteValue)

  println {
    minByAbsoluteValue(10, -20)
  }

  println {
    List(-10, -30, 2, 8).reduce(minByAbsoluteValue)
  }
end multiParameterListsExamples
