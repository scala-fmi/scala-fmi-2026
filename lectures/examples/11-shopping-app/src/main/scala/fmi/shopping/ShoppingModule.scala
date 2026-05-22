package fmi.shopping

import cats.effect.IO
import cats.effect.kernel.Resource
import fmi.infrastructure.db.DoobieDatabase.DbTransactor
import fmi.inventory.ProductStockRepository
import fmi.user.authentication.AuthenticationService
import sttp.tapir.server.ServerEndpoint

case class ShoppingModule(
  orderRepository: OrderRepository,
  orderService: OrderService,
  endpoints: List[ServerEndpoint[Any, IO]]
)

object ShoppingModule:
  def apply(
    dbTransactor: DbTransactor,
    authenticationService: AuthenticationService,
    productStockRepository: ProductStockRepository
  ): Resource[IO, ShoppingModule] =
    val orderDao = new OrderRepository(dbTransactor)
    val orderService = new OrderService(dbTransactor)(productStockRepository, orderDao)
    val shippingController = new ShoppingController(orderService)(authenticationService)

    Resource.pure(
      ShoppingModule(orderDao, orderService, shippingController.endpoints)
    )
