package scalafmi

trait Humanoid:
  def name: String
  val age: Int

case class Person(name: String) extends Humanoid:
  val age: Int = name.size

case class Robot(brand: String, serialNumber: String, age: Int) extends Humanoid:
  def name = s"$brand--$serialNumber"
