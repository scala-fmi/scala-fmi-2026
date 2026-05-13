package json

import JsonValue.JsonObject

import JsonSerializable.given

case class Person(name: String, email: String, age: Int)

object Person:
  given JsonSerializable[Person]:
    extension (person: Person)
      def toJson: JsonValue = JsonObject(
        Map(
          "name" -> person.name.toJson,
          "email" -> person.email.toJson,
          "age" -> person.age.toJson
        )
      )
