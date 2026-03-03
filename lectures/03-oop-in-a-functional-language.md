# import клаузи

::: { .fragment }

```scala
import scala.util.Try // само типа Try

Try(10)
```

:::

::: { .fragment }

```scala
import scala.util.* // всичко от util пакета
// import scala.util._ Scala2 синтаксис. Все още работи в Scala3

Try(10)
Success(10)
```

:::

::: { .fragment }

```scala
import math.Math.{ gcd, Pi } // няколко неща от обекта Math

gcd(42, 18) * Pi
```

:::

# import клаузи

::: { .fragment }

```scala
import math.Math.* // всичко от oбекта Math

squared(11)
gcd(42, 10)
```

:::

::: { .fragment }

```scala
import scala.collection.immutable.Set
import scala.collection.mutable.{ Set as MutableSet } // преименуване
// import scala.collection.mutable.{ Set => MutableSet } // Scala2

Set(1, 2, 3)
MutableSet(4, 5, 6)
```

:::

::: { .fragment }

```conf
rewrite.scala3.convertToNewSyntax = true
```

:::

::: { .fragment }

```scala
import scala.collection.immutable.Set
import scala.collection.mutable // импорт на част от пътя

Set(1, 2, 3)
mutable.Set(4, 5, 6)
```

:::

# import клаузи

::: incremental

* Могат да са във всеки scope, не е нужно да са в началото на файла:
  
  ```scala
  class Rational(n: Int, d: Int):
    import Math.gcd
    
    gcd(n.abs, d.abs)
    // ...
  ```
* Автоматично във всеки файл се включват следните import-и:
  ```scala
  import java.lang.*
  import scala.*
  import scala.Predef.*
  ```

:::

# export клаузи { .scala3 }

Позволяват делегация:

```scala
object IntUtils:
  def twice(n: Int): Int = 2 * n
  def squared(n: Int): Int = n * n

object DoubleUtils:
  def twice(n: Double): Double = 2 * n
  def squared(n: Double): Double = n * n

object MathUtils:
  export IntUtils.*
  export DoubleUtils.*

MathUtils.twice(2) // 4
MathUtils.twice(2.0) // 4.0
```

::: incremental 

* export-натите имена стават членове на обекта
* синтактично е със същия формат като `import`

:::

# export клаузи { .scala3 }

```scala
class Scanner:
  def scan(image: Image): Page = ???
  def isOn: Boolean = ???

class Printer:
  def print(page: Page): Image = ???
  def isOn: Boolean = ???

class Copier:
  private val scanner = new Scanner
  private val printer = new Printer
  
  export scanner.scan
  export printer.print
  
  def isOn = scanner.isOn && printer.isOn

val copier = new Copier
val image = ???
val copiedImage = copier.print(copier.scan(image))

image == copiedImage // true, hopefully :D
```

# Value класове

::: incremental

```scala
def createAddressRegistration(personId: String, locationId: String) = ???
```

```scala
createAddressRegistration(locationId, personId) // compiles 😬
```

:::

# AnyVal класове

::: incremental

```scala
final case class PersonId(value: String) extends AnyVal
final case class LocationId(value: String) extends AnyVal

def createAddressRegistration(person: PersonId, location: LocationId) = ???
```

```scala
createAddressRegistration(PersonId("100"), LocationId("5")) // OK
createAddressRegistration(LocationId("5"), PersonId("100")) // won't compile
```

:::

# AnyVal класове

```scala
case class Meter(amount: Double) extends AnyVal:
  def +(m: Meter): Meter = Meter(amount + m.amount)
  def *(coefficient: Double): Meter = Meter(coefficient * amount)
  
  override def toString = s"$amount meters"
```

::: { .fragment }

```scala
case class Circle(radius: Meter):
  def circumference: Meter = radius * 2 * math.Pi

Circle(Meter(2)).circumference.toString // 12.566370614359172 meters
```

:::

::: incremental

* В повечето случаи не създават допълнителен обект, вместо това се репрезентират от типа, който обвиват
* носят повече type safety в някои ситуации
* обвитата стойност задължително трябва да е `val` в обиващия клас
* поради JVM ограничения не могат да обвият повече от едно поле

:::

# Opaque Types { .scala3 }

* Same safety goal, fewer JVM compromises, guaranteed allocation-free, and the abstraction boundary is stronger.

::: incremental

```scala
object domain:
  opaque type PersonId = String
  object PersonId:
    def apply(s: String): PersonId = s
    extension (id: PersonId) def value: String = id

  opaque type LocationId = String
  object LocationId:
    def apply(s: String): LocationId = s
    extension (id: LocationId) def value: String = id
```

```scala
createAddressRegistration(PersonId("100"), LocationId("5")) // OK
createAddressRegistration(LocationId("5"), PersonId("100")) // won't compile
```

* Under the hood it’s still a String at runtime (so it’s “zero allocation” in the way you actually care about).
* Outside the defining scope, PersonId is not treated as a String. You only expose what you choose (via extensions / methods).
* No “single-val constructor” limitation because it’s not a class.

:::

# Изброени типове { .scala3 }

```scala
enum WeekDay:
  case Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
```

::: { .fragment }

```scala
def isWorkingDay(day: WeekDay) = day != WeekDay.Saturday && day != WeekDay.Sunday
isWorkingDay(WeekDay.Wednesday) // true, :(
```

:::

::: { .fragment }

```scala
WeekDay.valueOf("Monday") // WeekDay.Monday

WeekDay.values // Array(Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday)
```

:::

# Типизиране -- съвместимост на типове

```scala
val a: A = new B

// кога тип B е съвместим с тип A?
```

::: incremental

* Номинално -- типове се проверяват за съвместимост по тяхното име (и по явна релация с други имена)
  - Аз съм бухал, защото са ми казали, че съм бухал
  - Аз като бухал съм птица, защото всички бухали са птици
  - "B наследява A"
* Структурно -- съвместимост на типове се определя по структурата на обекта (по неговото поведение)
  - Аз съм бухал, защото гукам като бухал и защото мога да летя
  - Аз като бухал съм птица, защото мога да летя
  - "B има същите методи (т.е. същата структура) като A"
  
:::

# Структурно типизиране в Scala

```scala
case class Eagle(name: String):
  def flyThrough(location: String): String =
    s"Hi, I am old $name and I am looking for food at $location."

case class Owl(age: Int):
  def flyThrough(location: String): String =
    s"Hi, I am a $age years old owl and I am flying through $location. Hoot, hoot!"
```

::: { .fragment }

```scala
def checkLocations(
  locations: List[String],
  bird: { def flyThrough(location: String): String }
): List[String] = 
  for
    location <- locations
  yield bird.flyThrough(location)

checkLocations(List("Sofia", "Varna"), Owl(7))
```

:::

# Структурно типизиране в Scala

```scala
case class Eagle(name: String):
  def flyThrough(location: String): String =
    s"Hi, I am old $name and I am looking for food at $location."

case class Owl(age: Int):
  def flyThrough(location: String): String =
    s"Hi, I am a $age years old owl and I am flying through $location. Hoot, hoot!"
```

```scala
type Bird = {
  def flyThrough(location: String): String
}

def checkLocations(locations: List[String], bird: Bird): List[String] =
  for
    location <- locations
  yield bird.flyThrough(location)

checkLocations(List("Sofia", "Varna"), Eagle("Henry"))
```

::: incremental

* Върху JVM се имплементира чрез reflection, поради което изисква:

```scala
import reflect.Selectable.reflectiveSelectable
```
* Ако не искаме да използваме reflection, можем да имплементираме `Selectable` интерфейса

:::

# Типова алгебра { .scala3 }

::: { .fragment }

Scala 3 добавя сечение (`&`) и обединение (`|`) на типове

:::

# Сечение на типове (`&`) { .scala3 }

```scala
trait LovingAnimal:
  def name: String
  def hug = s"A hug from $name"

case class Owl(name: String, age: Int):
  def flyThrough(location: String): String =
    s"Hi, I am a $age years old owl and I am flying through $location. Hoot, hoot!"

val lovelyOwl: Owl & LovingAnimal = new Owl("Oliver", 7) with LovingAnimal
lovelyOwl.hug // A hug from Oliver
lovelyOwl.flyThrough("Plovdiv") // Hi, I am a 7 years old owl and
                                // I am flying through Plovdiv. Hoot, hoot!
```

# Обединение на типове (`|`) { .scala3 }

```scala
def toInteger(value: String | Int | Double): Int = value match
  case n: Int => n
  case s: String => s.toInt
  case d: Double => d.toInt

toInteger("10") // 10
toInteger(10) // 10
toInteger(10.0) // 10
toInteger(List(10)) // не се компилира
```

# Обединение на типове (`|`) { .scala3 }

```scala
def toInteger(value: String | Int | Double): Int = value match
  case n: Int => n
  case s: String => s.toInt
```

::: { .fragment }

```
|def toInteger(value: String | Int | Double): Int = value match
|                                                   ^^^^^
|                                  match may not be exhaustive.
|
|                                  It would fail on pattern case: _: Double
```

:::

::: { .fragment }

Превърнете в грешка чрез<br />`-Xfatal-warnings`:

```scala
scalacOptions += "-Xfatal-warnings"
```

:::

# Обединение на типове (`|`) { .scala3 }

```scala
def registerUser(registrationForm: RegistrationForm): RegistrationFormError | User = ???
```

# The Expression Problem

::: {.fragment}

> The goal is to define a datatype by cases, where one can add new cases to the datatype and new functions over the datatype, without recompiling existing code, and while retaining static type safety (e.g., no casts).

:::

# The Expression Problem (алтернативно)

::: incremental

* Добавяне на нов тип без промяна на съществуващия код
* Добавяне на нова операция без промяна на съществуващия код

:::

# ООП подход

```scala
trait Shape:
  def area: Double

case class Circle(r: Double) extends Shape:
  def area: Double = math.Pi * r * r

case class Rectangle(a: Double, b: Double) extends Shape:
  def area: Double = a * b
```

# ФП подход

```scala
trait Shape
case class Circle(r: Double) extends Shape
case class Rectangle(a: Double, b: Double) extends Shape

def area(s: Shape): Double = s match
  case Circle(r) => math.Pi * r * r
  case Rectangle(a, b) => a * b
```

::: { .fragment }

`case` класовете могат да бъдат използвани в pattern matching

:::

# Добавяне на операция във ФП -- лесно

```scala
def circumference(s: Shape): Double = s match
  case Circle(r) => 2 * math.Pi * r
  case Rectangle(a, b) => 2 * (a + b)
```

# Добавяне на операция в ООП -- трудно, промяна на всички класове

```scala
trait Shape:
  def area: Double
  def circumference: Double

case class Circle(r: Double) extends Shape:
  def area: Double = math.Pi * r * r
  def circumference = 2 * math.Pi * r

case class Rectangle(a: Double, b: Double) extends Shape:
  def area: Double = a * b
  def circumference = 2 * (a + b)
```

# Добавяне на тип в ООП -- лесно

```scala
case class Square(a: Double) extends Shape:
  def area: Double = a * a
  def circumference: Double = 4 * a
```

# Добавяне на тип във ФП -- трудно

```scala
case class Square(a: Double) extends Shape

def area(s: Shape): Double = s match
  case Circle(r) => math.Pi * r * r
  case Rectangle(a, b) => a * b
  case Square(a) => a * a

def circumference(s: Shape): Double = s match
  case Circle(r) => 2 * math.Pi * r
  case Rectangle(a, b) => 2 * (a + b)
  case Square(a) => 4 * a
```

# The Expression Problem

::: incremental

* Всеки език е добре да предоставя изразни средства и за двата проблема
* ООП подходът е подходящ за типове с предварително неизвестен брой случаи и малко основни операции
* Функционалният подход е подходящ за типове с предварително фиксирани случаи

:::

# Extension Methods

::: incremental

* Добавяне на методи към съществуващи типове
* Само в текущия scope

:::

# Extension Methods { .scala3 }

```scala
extension (n: Int)
  def squared = n * n
  def **(exp: Double) = math.pow(n, exp)

3.squared // 9
2 ** 3 // 8.0
```

# Extension Methods { .scala3 }

Могат да бъдат overload-вани,<br />import-ват се по името на метода:

```scala
// file NumberExtensions.scala
package scalafmi.numberextensions

extension (n: Int)
  def squared = n * n
  def **(exp: Double) = math.pow(n, exp)

extension (n: Double)
  def squared = n * n
  def **(exp: Double) = math.pow(n, exp)
```

```scala
// file Demo.scala
import scalafmi.numberextensions.{ squared, ** }

3.squared // 9
2 ** 3 // 8.0

3.14.squared
2.71 ** 4
```

# Extension Methods { .scala3 }

```scala
extension (xs: List[Double])
  def avg = xs.sum / xs.size

List(1.0, 2.0, 3.0).avg // 2.0
List("a", "b", "c").avg // грешка, value avg is not a member of List[String]
```

::: { .fragment }

```scala
extension [A](xs: List[A])
  def second = xs.tail.head

List(1.0, 2.0, 3.0).second // 2.0
List("a", "b", "c").second // b
```
:::

# Търсене на extension методи –<br />точно като при implicits

1. В текущия scope (чрез текущ или външен блок или чрез import)
2. В продружаващия обект на който и да е от участващите типове

# Extension методи в придружаващ обект

```scala
object Rational:
  extension (xs: List[Rational])
    def total: Rational =
      if xs.isEmpty then 0
      else xs.head + xs.tail.total
      
    def avg: Rational = xs.total / xs.size
```

# Extension Methods в Scala 2

::: incremental

* Scala 2 също позволява добавяне на методи
* Използва се механизма за implicit конверсия
* Все още се среща масово в библиотеките за Scala<br />(независимо от версията)

:::

# Extension Methods чрез implicit<br />(стар подход от Scala 2)

```scala
class EnrichedInt(val n: Int) extends AnyVal:
  def squared = n * n
  def **(exp: Double) = math.pow(n, exp)

implicit def intToEnrichedInt(n: Int) = new EnrichedInt(n)

3.squared // 9
2 ** 3 // 8.0
```

# Extension Methods чрез implicit<br />(стар подход от Scala 2)

```scala
implicit class EnrichedInt(val n: Int) extends AnyVal:
  def squared = n * n
  def **(exp: Double) = math.pow(n, exp)

3.squared // 9
2 ** 3 // 8.0
```

::: { .fragment }

Тук не е нужен `import scala.language.implicitConversions`

:::

# Примери от стандартната библиотека

```scala
1 -> "One" // (1, "One"), -> се добавя към всички типове

// extension methods се използва за добавяне на методите за колекции върху String
"abcdef".take(2) // ab

import scala.concurrent.duration.DurationInt
5.seconds // scala.concurrent.duration.FiniteDuration = 5 seconds
```

# ООП дизайн?

# ООП дизайн -- скрити домейн обекти

```scala
def buyTea(cc: CreditCard, paymentService: PaymentService): Tea =
  val teaCup = new Tea(...)
  paymentService.charge(cc, teaCup.price)
  teaCup
```

::: { .fragment }

```scala
case class Charge(cc: CreditCard, amount: Double)

def buyTea(cc: CreditCard): (Tea, Charge) =
  val teaCup = new Tea(...)
  (teaCup, Charge(cc, teatCup.price)
```

:::

::: { .fragment }

Отлагане на страничния ефект =>

::: incremental

* скрити домейн концепции изплуват на яве (`Charge` обект)
* моделираме дейности като данни
* които допълнително можем да трансформираме функционално
  - купуване на n кафета и събиране на Charge-ове
  - анализ на Charge-ове от различни потребители
* по-добра тестваемост

:::

:::

# [Таблица на типовите елементи в Scala](https://github.com/scala-fmi/scala-fmi-2024/blob/main/resources/type-elements-in-scala.md)

# Въпроси :)?

