package scalafmi.patternmatching

case class DriverName(value: String) extends AnyVal

case class LapTime(minutes: Int, seconds: Double) extends Ordered[LapTime]:
  def compare(that: LapTime): Int =
    (this.minutes * 60 + this.seconds) compare (that.minutes * 60 + that.seconds)

object FastestLap:
  // TODO: Split the string "Name: M:SS" into DriverName and LapTime
  // Example: "Verstappen: 1:24"
  def unapply(input: String): Option[(DriverName, LapTime)] = ???

object Podium:
  // TODO: Take a List[String] and return a Seq[DriverName] if there are at least 3
  // Example: List("Verstappen", "Norris", "Leclerc", "Hamilton", "Sainz", "Piastri")
  def unapplySeq(drivers: List[String]): Option[Seq[DriverName]] = ???

def analyzeRace(lapRecord: String, results: List[String]): String = {
  val trackRecord = LapTime(1, 15.0)

  (lapRecord, results) match {
    // TODO: Match if the driver who got the FastestLap is the same as the Winner (1st in Podium)

    // TODO: Match if the FastestLap time is faster than the 'trackRecord'

    // TODO: Match a specific 1-2 finish for Ferrari (Leclerc and Hamilton)

    // TODO: Match if the FastestLap time is below 1 minute and print it

    // TODO: Match if the FastestLap holder made it to the podium

    case _ => "Standard race conclusion."
  }
}
