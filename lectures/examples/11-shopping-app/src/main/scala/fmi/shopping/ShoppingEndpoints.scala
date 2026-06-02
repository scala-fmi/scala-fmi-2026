package fmi.shopping

import fmi.inventory.NotEnoughStockAvailable
import fmi.http.{AuthenticationError, ShoppingAppEndpoints, jsonBodyTypedError}
import sttp.model.StatusCode.Conflict
import sttp.tapir.*
import sttp.tapir.json.circe.*

object ShoppingEndpoints:
  import ShoppingAppEndpoints.*

  val ordersBaseEndpoint = v1BaseEndpoint.in("orders").tag("Orders")

  val placeOrderEndpoint =
    ordersBaseEndpoint.secure
      .in(jsonBody[ShoppingCart])
      .out(jsonBody[Order])
      .errorOutVariant[NotEnoughStockAvailable | AuthenticationError](
        oneOfVariant(statusCode(Conflict).and(jsonBodyTypedError[NotEnoughStockAvailable]))
      )
