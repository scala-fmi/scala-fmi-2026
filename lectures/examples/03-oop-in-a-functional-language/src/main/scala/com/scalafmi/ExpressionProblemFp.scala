package com.scalafmi.expressionfp

enum Shape:
  case Circle(r: Double)
  case Rectangle(a: Double, b: Double)

// Scala 2
// trait Shape
// case class Circle(r: Double) extends Shape
// case class Rectangle(a: Double, b: Double) extends Shape

import com.scalafmi.expressionfp.Shape.*

def area(s: Shape): Double = s match
  case Circle(r) => math.Pi * r * r
  case Rectangle(a, b) => a * b
