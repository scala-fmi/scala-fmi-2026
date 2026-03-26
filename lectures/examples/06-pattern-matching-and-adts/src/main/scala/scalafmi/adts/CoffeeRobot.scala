package scalafmi

// TODO: Refactor Coffee Robot to eliminate illegal states
// We first need to answer the question: what are the illegal states we want to eliminate and why?

// no need to implement prepareIrishCoffee or methods for other coffee types

class CoffeeRobot:
  type Milk = String
  type Gelato = String
  type Cream = String
  type Whiskey = String

  enum CoffeeType:
    case Cappuccino, Espresso, Latte, Affogato, IrishCoffee

  case class CoffeeOrder(
    coffeeType: CoffeeType,
    milk: Option[Milk],
    gelato: Option[Gelato],
    cream: Option[Cream],
    whiskey: Option[Whiskey]
  )

  private def prepareIrishCoffee(cream: Cream, whiskey: Whiskey): Unit = ???

  def prepareCoffeeOrder(order: CoffeeOrder): Unit =
    order.coffeeType match
      case CoffeeType.IrishCoffee =>
        //Unreachable state but check just in case
        if order.cream.isEmpty then throw IllegalStateException("No cream")
        if order.whiskey.isEmpty then throw IllegalStateException("No whiskey")

        prepareIrishCoffee(order.cream.get, order.whiskey.get)
      case _ => ()



