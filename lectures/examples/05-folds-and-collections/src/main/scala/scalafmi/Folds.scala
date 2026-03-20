package scalafmi

def map[A, B](l: List[A])(f: A => B): List[B] =
  l.foldRight(List.empty)((x, xs) => f(x) :: xs)

def isEven(n: Int): Boolean = n % 2 == 0

@main
def test = println:
  exists(List(2, 3, 6))(isEven)

def filter[A](l: List[A])(f: A => Boolean): List[A] =
  l.foldRight(List.empty)((x, xs) => if f(x) then x :: xs else xs)

def size[A](l: List[A]): Int = l.foldLeft(0)((s, _) => s + 1)

def max(l: List[Int]): Int = l.reduceLeft(math.max)
def maxOpt(l: List[Int]): scala.Option[Int] = l.reduceLeftOption(math.max)

// По-конкретни и хомогенни типове – повече опции за имплементация
// По-абстрактни и хетерогенни типове – по-малко опции за имплементация

def reverse[A](l: List[A]): List[A] = l.foldLeft(List.empty)((xs, x) => x :: xs)

def f4[A](l1: List[A], l2: List[A]): List[A] = ???

def f5[A](l: List[A], x: A): Boolean = ???

def fact(n: Int): Int = (1 to n).foldLeft(1)(_ * _)
def fib(n: Int): Int = (1 to n)
  .foldLeft((a = 0, b = 1))((acc, _) => (acc.b, acc.a + acc.b))
  .a

def forall[A](l: List[A])(f: A => Boolean): Boolean = l.foldLeft(true)(_ && f(_))
def exists[A](l: List[A])(f: A => Boolean): Boolean = !forall(l)(!f(_))
