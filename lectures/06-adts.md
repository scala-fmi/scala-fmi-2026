---
title: Pattern Matching (Съпоставяне на образци)
---

# Какво са ADTs?

::: incremental

* Начин за моделиране на данни чрез комбиниране на типове
* Основават се на математическата теория на множествата
* Позволяват ни да дефинираме точно какви стойности може да приема един тип
* Основен инструмент за постигане на "type safety" в Scala
* Документация: Типовете описват бизнес логиката по-добре от коментарите.

:::

# Видове ADTs

ADTs се изграждат чрез две основни концепции:

1.  **Sum Type (Тип "Или"):** Стойността е или от тип А, ИЛИ от тип B (напр. `sealed Trait` или `enum`)
2.  **Product Type (Тип "И"):** Стойността съдържа тип А И тип B (напр. `Case Class` или `Tuple`)
3.  **Hybrid ADTs:** Комбинация от горните две

---

# Product Types

* Стойностите се комбинират заедно.
* Името идва от това, че броят на възможните стойности е произведение от броя на стойностите на неговите компоненти.

```scala
case class Point(x: Int, y: Int) // Product от Int и Int
case class Person(name: String, age: Int) // Product от String и Int
```

---

# Product type complexity

::: incremental

* (Byte, Boolean)
  * Complexity: 256 * 2 = 512

* (Boolean, Unit)
  * Complexity: 2 * 1 = 2

* (String, Nothing)
  * Complexity: many * 0 = 0

:::

---

# Sum Types

* Типът може да бъде само една от изброените възможности.
* Името идва от това, че броят на възможните стойности е сума от всички подтипове

::: incremental

* Чрез sealed trait:

```scala
sealed trait Direction
case class North extends Direction
case class South extends Direction
case class East extends Direction
case class West extends Direction

// Променлива от тип Direction може да бъде САМО едно от тези състояния
val current: Direction = Direction.North
```
* Чрез enum:

```scala
enum Direction:
  case North, South, East, West
```

:::

---

# Sum type complexity

::: incremental

* Byte | Boolean
  * Complexity: 256 + 2 = 258

* Boolean | Unit
  * Complexity: 2 + 1 = 3

* Byte | Nothing
  * Complexity: 256 + 0 = 256

:::

---

# Functions and ADTs

::: incremental

```scala
  def f1(b: Boolean): Boolean
```
* Complexity: 4

```scala
  def f2(b: Option[Boolean]): Boolean
```

* Complexity: 8

Функциите имат експоненциална сложност

```scala
  def f3(b: Byte): Boolean
  def f4(b: Boolean): Byte
```

:::

---

# Защо експоненциална?

### Таблица на възможните функции за f2(Option[Boolean]): Boolean

Математическа сложност: 2^3 = 8 възможни уникални имплементации.

| Функция # | Резултат при `Some(true)` | Резултат при `Some(false)` | Резултат при `None` | Логическо описание |
| :--- | :---: | :---: | :---: | :--- |
| **1** | `true` | `true` | `true` | Постоянна стойност `true` |
| **2** | `false` | `false` | `false` | Постоянна стойност `false` |
| **3** | `true` | `false` | `true` | `opt.getOrElse(true)` |
| **4** | `true` | `false` | `false` | `opt.getOrElse(false)` |
| **5** | `false` | `true` | `true` | `!opt.getOrElse(false)` |
| **6** | `false` | `true` | `false` | `!opt.getOrElse(true)` |
| **7** | `true` | `true` | `false` | `opt.isDefined` |
| **8** | `false` | `false` | `true` | `opt.isEmpty` |

---

# Hybrid Types

* Мощен начин за моделиране на бизнес логика.
* Използваме enum или sealed trait, където отделните случаи могат да бъдат case class (Product types).

```scala
sealed trait Shape
case class Circle(radius: Double) extends Shape
case class Rectangle(width: Double, height: Double) extends Shape
case object Point extends Shape // "Сума" от различни "Произведения"
```

```scala
enum Shape:
  case Circle(radius: Double) extends Shape
  case Rectangle(width: Double, height: Double) extends Shape
  case Point extends Shape // "Сума" от различни "Произведения"
```

---

# Защо бихме използвали sealed trait?

* Case Overriding

```scala
sealed trait Status:
  def isVisible: Boolean

case object Active extends Status:
  override def isVisible = true

case object Deleted extends Status:
  override def isVisible = false
```

* Composition

```scala
sealed trait Error
  sealed trait SystemError extends Error
    case object NetworkError extends SystemError
    case object Timeout      extends SystemError
  sealed trait DataError extends Error
    case object ValidationError extends DataError
```

* Type Inference and Type identity

```scala
enum Result:
  case Success(data: String)
  case Failure

// You cannot easily use 'Success' as a type here 
// because it is a value of the type 'Result'.
def process(success: Result.Success) = println(success.data)

// The compiler infers 'Result', losing the 'Success' specific info
val x = Result.Success("Done")
//will not compile
x.data
```

---

# Make illegal states unrepresentable

## Моделиране чрез SUM types

::: incremental

```scala
case class Card(cardType: CardType, monthlyLimit: double)

enum CardType:
  case Debit, Credit
```

```scala
sealed trait Card
  case class CreditCard(monthlyLimit: double)
  case object DebitCard
```
:::

---

# Още примери

```scala
enum WhereaboutsType:
  case AtLocation, InContainer, InMover

case class Whereabouts(id: Id, tpe: WhereaboutsType, position: Option[Position])
```

```scala
sealed trait Whereabouts
case class AtLocation(locationId: LocationId) extends Whereabouts
case class InContainer(parentContainer: ContainerId, position: Position) extends Whereabouts
case class InMover(mover: MoverId) extends WhereAbouts
```

```scala
case class Route(started: Boolean, finished: Boolean)
```

```scala
enum RouteStatus
  case Created, Started, Finised extends RouteStatus

case class Route(status: RouteStatus)
```

---

# Smart constructors

```scala
case class private MonthlyLimit (value: Double)
object MonthlyLimit:
  def apply(value: Double): Option[MonthlyLimit] = value match 
    case d if d <= 0d => None
    case d if d > 10000d => Some(new MonthlyLimit(10000d))
    case _ => Some(new MonthlyLimit(value))

sealed trait Card
  case class CreditCard(monthlyLimit: MonthlyLimit)
  case object DebitCard
```

---

# Ползи от този подход

::: incremental

* По-малко тестове: Не е нужно да тествате сценарии, които са невъзможни за достигане
* Безопасен рефакторинг: Ако променим структурата, компилаторът показва грешките:
```scala
sealed trait Card
  case class CreditCard(monthlyLimit: double)
  case object DebitCard
  case object VirtualCard

def sendCard(card: Card): Unit = card match 
  case CreditCard(monthlyLimit) => ???
  case DebitCard => ???
  //inexhaustive match
```
* Без defensive programming (`if (x != null)` или `if (isInvalid)` проверки)
* По лесен за разбиране код (self documenting)
* По малко бъгове

:::


