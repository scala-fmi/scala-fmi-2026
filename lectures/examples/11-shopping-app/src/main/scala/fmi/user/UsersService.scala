package fmi.user

import cats.data.{EitherT, NonEmptyChain}
import cats.effect.IO
import cats.syntax.all.*
import fmi.utils.DerivationConfiguration.given
import io.circe.Codec
import io.circe.derivation.ConfiguredCodec
import sttp.tapir.Schema
import sttp.tapir.integ.cats.codec.*

class UsersService(usersRepository: UsersRepository):
  def registerUser(registrationForm: UserRegistrationForm): IO[Either[RegistrationError, User]] = (for
    user <- 
      UserRegistrationForm
        .validate(registrationForm)
        .leftMap(UserValidationError.apply)
        .toEitherT
    
    _ <- EitherT(usersRepository.registerUser(user))
  yield user).value

  def login(userLogin: UserLogin): IO[Option[User]] =
    usersRepository
      .retrieveUser(userLogin.id)
      .map:
        case Some(user) =>
          if PasswordUtils.checkPasswords(userLogin.password, user.passwordHash) then Some(user)
          else None
        case _ => None

sealed trait RegistrationError derives Codec, Schema
case class UserValidationError(registrationErrors: NonEmptyChain[RegistrationFormError]) extends RegistrationError
case class UserAlreadyExists(email: UserId) extends RegistrationError

case class UserLogin(id: UserId, password: String) derives Codec, Schema
