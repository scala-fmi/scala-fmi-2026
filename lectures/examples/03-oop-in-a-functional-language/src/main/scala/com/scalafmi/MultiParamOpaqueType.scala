package com.scalafmi

object state:
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

object MultiParamOpaqueType:
  import state.*

  @main def multiParamOpaqueTypes(): Unit =
    val id1: OrderId = OrderId("bg", 42, 9001L)
    println(id1.value) // BG-42-9001
    println(id1.parts) // (BG,42,9001)
    val id2 = OrderId.from("Bulgaria", -1, 0) // Left(Invalid OrderId components)
