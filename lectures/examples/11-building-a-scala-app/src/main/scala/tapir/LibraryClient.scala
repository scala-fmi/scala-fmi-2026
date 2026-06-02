package tapir

import cats.effect.{IO, IOApp}
import sttp.client4.*
import sttp.client4.httpclient.fs2.HttpClientFs2Backend
import tapir.client.{ApiClient, LibraryApi, LibraryClientUI}
import tapir.library.{Author, Book}

case class BookWithAuthors(book: Book, authors: List[Author])

object LibraryClient extends IOApp.Simple:
  val app = for
    sttpBackend <- HttpClientFs2Backend.resource[IO]()

    libraryApi = new LibraryApi(new ApiClient(uri"http://localhost:8080", sttpBackend))
    libraryClientUI = new LibraryClientUI(libraryApi)
  yield libraryClientUI

  def run: IO[Unit] =
    app
      .use(ui => ui.libraryApp)
      .guarantee(IO.println("Thank you for browsing our library :)!"))
