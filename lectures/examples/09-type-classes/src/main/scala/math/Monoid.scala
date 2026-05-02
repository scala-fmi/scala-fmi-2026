package math

trait Semigroup[A]:
  extension (a: A) def |+|(b: A): A

trait Monoid[A] extends Semigroup[A]:
  def combine(a: A, b: A): A = a |+| b

  def identity: A

object Monoid:
  def apply[A](using m: Monoid[A]): Monoid[A] = m

  given Monoid[Int]:
    extension (a: Int) def |+|(b: Int): Int = a + b

    def identity: Int = 0

  given [A: Monoid, B: Monoid] => Monoid[(A, B)]:
    extension (a: (A, B))
      def |+|(b: (A, B)): (A, B) = (a, b) match
        case ((a1, b1), (a2, b2)) => (a1 |+| a2, b1 |+| b2)

    def identity: (A, B) = (Monoid[A].identity, Monoid[B].identity)

  given Monoid[String]:
    extension (a: String) def |+|(b: String): String = a + b

    def identity: String = ""

  given [A: Monoid] => Monoid[Option[A]]:
    extension (ma: Option[A])
      def |+|(mb: Option[A]): Option[A] = (ma, mb) match
        case (Some(a), Some(b)) => Some(a |+| b)
        case (Some(_), _) => ma
        case (_, Some(_)) => mb
        case _ => None

    def identity: Option[A] = None

  given mapMonoid: [K, V: Monoid] => Monoid[Map[K, V]]:
    extension (a: Map[K, V])
      def |+|(b: Map[K, V]): Map[K, V] =
        val vIdentity = Monoid[V].identity

        (a.keySet ++ b.keySet).foldLeft(identity): (acc, key) =>
          acc.updated(key, a.getOrElse(key, vIdentity) |+| b.getOrElse(key, vIdentity))

    def identity: Map[K, V] = Map.empty[K, V]
