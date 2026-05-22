package fmi.shopping

import cats.effect.IO
import doobie.Meta
import fmi.inventory.{ProductId, ProductStockAdjustment}
import fmi.user.UserId
import io.circe.Codec
import sttp.tapir.{Schema, SchemaType}

import java.time.Instant

case class Order(orderId: OrderId, user: UserId, orderLines: List[OrderLine], placingTimestamp: Instant)
    derives Codec,
      Schema

opaque type OrderId = String

object OrderId:
  def apply(idString: String): OrderId = idString
  extension (orderId: OrderId) def asString: String = orderId

  def generate: IO[OrderId] = IO.randomUUID.map(uuid => OrderId(uuid.toString))

  given Codec[OrderId] = Codec.implied[String]
  given Schema[OrderId] = Schema(SchemaType.SString())
  given Meta[OrderId] = Meta[String]

case class OrderLine(product: ProductId, quantity: Int) derives Codec, Schema:
  def toProductStockAdjustment = ProductStockAdjustment(product, -quantity)
