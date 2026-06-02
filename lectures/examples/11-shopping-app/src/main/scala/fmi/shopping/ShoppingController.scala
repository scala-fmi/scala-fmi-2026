package fmi.shopping

import cats.effect.IO
import fmi.user.authentication.AuthenticationService
import sttp.tapir.server.ServerEndpoint

class ShoppingController(orderService: OrderService)(authenticationService: AuthenticationService):
  import authenticationService.authenticate

  val placeOrders = ShoppingEndpoints.placeOrderEndpoint.authenticate
    .serverLogic { user => shoppingCart =>
      orderService.placeOrder(user.id, shoppingCart)
    }

  val endpoints: List[ServerEndpoint[Any, IO]] = List(placeOrders)
