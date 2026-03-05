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

opaque type Meter = Double
object Meter:
  def apply(value: Double): Meter = value
  extension (self: Meter)
    def value: Double = self
    def +(that: Meter): Meter = Meter(self + that)
    def *(coefficient: Double): Meter = Meter(coefficient * self)
    def show: String = s"$self meters"

object MeterExample:
  case class Circle(radius: Meter):
    def circumference: Meter = radius * 2 * math.Pi

  @main def run(): Unit =
    println:
      Circle(Meter(2)).circumference.show

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
