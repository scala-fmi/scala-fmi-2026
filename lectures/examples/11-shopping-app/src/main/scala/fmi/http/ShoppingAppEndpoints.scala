package fmi.http

import fmi.user.UserRole
import io.circe.{Codec, *}
import sttp.model.StatusCode.{Forbidden, Unauthorized}
import sttp.tapir.Schema.SName
import sttp.tapir.generic.Configuration as TapirConfiguration
import sttp.tapir.{endpoint, *}

import scala.reflect.ClassTag

sealed transparent trait AuthenticationError
case class UnauthorizedAccess(message: String) extends AuthenticationError derives Codec.AsObject, Schema
case class ForbiddenResource(message: String) extends AuthenticationError derives Codec.AsObject, Schema

case class ResourceNotFound(message: String) derives Codec.AsObject, Schema

val userRoleKey = AttributeKey[UserRole]

object ShoppingAppEndpoints:
  val SessionCookie = "session"

  val baseEndpoint: PublicEndpoint[Unit, Unit, Unit, Any] = endpoint
  val v1BaseEndpoint: Endpoint[Unit, Unit, Unit, Unit, Any] = baseEndpoint.in("v1")

  extension [I, O, R](endpoint: PublicEndpoint[I, Unit, O, R])
    def secure: Endpoint[String, I, AuthenticationError, O, R] = endpoint.secure(None)
    def secure(userRole: UserRole): Endpoint[String, I, AuthenticationError, O, R] = endpoint.secure(Some(userRole))

    def secure(maybeRole: Option[UserRole]): Endpoint[String, I, AuthenticationError, O, R] =
      val securedEndpoint = endpoint
        .securityIn(cookie[String](SessionCookie))
        .errorOut(
          oneOf[AuthenticationError](
            oneOfVariant(
              statusCode(Unauthorized).and(jsonBodyTypedError[UnauthorizedAccess].description("unauthorized access"))
            ),
            oneOfVariant(statusCode(Forbidden).and(jsonBodyTypedError[ForbiddenResource].description("resource forbidden")))
          )
        )

      maybeRole
        .map: role =>
          securedEndpoint.attribute(userRoleKey, role)
        .getOrElse(securedEndpoint)
