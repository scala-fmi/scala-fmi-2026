package com.scalafmi

final case class PersonId(value: String) extends AnyVal
final case class LocationId(value: String) extends AnyVal

def createAddressRegistration(person: PersonId, location: LocationId) = ???

case class Meter(amount: Double) extends AnyVal:
  def +(m: Meter): Meter = Meter(amount + m.amount)
  def *(coefficient: Double): Meter = Meter(coefficient * amount)
  override def toString = s"$amount meters"

case class Circle(radius: Meter):
  def circumference: Meter = radius * 2 * math.Pi

object AnyValClasses:
  @main def anyVal(): Unit =
    createAddressRegistration(PersonId("100"), LocationId("5")) // OK
    // createAddressRegistration(LocationId("5"), PersonId("100")) // won't compile
    Circle(Meter(2)).circumference.toString // 12.566370614359172 meters
