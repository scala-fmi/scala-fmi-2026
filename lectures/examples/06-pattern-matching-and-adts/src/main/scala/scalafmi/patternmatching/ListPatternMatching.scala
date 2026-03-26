package scalafmi.patternmatching

def quickSort(xs: List[Int]): List[Int] = xs match
  case Nil => Nil
  case ::(x, rest) =>
    val (smaller, larger) = rest.partition(_ < x)
    quickSort(smaller) ::: (x :: quickSort(larger))

@main def testQuickSort = println:
  quickSort(List(5, 3, 4, 8, -2, 12))
