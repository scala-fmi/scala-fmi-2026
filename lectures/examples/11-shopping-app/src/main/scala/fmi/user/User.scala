package fmi.user

import doobie.Meta
import io.circe.derivation.{Configuration, ConfiguredEnumCodec}
import fmi.utils.DerivationConfiguration.given Configuration
import io.circe.{Codec, Decoder, Encoder}
import sttp.tapir
import sttp.tapir.{CodecFormat, Schema, SchemaType}

case class User(
  id: UserId,
  passwordHash: String,
  role: UserRole,
  name: String,
  age: Option[Int]
) derives Codec,
      Schema

opaque type UserId = String
object UserId:
  def apply(email: String): UserId = email
  extension (id: UserId) def email: String = id

  given Codec[UserId] = Codec.implied[String]
  given Schema[UserId] = Schema(SchemaType.SString())
  given Meta[UserId] = Meta[String]

enum UserRole derives ConfiguredEnumCodec:
  case Admin, NormalUser

object UserRole:
  given Schema[UserRole] = Schema.derivedEnumeration()

  given tapir.Codec[String, UserRole, CodecFormat.TextPlain] =
    tapir.Codec.derivedEnumeration[String, UserRole].defaultStringBased
