package streams.util

import org.asynchttpclient.*
import org.asynchttpclient.Dsl.*
import cats.effect.IO

import scala.util.Try

object HttpClient:
  val client = asyncHttpClient()

  def getIO(url: String): IO[Response] =
    cats.effect.IO.executionContext.flatMap { ec =>
      cats.effect.IO.async_ { callback =>
        val eventualResponse = client.prepareGet(url).setFollowRedirect(true).execute()
        eventualResponse.addListener(() => callback(Try(eventualResponse.get()).toEither), r => ec.execute(r))
      }
    }

  def shutdown() = client.close()

case class BadResponse(statusCode: Int, response: String) extends Exception
