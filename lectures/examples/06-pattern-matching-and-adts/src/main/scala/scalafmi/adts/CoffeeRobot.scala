package scalafmi

// TODO: Refactor Coffee Robot to eliminate illegal states
// We first need to answer the question: what are the illegal states we want to eliminate and why?

// no need to implement prepareIrishCoffee or methods for other coffee types

type Milk = String
type Gelato = String
type Cream = String
type Whiskey = String

sealed trait CoffeeOrder
case class Cappuccino(milk: Milk)  extends CoffeeOrder
case object Espresso extends CoffeeOrder
case class Latte(milk: Milk) extends CoffeeOrder
case class Affogato(gelato: Gelato) extends CoffeeOrder
case class IrishCoffee private(whiskey: Whiskey, cream: Cream) extends CoffeeOrder
  
object IrishCoffee:
  def apply(whiskey: Whiskey, cream: Cream): Option[IrishCoffee] =
    if whiskey.length > 5 then Some(new IrishCoffee(whiskey, cream))
    else None

private def prepareIrishCoffee(cream: Cream, whiskey: Whiskey): Unit = ???

def prepareCoffeeOrder(order: CoffeeOrder): Unit =
  order match
    case IrishCoffee(whiskey, cream) =>
      prepareIrishCoffee(cream, whiskey)
    case _ => ()



@main def test = {
  val coffeOrder = IrishCoffee("chivas regal", "heavy cream");

  coffeOrder.map(ic => prepareCoffeeOrder(ic))
}
