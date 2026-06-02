package tapir.client

import cats.effect.IO
import sttp.client4.Backend
import sttp.model.Uri
import sttp.tapir.PublicEndpoint
import sttp.tapir.client.sttp4.SttpClientInterpreter

class ApiClient(base: Uri, sttpBackend: Backend[IO]):
  private val interpreter = SttpClientInterpreter()

  def apply[I, E, O](endpoint: PublicEndpoint[I, E, O, Any]): I => IO[Either[E, O]] =
    interpreter.toClientThrowDecodeFailures(endpoint, Some(base), sttpBackend)

  def applySuccess[I, E, O](endpoint: PublicEndpoint[I, E, O, Any]): I => IO[O] =
    interpreter.toClientThrowErrors(endpoint, Some(base), sttpBackend)
