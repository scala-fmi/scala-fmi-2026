package fmi.inventory

import doobie.Meta
import io.circe.Codec
import sttp.tapir
import sttp.tapir.{CodecFormat, Schema, SchemaType, Validator}

opaque type ProductId = String

object ProductId:
  def apply(idString: String): ProductId = idString
  extension (productId: ProductId) def asString: String = productId

  given Codec[ProductId] = Codec.implied[String]
  given Schema[ProductId] = Schema(SchemaType.SString())
  given Meta[ProductId] = Meta[String]
  given Ordering[ProductId] = Ordering[String]

  given tapir.Codec[String, ProductId, CodecFormat.TextPlain] =
    tapir.Codec.string.validate(Validator.minLength(3)).map(ProductId.apply)(_.asString)

case class Product(id: ProductId, name: String, description: String, weightInGrams: Int) derives Codec, Schema
