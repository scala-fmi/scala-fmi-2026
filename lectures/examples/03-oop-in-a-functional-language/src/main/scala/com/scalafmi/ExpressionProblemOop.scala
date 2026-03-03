package com.scalafmi.expressionoop

trait Shape:
  def area: Double

case class Circle(r: Double) extends Shape:
  def area: Double = math.Pi * r * r

case class Rectangle(a: Double, b: Double) extends Shape:
  def area: Double = a * b

case class Square(a: Double) extends Shape:
  def area: Double = a * a
