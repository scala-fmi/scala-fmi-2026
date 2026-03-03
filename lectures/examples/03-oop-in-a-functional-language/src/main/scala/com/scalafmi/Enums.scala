package com.scalafmi

enum WeekDay:
  case Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday

def mood(d: WeekDay) = d match
  case WeekDay.Saturday | WeekDay.Sunday => "alive"
  case WeekDay.Monday => "why"
  case _ => "default"

def isWorkingDay(day: WeekDay) = day != WeekDay.Saturday && day != WeekDay.Sunday

enum Payment:
  case Cash
  case Card(last4: String)
  case BankTransfer(iban: String)

def fee(p: Payment): Int = p match
  case Payment.Cash => 0
  case Payment.Card(_) => 30
  case Payment.BankTransfer(iban) => if iban.startsWith("BG") then 10 else 20

// Scala 2
// sealed trait Payment
// object Payment:
//   case object Cash extends Payment
//   final case class Card(last4: String) extends Payment
//   final case class BankTransfer(iban: String) extends Payment

object Enums:
  @main def enumerations(): Unit =
    isWorkingDay(WeekDay.Wednesday)
    WeekDay.valueOf("Monday") // WeekDay.Monday
    WeekDay.values // Array(Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday)
