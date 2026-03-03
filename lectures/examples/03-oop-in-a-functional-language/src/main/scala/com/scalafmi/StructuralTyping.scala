package com.scalafmi

import reflect.Selectable.reflectiveSelectable

case class Eagle(name: String):
  def flyThrough(location: String): String =
    s"Hi, I am old $name and I am looking for food at $location."

case class Owl(age: Int):
  def flyThrough(location: String): String =
    s"Hi, I am a $age years old owl and I am flying through $location. Hoot, hoot!"

type Bird = {
  def flyThrough(location: String): String
}

def checkLocations(
  locations: List[String],
  bird: { def flyThrough(location: String): String }
): List[String] =
  for location <- locations
  yield bird.flyThrough(location)

// checkLocations(List("Sofia", "Varna"), Owl(7))
// checkLocations(List("Sofia", "Varna"), Eagle("Henry"))

type Closable = { def close(): Unit }

def using[R](resource: Closable)(f: resource.type => R): R =
  try f(resource)
  finally resource.close()
