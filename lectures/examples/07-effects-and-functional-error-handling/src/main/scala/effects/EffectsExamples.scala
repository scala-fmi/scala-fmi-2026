package effects

import scala.util.{Failure, Try}

type Params = Map[String, String]

def extract(params: Params): String = params("number")
def parse(s: String): Int = s.toInt
val reciprocal: PartialFunction[Int, Double] =
  case x if x != 0 => 1.toDouble / x

val process = extract andThen parse andThen reciprocal

@main
def tryOutProcess = println:
  process(Map("number" -> "0"))

def extractMaybe(params: Params): Option[String] = params.get("number")
def parseMaybe(s: String): Option[Int] = Try(s.toInt).toOption
def reciprocalMaybe(n: Int): Option[Double] = reciprocal.lift(n)

def processMaybe(params: Params): Option[Double] =
  for
    integerString <- extractMaybe(params)
    integer <- parseMaybe(integerString)
    reciprocal <- reciprocalMaybe(integer)
  yield reciprocal

def processMaybe2(params: Params): Option[Double] =
  extractMaybe(params)
    .flatMap(parseMaybe)
    .flatMap(reciprocalMaybe)

@main
def tryOutProcessOption = println:
  processMaybe(Map("number" -> "10"))

def extractTry(params: Params): Try[String] =
  Try(extract(params)).orElse(Failure(new IllegalArgumentException("params don't have 'number' element")))
def parseTry(s: String): Try[Int] = Try(parse(s))
def reciprocalTry(n: Int): Try[Double] = Try(reciprocal(n))

def processTry(params: Params): Try[Double] =
  for
    integerString <- extractTry(params)
    integer <- parseTry(integerString)
    reciprocal <- reciprocalTry(integer)
  yield reciprocal

@main
def tryOutProcessTry = println:
  processTry(Map("numb2er" -> "0"))

sealed trait ProcessingError
case class KeyNotFound(key: String) extends ProcessingError
case class NotNumeric(s: String) extends ProcessingError
case object DivisionByZero extends ProcessingError

def extractEither(params: Params) = extractMaybe(params).toRight(KeyNotFound("number"))
def parseEither(s: String): Either[NotNumeric, Int] = parseMaybe(s).toRight(NotNumeric(s))
def reciprocalEither(n: Int): Either[DivisionByZero.type, Double] = reciprocalMaybe(n).toRight(DivisionByZero)

def processEither(params: Params): Either[ProcessingError, Double] =
  val maybeNumber = extractEither(params)

  for
    integerString <- extractEither(params)
    integer <- parseMaybe(integerString).toRight(NotNumeric(integerString))
    reciprocal <- reciprocalEither(integer)
  yield reciprocal

@main
def tryOutProcessEither = println:
  processEither(Map("number" -> "f0"))
