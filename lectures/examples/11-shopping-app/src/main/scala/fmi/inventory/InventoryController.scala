package fmi.inventory
import cats.effect.IO
import cats.syntax.all.*
import fmi.user.authentication.AuthenticationService
import fmi.{ConflictDescription, ResourceNotFound}
import sttp.tapir.server.ServerEndpoint

class InventoryController(
  productRepository: ProductRepository,
  productStockRepository: ProductStockRepository
)(
  authenticationService: AuthenticationService
):
  import authenticationService.authenticate

  val getProduct = InventoryEndpoints.getProductEndpoint.serverLogic: (productId, _) =>
    productRepository
      .retrieveProduct(productId)
      .map(_.toRight(ResourceNotFound(s"Product $productId was not found")))

  val putProduct = InventoryEndpoints.putProductEndpoint.authenticate
    .serverLogicSuccess(user => productRepository.addProduct)

  val getAllStock = InventoryEndpoints.getAllStockEndpoint.serverLogicSuccess: _ =>
    productStockRepository.retrieveAllAvailableStock

  val adjustStock = InventoryEndpoints.adjustStockEndpoint.authenticate
    .serverLogic { user => adjustment =>
      productStockRepository
        .applyInventoryAdjustment(adjustment)
        .map:
          case SuccessfulAdjustment => ().asRight
          case NotEnoughStockAvailable => ConflictDescription("Not enough stock available").asLeft
    }

  val endpoints: List[ServerEndpoint[Any, IO]] = List(
    getProduct,
    putProduct,
    getAllStock,
    adjustStock
  )
