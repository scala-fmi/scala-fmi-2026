package exercises

import scala.annotation.tailrec

object Recursion:
  def fact(n: Int): Int =
    if n <= 1 then 1
    else n * fact(n - 1)

  def size[A](l: List[A]): Int =
    if l.isEmpty then 0
    else 1 + size(l.tail)

  def sum(l: List[Int]): Int =
    if l.isEmpty then 0
    else l.head + sum(l.tail)

  def fibonacci(i: Int): Int =
    if i <= 0 then 0
    else if i == 1 then 1
    else fibonacci(i - 1) + fibonacci(i - 2)

object TailRecursion:
  // We could introduce inner functions if we don't want to pollute the interface
  // But let's use default parameters

  def fact(n: Int): Int =
    @tailrec
    def loop(n: Int, acc: Int = 1): Int =
      if n <= 1 then acc
      else loop(n - 1, acc * n)

    loop(n, 1)

  def size[A](l: List[A]): Int =
    @tailrec
    def sizeHelper(l: List[A], acc: Int): Int =
      if l.isEmpty then acc
      else sizeHelper(l.tail, acc + 1)

    sizeHelper(l, 0)

  def sum(l: List[Int]): Int =
    @tailrec
    def sum(l: List[Int], acc: Int): Int =
      if l.isEmpty then acc
      else sum(l.tail, l.head + acc)

    sum(l, 0)

  // @tailrec
  def fibonacci(i: Int): Int = ???

@main
def testFibonacci(): Unit = println:
  List(1, 2, 3, 4, 5, 6).map(TailRecursion.fibonacci)

object MoreListFunctions:
  @tailrec
  def drop[A](la: List[A], n: Int): List[A] =
    if n <= 0 || la.isEmpty then la
    else drop(la.tail, n - 1)

  def nthElement[A](la: List[A], n: Int): A = ???

  def reverse[A](l: List[A]): List[A] = ???

  def take[A](la: List[A], n: Int): List[A] = ???

  def concat(l1: List[Int], l2: List[Int]): List[Int] = ???
