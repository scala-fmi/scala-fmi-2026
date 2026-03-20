package scalafmi

trait Fruit:
  def color: String
  def size: Int

case class Apple(color: String, size: Int) extends Fruit
case class Orange(sort: String, size: Int) extends Fruit:
  def color: String = "orange"

def questions =
  val apples: List[Apple] = List(Apple("red", 2), Apple("green", 5), Apple("yellow", 3))
  // will this compile?
  val fruits: List[Fruit] = apples

end questions

def invariant =
  import scala.collection.mutable.ListBuffer

  val apples: ListBuffer[Apple] = ListBuffer(Apple("red", 2), Apple("green", 5), Apple("yellow", 3))
  val fruits: ListBuffer[Fruit] = ListBuffer.from(apples)

  fruits += Orange("navel", 8)
end invariant

def covariant =
  val apples: List[Apple] = List(Apple("red", 2), Apple("green", 5), Apple("yellow", 3))
  val fruits: List[Fruit] = apples

  // sealed trait List[+A]:
  //  def prepended[AA >: A](elem: AA): List[AA] = elem :: this
  val moreFruits: List[Fruit] = fruits.prepended(Orange("navel", 8))
  val evenMoreFruits: List[Fruit] = apples.prepended(Orange("navel", 8))
end covariant

// Contravariant

trait Consumer[-A]:
  def consume(a: A): String

object FruitConsumer extends Consumer[Fruit]:
  def consume(fruit: Fruit): String =
    s"Nom, nom, nom, I love all kinds of fruits. This ${fruit.color} fruit is delicious!!!"

object OrangeConsumer extends Consumer[Orange]:
  def consume(orange: Orange): String =
    s"Nom, nom, nom, I absolutely love oranges. This ${orange.sort} sort of oranges is so tasty!!!"

def contravarianExample =
  def dineFruits(fruits: List[Fruit])(fruitLover: Consumer[Fruit]): List[String] =
    fruits.map(fruitLover.consume)

  val fruits: List[Fruit] = List(Orange("navel", 3), Apple("red", 10))

  dineFruits(fruits)(FruitConsumer)

  // List(
  //   Nom, nom, nom, I love all kinds of fruits. This orange fruit is delicious!!!,
  //   Nom, nom, nom, I love all kinds of fruits. This red fruit is delicious!!!
  // )

  // Does not compile:
  // dineFruits(fruits)(OrangeConsumer)

  def dineOranges(oranges: List[Orange])(orangeLover: Consumer[Orange]): List[String] =
    oranges.map(orangeLover.consume)

  val oranges: List[Orange] = List(Orange("navel", 6), Orange("bergamot", 2))

  dineOranges(oranges)(FruitConsumer)
  // List(
  //   Nom, nom, nom, I love all kinds of fruits. This orange fruit is delicious!!!,
  //   Nom, nom, nom, I love all kinds of fruits. This orange fruit is delicious!!!
  // )

  dineOranges(oranges)(OrangeConsumer)
  // List(
  //   Nom, nom, nom, I absolutely love oranges. This navel sort of oranges is so tasty!!!,
  //   Nom, nom, nom, I absolutely love oranges. This bergamot sort of oranges is so tasty!!!
  // )
end contravarianExample

// Functions

trait Function2[-T1, -T2, +R]:
  def apply(v1: T1, v2: T2): R

trait Meal
case class Salad(description: String) extends Meal
case class Soup(description: String) extends Meal

def prepareOranges(oranges: List[Orange])(cook: Orange => Meal): List[Meal] = oranges.map(cook)

def functionsExamples =
  val fruitSaladCook: Fruit => Salad = fruit => Salad(s"Fruit salad with ${fruit.color} color")

  val oranges: List[Orange] = List(Orange("navel", 6), Orange("bergamot", 2))
  prepareOranges(oranges)(fruitSaladCook)

  // Fruit is a supertype of Orange
  // Salad is a subtype of Meal
end functionsExamples
