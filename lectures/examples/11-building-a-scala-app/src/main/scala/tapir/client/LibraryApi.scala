package tapir.client

import cats.data.OptionT
import cats.effect.IO
import cats.syntax.all.*
import tapir.BookWithAuthors
import tapir.library.*

class LibraryApi(libraryApiClient: ApiClient):
  def listBooks: IO[List[BookSummary]] =
    libraryApiClient.applySuccess(LibraryEndpoints.retrieveBooksEndpoint)(Page(1, Some(5)))

  def retrieveBook(bookId: BookId): IO[Option[Book]] =
    libraryApiClient(LibraryEndpoints.retrieveBookEndpoint)(bookId).map(_.toOption)

  def retrieveAuthor(authorId: AuthorId): IO[Author] =
    libraryApiClient.applySuccess(LibraryEndpoints.retrieveAuthorEndpoint)(authorId)

  def retrieveBookWithAuthors(bookId: BookId): IO[Option[BookWithAuthors]] = (for
    book <- OptionT(retrieveBook(bookId))
    authors <- OptionT.liftF(book.authors.parTraverse(retrieveAuthor))
  yield BookWithAuthors(book, authors)).value
