package scalafmi

enum WeekDay:
  case Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday

def mood(d: WeekDay) = d match
  case WeekDay.Saturday | WeekDay.Sunday => "alive"
  case WeekDay.Monday => "why"
  case _ => "default"

def isWorkingDay(day: WeekDay) = day != WeekDay.Saturday && day != WeekDay.Sunday

object Enums:
  @main def enumerations(): Unit =
    isWorkingDay(WeekDay.Wednesday)
    WeekDay.valueOf("Monday") // WeekDay.Monday
    WeekDay.values // Array(Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday)
