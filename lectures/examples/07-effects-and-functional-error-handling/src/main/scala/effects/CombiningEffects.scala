package effects

import cats.effect.IO

//sealed trait ProcessingError extends Product with Serializable
//case class KeyNotFound(key: String) extends ProcessingError
//case class NotNumeric(s: String) extends ProcessingError
//case object DivisionByZero extends ProcessingError

def extractFutureEither(params: Params): IO[Either[ProcessingError, String]] =
  IO.pure(extractMaybe(params).toRight(KeyNotFound("num")))

def parseFutureEither(param: String): IO[Either[ProcessingError, Int]] =
  IO.pure(parseMaybe(param).toRight(NotNumeric(param)))

def reciprocalFutureEither(num: Int): IO[Either[ProcessingError, Double]] =
  IO.pure(reciprocalMaybe(num).toRight(DivisionByZero))

def processFutureEither(params: Params) = for
  paramEither <- extractFutureEither(params)
  numEither <- paramEither match
    case Right(param) => parseFutureEither(param)
    case l @ Left(_) => IO.pure(l)
  rEither <- numEither match
    case Right(num: Int) => reciprocalFutureEither(num)
    case l @ Left(_) => IO.pure(l)
yield rEither

// type for compose IO and Either

case class IOEither[E, A](value: IO[Either[E, A]]):
  def map[B](f: A => B): IOEither[E, B] =
    IOEither(value.map(_.map(f)))

  def flatMap[B](f: A => IOEither[E, B]): IOEither[E, B] = IOEither(
    value.flatMap:
      case Right(r) => f(r).value
      case Left(l) => IO.pure(Left(l))
  )

// with EitherT

import cats.data.EitherT
def processEitherT(params: Params): IO[Either[ProcessingError, Double]] =
  (for
    param <- EitherT(extractFutureEither(params))
    num <- EitherT(parseFutureEither(param))
    r <- EitherT(reciprocalFutureEither(num))
  yield r).value
  