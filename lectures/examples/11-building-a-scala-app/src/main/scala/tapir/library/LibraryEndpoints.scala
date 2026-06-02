package tapir.library

import io.circe
import io.circe.*
import sttp.model.StatusCode.NotFound
import sttp.tapir.*
import sttp.tapir.json.circe.*

/*
case class Endpoint[SECURITY_INPUT, INPUT, ERROR_OUTPUT, OUTPUT, -R](
    securityInput: EndpointInput[SECURITY_INPUT],
    input: EndpointInput[INPUT],
    errorOutput: EndpointOutput[ERROR_OUTPUT],
    output: EndpointOutput[OUTPUT],
    info: EndpointInfo // docs
)

The types are just business logic types
They default to Unit (empty tuple), and can grow to larger tuples



                          /---> `EndpointInput`  >---\
  `EndpointTransput` >---                             ---> `EndpointIO`
                          \---> `EndpointOutput` >---/
 
 
                                                         /--> `Basic` >--> `Atom`
                                        /--> `Single` >--
  `EndpointInput` / `EndpointOutput` >--                 \--> `MappedPair`
                                        \--> `Pair`

Atoms describe a single input
Atoms have codecs – how to serialize/deserialize to an HTTP element (body, path param, header, etc...)

Codec[LOW_LEVEL_TYPE, HIGH_LEVEL_TYPE, CODEC_FORMAT]
           ^                ^               ^
           |                |               |
        Http type       Business type   Media type
     (usually String
      or a List[String])

Codecs have schema – how the atom type is represented in open api
Schemas have validators – they are part of the open api, but can also be custom logic validators
Codecs have media type – Json, TextPlain, XML, etc...

EndpointIO:
- Empty, type: Unit
- Body, type: Raw format -> T
  methods: jsonBody[T], plainBody, stringBody, rawBinaryBody, customCodecJsonBody
- Header, type: List[String]/String -> T, method: header[A]
- FixedHeader type: Unit, method: header(name, value)
- Headers type: List[Header], method: headers

EndpointInput:
- FixedMethod, type: Unit, get, post, ...
- PathCapture, type: String -> T, method: path[T]
- PathsCapture, type: List[String], method: paths
- FixedPath, type: Unit, method: "string"
- Query, type: List[String]/String -> T, method: query[T]
- QueryParams, type: QueryParams, method: queryParams
- Cookie, type: Option(String) -> T, method: cookie[T]
- ExtractFromRequest, type: ServerRequest -> T, method: extractFromRequest[T]

EndpointOutput:
- StatusCode, type: StatusCode, method: statusCode
- FixedStatusCode, type: Unit, method: statusCode(Ok)
- OneOf, type: T, methods: oneOf, ...

 */

case class Page(number: Int, limit: Option[Int])

val pageInput =
  query[Int]("number")
    .validate(Validator.positive)
    .and(query[Option[Int]]("limit"))
    .mapTo[Page]

object LibraryEndpoints:
  val baseEndpoint: PublicEndpoint[Unit, Unit, Unit, Any] = endpoint
  val v1BaseEndpoint: Endpoint[Unit, Unit, Unit, Unit, Any] = baseEndpoint.in("v1")

  val booksRootEndpoint: Endpoint[Unit, Unit, Unit, Unit, Any] = v1BaseEndpoint.in("books").tag("Books")

  val retrieveBooksEndpoint: Endpoint[Unit, Page, Unit, List[BookSummary], Any] =
    booksRootEndpoint
      .in(pageInput)
      .out(
        jsonBody[List[BookSummary]]
          .description("List of all books")
          .example(List(BookSummary(BookId("1"), "Programming in Scala"), BookSummary(BookId("2"), "1984")))
      )
      .get

  val retrieveBookEndpoint: Endpoint[Unit, BookId, String, Book, Any] =
    booksRootEndpoint
      .in("boook" / path[BookId].name("book-id"))
      .out(jsonBody[Book])
      .errorOut(statusCode(NotFound).and(jsonBody[String]))
      .get
      .summary("Retrieving all books")
      .description("fdskljfdksajfkldsjlfk")

  val authorsRootEndpoint: Endpoint[Unit, Unit, Unit, Unit, Any] = v1BaseEndpoint.in("authors").tag("Authors")

  val retrieveAuthorsEndpoint: Endpoint[Unit, Unit, Unit, List[Author], Any] =
    authorsRootEndpoint
      .out(jsonBody[List[Author]])
      .get

  val retrieveAuthorEndpoint: Endpoint[Unit, AuthorId, String, Author, Any] =
    authorsRootEndpoint
      .in(path[AuthorId]("auth-id"))
      .out(jsonBody[Author])
      .errorOut(statusCode(NotFound).and(jsonBody[String]))
      .get
