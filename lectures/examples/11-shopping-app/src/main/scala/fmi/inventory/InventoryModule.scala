package fmi.inventory

import cats.effect.IO
import cats.effect.kernel.Resource
import fmi.infrastructure.db.DoobieDatabase.DbTransactor
import fmi.user.authentication.AuthenticationService
import sttp.tapir.server.ServerEndpoint

case class InventoryModule(
  productRepository: ProductRepository,
  productStockRepository: ProductStockRepository,
  endpoints: List[ServerEndpoint[Any, IO]]
)

object InventoryModule:
  def apply(dbTransactor: DbTransactor, authenticationService: AuthenticationService): Resource[IO, InventoryModule] =
    val productDao = new ProductRepository(dbTransactor)
    val productStockDao = new ProductStockRepository(dbTransactor)
    val inventoryController = new InventoryController(productDao, productStockDao)(authenticationService)

    Resource.pure(
      InventoryModule(
        productDao,
        productStockDao,
        inventoryController.endpoints
      )
    )
