package fmi.inventory

import cats.effect.IO
import cats.syntax.all.*
import fmi.http.ResourceNotFound
import fmi.user.authentication.AuthenticationService
import sttp.tapir.server.ServerEndpoint

class InventoryController(
  productRepository: ProductRepository,
  productStockRepository: ProductStockRepository
)(
  authenticationService: AuthenticationService
):
  import authenticationService.authenticate

  val getProduct = InventoryEndpoints.getProductEndpoint.serverLogic: productId =>
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
          case e: NotEnoughStockAvailable => e.asLeft
    }

  val endpoints: List[ServerEndpoint[Any, IO]] = List(
    getProduct,
    putProduct,
    getAllStock,
    adjustStock
  )
