package com.scalafmi

object domain:
  opaque type PersonId = String
  object PersonId:
    def apply(s: String): PersonId = s
    extension (id: PersonId) def value: String = id
    // Bonus
    // def from(s: String): Option[PersonId] = Option.when(s.nonEmpty)(s)

  opaque type LocationId = String
  object LocationId:
    def apply(s: String): LocationId = s
    extension (id: LocationId) def value: String = id

object OpaqueTypes:
  @main def opaqueTypes(): Unit =
    import domain.*

    def createAddressRegistration(person: PersonId, location: LocationId) = ???
    createAddressRegistration(PersonId("100"), LocationId("5")) // OK
    // createAddressRegistration(LocationId("5"), PersonId("100")) // won't compile
