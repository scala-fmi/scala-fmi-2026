package fmi.user

import fmi.user.authentication.AuthenticatedUser
import fmi.http.{ShoppingAppEndpoints, UnauthorizedAccess, jsonBodyTypedError}
import sttp.model.StatusCode.{BadRequest, Unauthorized}
import sttp.tapir.*
import sttp.tapir.json.circe.*

object UsersEndpoints:
  import ShoppingAppEndpoints.*

  val usersBaseEndpoint = v1BaseEndpoint.in("users").tag("Users")

  val registerUserEndpoint = usersBaseEndpoint
    .in(jsonBody[UserRegistrationForm])
    .errorOut(
      statusCode(BadRequest).and(jsonBody[RegistrationError])
    )
    .post

  val getAuthenticatedUserEndpoint = usersBaseEndpoint.secure
    .in("current")
    .out(jsonBody[AuthenticatedUser].example(AuthenticatedUser(UserId("Pammy"), UserRole.Admin)))
    .get

  val loginUserEndpoint = usersBaseEndpoint
    .in("login")
    .in(jsonBody[UserLogin])
    .out(setCookie(SessionCookie))
    .errorOut(
      statusCode(Unauthorized).and(jsonBodyTypedError[UnauthorizedAccess])
    )
    .post

  val logoutUserEndpoint = usersBaseEndpoint
    .in("current")
    .out(setCookie(SessionCookie))
    .delete
