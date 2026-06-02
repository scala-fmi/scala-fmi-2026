package tapir

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.EitherValues
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.client4.circe.*
import sttp.client4.testing.BackendStub
import sttp.client4.{UriContext, basicRequest}
import sttp.tapir.integ.cats.effect.CatsMonadError
import sttp.tapir.server.stub4.TapirStubInterpreter
import tapir.library.*
import tapir.server.LibraryController

class EndpointsSpec extends AsyncFlatSpec with Matchers with EitherValues with AsyncIOSpec:
  val tapirStubInterpreter = TapirStubInterpreter(BackendStub(new CatsMonadError[IO]()))

  val books = List(
    Book(BookId("1"), "Test Book", List(AuthorId("1")), "investigation"),
    Book(BookId("4"), "Echo", List(AuthorId("1"), AuthorId("2")), "analysis")
  )
  val authors = List(
    Author(AuthorId("1"), "Smith"),
    Author(AuthorId("2"), "Pink")
  )
  val library = new Library(books, authors)

  it should "list available books" in {
    // given
    val libraryController = new LibraryController(library)

    val backendStub = tapirStubInterpreter
      .whenServerEndpointRunLogic(libraryController.retrieveBooks)
      .backend()

    // when
    val response = basicRequest
      .get(uri"http://test.com/v1/books?number=1&limit=10")
      .response(asJson[List[BookSummary]])
      .send(backendStub)

    // then
    val bookSummaries = books.map(BookSummary.apply)

    response.asserting(_.body.value shouldBe bookSummaries)
  }
