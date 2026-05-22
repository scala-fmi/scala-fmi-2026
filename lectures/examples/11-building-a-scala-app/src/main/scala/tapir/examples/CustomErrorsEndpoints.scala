package tapir.examples

import io.circe
import io.circe.*
import io.circe.Decoder.Result
import io.circe.derivation.ConfiguredCodec
import sttp.model.StatusCode
import sttp.tapir.Schema.SName
import sttp.tapir.generic.Configuration as TapirConfiguration
import sttp.tapir.json.circe.*
import sttp.tapir.{Codec as _, *}

import scala.reflect.ClassTag

def jsonBodyError[T: {Encoder.AsObject as encoder, Decoder, Schema as tSchema, ClassTag as classTag}](
  statusCodeOutput: StatusCode,
  decorateOutput: EndpointIO.Body[String, T] => EndpointIO.Body[String, T] = identity[EndpointIO.Body[String, T]]
)(using tc: TapirConfiguration
): EndpointOutput[T] =
  val typeClassName = classTag.runtimeClass.getSimpleName
  val typeName = tc.toDiscriminatorValue(SName(typeClassName))

  val extendedEncoder = new Encoder.AsObject[T]:
    def encodeObject(a: T): JsonObject =
      encoder
        .encodeObject(a)
        .add("httpStatus", Json.fromInt(statusCodeOutput.code))
        .add("code", Json.fromString(typeName))

  val extendedDecoder = new Decoder[T]:
    def apply(c: HCursor): Result[T] =
      for
        _ <- c
          .downField("code")
          .as[String]
          .filterOrElse(_ == typeName, DecodingFailure(s"Code must be $typeName", c.downField("code").history))
        value <- c.as[T]
      yield value
    end apply

  val extendedSchemaType = tSchema.schemaType match
    case product: SchemaType.SProduct[T] =>
      product.copy(fields =
        product.fields ++ List(
          SchemaType.SProductField(
            FieldName("code", "code"),
            Schema.schemaForString.encodedDiscriminatorValue(typeName),
            _ => Some(typeName)
          ),
          SchemaType.SProductField(
            FieldName("httpStatus", "httpStatus"),
            Schema.schemaForInt.validate(Validator.enumeration(List(statusCodeOutput.code))),
            _ => Some(statusCodeOutput.code)
          )
        )
      )
    case _ => throw new RuntimeException("Schema must be SProduct")
  val extendedSchema = tSchema.copy(schemaType = extendedSchemaType)

  statusCode(statusCodeOutput).and(decorateOutput(jsonBody[T](using extendedEncoder, extendedDecoder, extendedSchema)))
end jsonBodyError

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

  val peopleListing =
    endpoint.get
      .in("people")
      .out(jsonBody[List[Person]])
      .errorOut(
        oneOf(
          oneOfVariant(
            jsonBodyError[CustomError](StatusCode.BadRequest, _.example(CustomError("Custom Error", "Custom Name")))
          ),
          oneOfVariant(
            jsonBodyError[CustomError2](
              StatusCode.NotFound,
              _.example(CustomError2("Custom Error 2", "Custom Name 2"))
            )
          )
          //          oneOfVariant(jsonBody[BadRequest](StatusCode.BadRequest))
        )
      )
