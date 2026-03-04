package com.scalafmi

opaque type PersonId = String
object PersonId:
  def apply(s: String): PersonId = s
  extension (id: PersonId) def value: String = id

opaque type LocationId = String
object LocationId:
  def apply(s: String): LocationId = s
  extension (id: LocationId) def value: String = id

object OpaqueTypes:
  @main def opaqueType(): Unit =
    def createAddressRegistration(person: PersonId, location: LocationId) = ???
    createAddressRegistration(PersonId("100"), LocationId("5")) // OK
    // createAddressRegistration(LocationId("5"), PersonId("100")) // won't compile

opaque type OrderId = String

object OrderId:
  def apply(country: String, store: Int, number: Long): OrderId =
    s"${country.toUpperCase}-${store}-${number}"

  def from(
    country: String,
    store: Int,
    number: Long
  ): Either[String, OrderId] =
    if country.matches("[A-Za-z]{2}") && store > 0 && number > 0
    then Right(apply(country, store, number))
    else Left("Invalid OrderId components")

  extension (id: OrderId)
    def value: String = id
    def parts: (String, Int, Long) =
      val Array(c, s, n) = id.split('-')
      (c, s.toInt, n.toLong)
