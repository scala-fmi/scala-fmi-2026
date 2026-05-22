package tapir.library

import cats.effect.IO
import cats.syntax.all.*
import io.circe.Codec
import sttp.tapir
import sttp.tapir.{CodecFormat, Schema, SchemaType, Validator}

opaque type BookId = String
object BookId:
  def apply(idString: String): BookId = idString
  extension (bookId: BookId) def asString: String = bookId

  given Codec[BookId] = Codec.implied[String]
  given Schema[BookId] = Schema(SchemaType.SString())
  given tapir.Codec[String, BookId, CodecFormat.TextPlain] =
    tapir.Codec.string.map(BookId.apply)(_.asString)

case class Book(id: BookId, name: String, authors: List[AuthorId], genre: String) derives Codec, Schema

opaque type AuthorId = String
object AuthorId:
  def apply(idString: String): AuthorId = idString
  extension (authorId: AuthorId) def asString: String = authorId

  given Codec[AuthorId] = Codec.implied[String]
  given Schema[AuthorId] = Schema(SchemaType.SString())
  given tapir.Codec[String, AuthorId, CodecFormat.TextPlain] = tapir.Codec.string.map(AuthorId.apply)(_.asString)

case class Author(id: AuthorId, name: String) derives Codec, Schema

class Library(books: List[Book], authors: List[Author]):
  private val bookIdToBook = books.map(book => book.id -> book).toMap
  private val authorIdToAuthor = authors.map(author => author.id -> author).toMap

  def findBook(bookId: BookId): IO[Option[Book]] = bookIdToBook.get(bookId).pure

  def findAuthor(authorId: AuthorId): IO[Option[Author]] = authorIdToAuthor.get(authorId).pure

  def allBooks: IO[List[Book]] = bookIdToBook.values.toList.pure

  def allAuthors: IO[List[Author]] = authors.pure

object Library:
  private val books = List(
    Book(BookId("1"), "Programming in Scala", List(AuthorId("1"), AuthorId("2")), "Computer Science"),
    Book(BookId("2"), "Programming Erlang", List(AuthorId("3")), "Computer Science"),
    Book(BookId("3"), "American Gods", List(AuthorId("4")), "Fantasy"),
    Book(BookId("4"), "The Fellowship of the Ring", List(AuthorId("5")), "Fantasy"),
    Book(
      BookId("5"),
      "The Book",
      List(
        AuthorId("1"),
        AuthorId("3"),
        AuthorId("4"),
        AuthorId("5")
      ),
      "Fantasy"
    )
  )
  private val authors = List(
    Author(AuthorId("1"), "Martin Odersky"),
    Author(AuthorId("2"), "Bill Venners"),
    Author(AuthorId("3"), "Joe Armstrong"),
    Author(AuthorId("4"), "Neil Gaiman"),
    Author(AuthorId("5"), "J. R. R. Tolkien")
  )

  val TheGreatLibrary = new Library(books, authors)
