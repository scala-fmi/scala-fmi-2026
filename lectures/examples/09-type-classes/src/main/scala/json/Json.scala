package json

enum JsonValue:
  case JsonNumber(value: BigDecimal)
  case JsonString(value: String)
  case JsonBoolean(value: Boolean)
  case JsonArray(value: Seq[JsonValue])
  case JsonObject(value: Map[String, JsonValue])
  case JsonNull

  override def toString: String = this match
    case JsonNumber(value) => value.toString
    case JsonString(value) => s"\"$value\""
    case JsonBoolean(value) => value.toString
    case JsonArray(elements) => "[" + elements.map(_.toString).mkString(", ") + "]"
    case JsonObject(members) =>
      val membersStrings = members.map { case (key, value) =>
        s"""  "$key": ${value.toString}"""
      }
      "{\n" + membersStrings.mkString(",\n") + "\n}"
    case JsonNull => "null"

trait JsonSerializable[A]:
  extension (a: A)
    def toJson: JsonValue
    def toJsonString: String = a.toJson.toString

object JsonSerializable:
  import JsonValue.*

  given JsonSerializable[Int]:
    extension (a: Int) def toJson: JsonValue = JsonNumber(a)

  given JsonSerializable[String]:
    extension (a: String) def toJson: JsonValue = JsonString(a)

  given JsonSerializable[Boolean]:
    extension (a: Boolean) def toJson: JsonValue = JsonBoolean(a)

  given [A: JsonSerializable] => JsonSerializable[Option[A]]:
    extension (a: Option[A]) def toJson: JsonValue = a.map(_.toJson).getOrElse(JsonNull)

  given [A: JsonSerializable] => JsonSerializable[List[A]]:
    extension (as: List[A])
      def toJson: JsonValue = JsonArray(
        as.map(_.toJson)
      )
