package fmi.shopping

import cats.effect.IO
import fmi.inventory.NotEnoughStockAvailable
import fmi.user.UserId

class OrderService(
  orderRepository: OrderRepository
):
  // TODO: validate shopping cart has positive quantities
  def placeOrder(user: UserId, shoppingCart: ShoppingCart): IO[Either[NotEnoughStockAvailable, Order]] = for
    orderId <- OrderId.generate
    placingTimestamp <- IO.realTimeInstant

    order = Order(orderId, user, shoppingCart.orderLines, placingTimestamp)

    maybeOrder <- orderRepository.placeOrder(order)
  yield maybeOrder
