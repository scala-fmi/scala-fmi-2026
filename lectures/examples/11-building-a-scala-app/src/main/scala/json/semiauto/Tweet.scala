package json.semiauto

import io.circe.{Codec, Decoder, Encoder}
import io.circe.derivation.{Configuration, ConfiguredEnumCodec}
import io.circe.parser.*
import io.circe.syntax.*
import cats.syntax.all.*

case class Likes(quantity: Int = 0) derives Codec
case class Tweet(id: Int, content: String, likes: Likes) derives Codec

@main def derivedCodecExample: Unit =
  val tweet = Tweet(1, "Some random content", Likes(123124))

  println(tweet.asJson)

  val json =
    """
      |{
      |  "id" : 1,
      |  "content" : "Some random content",
      |  "likes" : {
      |    "quantity": 123124
      |  }
      |}""".stripMargin

  val decodedTweet = decode[Tweet](json)
  println(decodedTweet)

  // TODO: examples with confugration codec, ADTs, enums, wrapped values
  // introducing iron/refined libraries

given Configuration = Configuration.default.withKebabCaseConstructorNames
enum Color derives ConfiguredEnumCodec:
  case RedButton
  case Blue
  case Green

@main
def enumSerialization =
  println(List(Color.Blue, Color.RedButton, Color.Green).asJson)

//def unwrappedCodec[W, U: {Encoder, Decoder}](wrap: U => W)(unwrap: W => U): Codec[W] =
//  Codec.from(Decoder[U], Encoder[U]).iemap(u => Right(wrap(u)))(unwrap)
//
//case class UserId(email: String)
//object UserId:
//  given Codec[UserId] = unwrappedCodec(UserId.apply)(_.email)
//
//@main def anyValSerialisation =
//  println(UserId("zdravko@gmail.com").asJson)

sealed trait AreaId
case object MainArea extends AreaId
case class SupplementaryArea(areaId: String) extends AreaId

object AreaId:
  given Codec[AreaId] = Codec
    .implied[Option[String]]
    .iemap(_.map(SupplementaryArea(_)).getOrElse(MainArea).asRight) {
      case MainArea => None
      case SupplementaryArea(area) => Some(area)
    }
