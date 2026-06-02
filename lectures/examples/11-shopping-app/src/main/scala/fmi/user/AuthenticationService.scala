package fmi.user.authentication

import cats.data.EitherT
import cats.effect.IO
import cats.syntax.all.*
import fmi.http.{AuthenticationError, ForbiddenResource, UnauthorizedAccess, userRoleKey}
import fmi.infrastructure.TokenSignatureService
import fmi.user.{UserId, UserRole, UsersRepository}
import io.circe.Codec
import sttp.model.headers.CookieValueWithMeta
import sttp.tapir.server.PartialServerEndpoint
import sttp.tapir.{Endpoint, Schema, endpoint}

import scala.concurrent.duration.DurationInt

case class Session(userId: UserId) derives Codec.AsObject, Schema

case class AuthenticatedUser(id: UserId, role: UserRole) derives Codec, Schema

class AuthenticationService(usersRepository: UsersRepository, tokenSignatureService: TokenSignatureService):
  val DefaultSessionExpirationDuration = 24.hours

  private def retrieveRole(endpoint: Endpoint[?, ?, ?, ?, ?]): Option[UserRole] =
    endpoint.attribute(userRoleKey)

  extension [I, E >: AuthenticationError, O, R](securityEndpoint: Endpoint[String, I, E, O, R])
    def authenticate: PartialServerEndpoint[String, AuthenticatedUser, I, E, O, R, IO] =
      securityEndpoint.serverSecurityLogic: sessionCookie =>
        (for
          userId <- EitherT(
            tokenSignatureService
              .validateAndRetrieve[Session](sessionCookie)
              .map(_.map(_.userId).toRight(UnauthorizedAccess("User must login")))
          )
          user <- EitherT(
            usersRepository
              .retrieveUser(userId)
              .map(_.toRight(UnauthorizedAccess("User must login")))
          )
          _ <- EitherT.fromEither[IO](
            if retrieveRole(endpoint).forall(_ == user.role) then ().asRight
            else ForbiddenResource(s"User $userId does not have rights to access this resource").asLeft
          )
        yield AuthenticatedUser(userId, user.role)).value

  def sessionWithUser(userId: UserId): IO[CookieValueWithMeta] =
    tokenSignatureService
      .sign(Session(userId), DefaultSessionExpirationDuration)
      .map(CookieValueWithMeta.unsafeApply(_, path = Some("/")))

  val clearSession: IO[CookieValueWithMeta] = CookieValueWithMeta.unsafeApply("", path = Some("/")).pure
