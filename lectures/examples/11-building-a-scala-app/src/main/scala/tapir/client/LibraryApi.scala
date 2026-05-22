package tapir.client

import cats.data.OptionT
import cats.effect.IO
import cats.syntax.all.*
import tapir.BookWithAuthors
import tapir.library.*
import tapir.examples.CustomErrorsEndpoints.{CustomError, CustomError2, Person}
import sttp.client4.Backend
import sttp.model.Uri
import sttp.tapir.client.sttp4.SttpClientInterpreter
import tapir.examples.CustomErrorsEndpoints

class LibraryApi(base: Uri, backend: Backend[IO]):
  private val interpreter = SttpClientInterpreter()

  def listBooks: IO[List[BookSummary]] =
    interpreter
      .toClientThrowErrors(LibraryEndpoints.retrieveBooksEndpoint, Some(base), backend)
      .apply(())

  def retrieveBook(bookId: BookId): IO[Option[Book]] =
    interpreter
      .toClientThrowDecodeFailures(LibraryEndpoints.retrieveBookEndpoint, Some(base), backend)
      .apply(bookId)
      .map(_.toOption)

  def retrieveAuthor(authorId: AuthorId): IO[Author] =
    interpreter
      .toClientThrowErrors(LibraryEndpoints.retrieveAuthorEndpoint, Some(base), backend)
      .apply(authorId)

  def retrieveBookWithAuthors(bookId: BookId): IO[Option[BookWithAuthors]] = (for
    book <- OptionT(retrieveBook(bookId))
    authors <- OptionT.liftF(book.authors.parTraverse(authorId => retrieveAuthor(authorId)))
  yield BookWithAuthors(book, authors)).value

  def person: IO[Either[CustomError | CustomError2, List[Person]]] =
    interpreter
      .toClientThrowDecodeFailures(CustomErrorsEndpoints.peopleListing, Some(base), backend)
      .apply(())
