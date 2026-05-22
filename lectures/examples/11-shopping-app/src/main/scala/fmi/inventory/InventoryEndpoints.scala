package fmi.inventory

import fmi.user.UserRole
import fmi.{AuthenticationError, ConflictDescription, ResourceNotFound, ShoppingAppEndpoints}
import sttp.model.StatusCode.{Conflict, NotFound}
import sttp.tapir.*
import sttp.tapir.json.circe.*

object InventoryEndpoints:
  import ShoppingAppEndpoints.*

  val productsBaseEndpoint = v1BaseEndpoint.in("products").tag("Products")
  val stockBaseEndpoint = v1BaseEndpoint.in("stock").tag("Stock")

  val getProductEndpoint = productsBaseEndpoint
    .in(path[ProductId]("product-id") / path[UserRole]("role"))
    .out(jsonBody[Product])
    .errorOut(statusCode(NotFound).and(jsonBody[ResourceNotFound]))
    .get

  val putProductEndpoint =
    productsBaseEndpoint
      .secure(UserRole.Admin)
      .in(jsonBody[Product])
      .post

  val getAllStockEndpoint = stockBaseEndpoint
    .out(jsonBody[List[ProductStock]])
    .get

  val adjustStockEndpoint: Endpoint[String, InventoryAdjustment, ConflictDescription | AuthenticationError, Unit, Any] =
    stockBaseEndpoint
      .secure(UserRole.Admin)
      .in(jsonBody[InventoryAdjustment])
      .errorOutVariant(
        oneOfVariant(statusCode(Conflict).and(jsonBody[ConflictDescription]))
      )
      .post
