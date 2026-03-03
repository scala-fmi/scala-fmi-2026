package com.scalafmi.typealgebra

trait LovingAnimal:
  def name: String
  def hug = s"A hug from $name"

case class Owl(name: String, age: Int):
  def flyThrough(location: String): String =
    s"Hi, I am a $age years old owl and I am flying through $location. Hoot, hoot!"

def petOwl(owl: Owl & LovingAnimal): Unit =
  println(s"Owl: $owl can pet")

def toInteger(value: String | Int | Double): Int = value match
  case n: Int => n
  case s: String => s.toInt
  case d: Double => d.toInt

object TypeAlgebra:
  @main def types(): Unit =
    val lovelyOwl: Owl & LovingAnimal = new Owl("Oliver", 7) with LovingAnimal
    lovelyOwl.hug // A hug from Oliver
    lovelyOwl.flyThrough("Plovdiv") // Hi, I am a 7 years old owl and
    // I am flying through Plovdiv. Hoot, hoot!

    petOwl(lovelyOwl)

    toInteger("10") // 10
    toInteger(10) // 10
    toInteger(10.0) // 10
    // toInteger(List(10)) // не се компилира
