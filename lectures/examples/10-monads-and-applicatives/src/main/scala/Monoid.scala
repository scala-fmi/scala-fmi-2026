trait Monad[F[_]]:

  extension [A](fa: F[A])
    def flatMap[B](f: A => F[B]): F[B] =
      compose(_ => fa, f)(())

  def unit[A](a: => A): F[A]

  def compose[A, B, C](f: A => F[B], g: B => F[C]): A => F[C] =
    a => f(a).flatMap(g)
