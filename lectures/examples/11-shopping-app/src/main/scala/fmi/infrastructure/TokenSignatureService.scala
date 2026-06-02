package fmi.infrastructure

import cats.effect.IO
import io.circe.parser.decode
import io.circe.syntax.given
import io.circe.{Decoder, Encoder}
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}

import java.time.{Clock, ZoneOffset}
import scala.concurrent.duration.Duration

class TokenSignatureService(privateKey: String):
  private val algorithm = JwtAlgorithm.HS256

  private def clockIO: IO[Clock] = IO.realTimeInstant.map(Clock.fixed(_, ZoneOffset.UTC))

  def sign[E: Encoder.AsObject](payload: E, expiration: Duration): IO[String] = clockIO.map: clock =>
    JwtCirce(clock).encode(
      JwtClaim(payload.asJson.toString).expiresIn(expiration.toSeconds)(using clock),
      privateKey,
      algorithm
    )

  def validateAndRetrieve[E: Decoder](str: String): IO[Option[E]] = clockIO.map: clock =>
    JwtCirce
      .decode(str, privateKey, Seq(algorithm))
      .filter(_.isValid(using clock))
      .toOption
      .flatMap: claim =>
        decode[E](claim.content).toOption
