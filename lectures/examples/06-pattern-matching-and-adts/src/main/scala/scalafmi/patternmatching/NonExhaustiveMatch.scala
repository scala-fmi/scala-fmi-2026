//package scalafmi.patternmatching
//
//def describeDay(day: Int): String = day match
//  case 1 => "Monday"
//  case 2 => "Tuesday"
//  case 3 => "Wednesday"
//  case 4 => "Thursday"
//  case 5 => "Friday"
//
//sealed trait Shape
//case class Circle(r: Double) extends Shape
//case class Square(a: Double) extends Shape
//
//def area(s: Shape) = s match
//  case Circle(r) => math.Pi * r * r
////
//enum Color:
//  case Red, Green, Blue, Yellow, Orange, Purple
////
//def isPrimaryColor(color: Color): Boolean = color match
//  case Color.Red | Color.Blue | Color.Green => true
//
//@main def testNonExhaustiveMatch =
//  describeDay(7)
