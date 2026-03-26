package scalafmi.patternmatching

case class Address(city: String, zip: String)
case class Person(name: String, age: Int, address: Address)

def check(person: Person): String = person match
  case Person(name, _, Address("Sofia", _)) => s"$name lives in Sofia"
  case Person(name, age, Address("Sofia", _)) if age > 18 => s"$name is an adult in Sofia"
  case _ => "No match"

@main def pesho = println:
  check(Person("Pesho", 24, Address("Varna", "9003")))
