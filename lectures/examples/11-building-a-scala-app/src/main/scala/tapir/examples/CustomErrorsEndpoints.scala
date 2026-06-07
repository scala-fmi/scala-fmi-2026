package tapir.examples

import io.circe
import io.circe.*
import io.circe.Decoder.Result
import io.circe.derivation.ConfiguredCodec
import sttp.model.StatusCode.{BadRequest, NotFound}
import sttp.tapir.Schema.SName
import sttp.tapir.generic.Configuration as TapirConfiguration
import sttp.tapir.json.circe.*
import sttp.tapir.{statusCode, Codec as _, *}

import scala.reflect.ClassTag

//val securityHeadersInput: EndpointInput[SecurityHeaders] =
//  header[RequestId]("RequestId")
//    .and(auth.bearer[Secret[String]]())
//    .and(header[Option[ClientId]]("OriginClientId"))
//    .and(header[Option[UserId]]("OriginUserId"))
//    .and(header[Option[DeviceId]]("OriginDeviceId"))
//    .and(header[Option[OperationId]]("operationid"))
//    .mapTo[SecurityHeaders]

def jsonBodyTypedError[T: {Encoder.AsObject as encoder, Decoder, Schema as tSchema, ClassTag as classTag}](
  using tc: TapirConfiguration
): EndpointIO.Body[String, T] =
  val typeClassName = classTag.runtimeClass.getSimpleName
  val typeName = tc.toDiscriminatorValue(SName(typeClassName))
  val typeDiscriminator = tc.discriminator.getOrElse("type")

  val extendedEncoder = new Encoder.AsObject[T]:
    def encodeObject(a: T): JsonObject =
      encoder
        .encodeObject(a)
        .add(typeDiscriminator, Json.fromString(typeName))

  val extendedDecoder = new Decoder[T]:
    def apply(c: HCursor): Result[T] =
      for
        _ <- c
          .downField(typeDiscriminator)
          .as[String]
          .filterOrElse(
            _ == typeName,
            DecodingFailure(s"'$typeDiscriminator' must be $typeName", c.downField(typeDiscriminator).history)
          )
        value <- c.as[T]
      yield value
    end apply

  val extendedSchemaType = tSchema.schemaType match
    case product: SchemaType.SProduct[T] =>
      product.copy(fields =
        product.fields ++ List(
          SchemaType.SProductField(
            FieldName(typeDiscriminator, typeDiscriminator),
            Schema.schemaForString.encodedDiscriminatorValue(typeName),
            _ => Some(typeName)
          )
        )
      )
    case _ => throw new RuntimeException("Schema must be SProduct")
  val extendedSchema = tSchema.copy(schemaType = extendedSchemaType)

  jsonBody[T](using extendedEncoder, extendedDecoder, extendedSchema)
end jsonBodyTypedError

trait Error
case class CustomError(message: String, name: String) extends Error
case class CustomError2(message: String, name: String) extends Error
case class CustomError3(message: String, name: String) extends Error

object CustomErrorsEndpoints:
  val baseEndpoint: PublicEndpoint[Unit, Unit, Unit, Any] = endpoint
  val v1BaseEndpoint: Endpoint[Unit, Unit, Unit, Unit, Any] = baseEndpoint.in("v1")

  case class CustomError(message: String, name: String) derives Codec.AsObject, Schema
  case class CustomError2(message: String, name: String) derives Codec.AsObject, Schema

  given TapirConfiguration = TapirConfiguration.default.withDiscriminator("type").withFullKebabCaseDiscriminatorValues

  sealed trait AAAA derives Schema
  case object BBBB extends AAAA
  case class CCCC(name: String) extends AAAA

  case class Address(addr: String)

  object Address:
    given circe.Codec[Address]:
      def apply(c: HCursor): Result[Address] = c.as[String].map(Address.apply)

      def apply(a: Address): Json = Json.fromString(a.addr)

    given Schema[Address] = Schema.string[Address]
  case class Person(name: String, age: Int, address: Address) derives circe.Codec, Schema

  val peopleListing: Endpoint[Unit, Unit, CustomError | CustomError2, List[Person], Any] =
    endpoint.get
      .in("people")
      .out(jsonBody[List[Person]])
      .errorOut(
        oneOf[CustomError | CustomError2](
          oneOfVariant(
            statusCode(BadRequest).and(jsonBody[CustomError].example(CustomError("Custom Error", "Custom Name")))
          ),
          oneOfVariant(
            statusCode(NotFound).and(jsonBody[CustomError2].example(CustomError2("Custom Error 2", "Custom Name 2")))
          )
        )
      )

  import cats.syntax.all.*

  peopleListing.serverLogicPure: _ =>
    CustomError2("a", "b").asLeft
