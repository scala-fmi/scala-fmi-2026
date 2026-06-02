package fmi.shopping

import cats.effect.IO
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import fmi.infrastructure.db.DoobieDatabase.DbTransactor
import fmi.inventory.{NotEnoughStockAvailable, NotEnoughStockAvailableException, ProductStockRepository}

class OrderRepository(dbTransactor: DbTransactor)(productStockRepository: ProductStockRepository):
  def placeOrder(order: Order): IO[Either[NotEnoughStockAvailable, Order]] =
    val transaction =
      productStockRepository.applyInventoryAdjustmentAction(order.toInventoryAdjustment) *>
        storeOrder(order)

    transaction
      .transact(dbTransactor)
      .map(_.asRight)
      .recover:
        case NotEnoughStockAvailableException(product) => NotEnoughStockAvailable(product).asLeft

  def storeOrder(order: Order): ConnectionIO[Order] =
    val insertOrder = sql"""
      INSERT INTO "order" (id, user_id, placing_timestamp)
      VALUES (${order.orderId}, ${order.user}, ${order.placingTimestamp})
    """

    val insertOrderLine =
      val orderLinesInsert = """
        INSERT INTO order_line(order_id, sku, quantity)
        VALUES(?, ?, ?)
      """

      Update[(OrderId, OrderLine)](orderLinesInsert)
        .updateMany(order.orderLines.map(ol => (order.orderId, ol)))

    (insertOrder.update.run *> insertOrderLine).as(order)
