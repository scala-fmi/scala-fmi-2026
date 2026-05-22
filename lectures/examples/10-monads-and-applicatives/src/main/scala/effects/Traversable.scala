package effects

import effects.id.Id
import validation.RegistrationData

trait Traversable[F[_]] extends Functor[F]:
  extension [A](fa: F[A])
    def traverse[G[_]: Applicative, B](f: A => G[B]): G[F[B]]
    // if sequence is abstract traverse can be implemented as:
    // sequence(map(fa)(f))

    def map[B](f: A => B): Id[F[B]] =
      import effects.id.Id
      import effects.id.given Monad[Id]

      fa.traverse[Id, B](f)

  extension [G[_]: Applicative, A](fga: F[G[A]]) def sequence: G[F[A]] = traverse(fga)(identity)

object Traversable:
  def apply[F[_]](using a: Traversable[F]): Traversable[F] = a

  given Traversable[List]:
    extension [A](as: List[A])
      def traverse[G[_]: Applicative as g, B](f: A => G[B]): G[List[B]] =
        as.foldRight(g.unit(List[B]()))((a, fbs) => g.map2(f(a), fbs)(_ :: _))

  given Traversable[Option]:
    extension [A](oa: Option[A])
      def traverse[G[_]: Applicative as g, B](f: A => G[B]): G[Option[B]] =
        oa match
          case Some(a) => g.map(f(a))(Some(_))
          case None => g.unit(None)

  case class Tree[+A](value: A, children: List[Tree[A]])
  given Traversable[Tree]:
    extension [A](ta: Tree[A])
      def traverse[G[_]: Applicative as g, B](f: A => G[B]): G[Tree[B]] =
        g.map2(f(ta.value), ta.children.traverse(child => child.traverse(f)))(Tree.apply)

@main def runTraversableDemo =
  import Traversable.given
  import validation.FormValidatorNecApplicative
  import validation.FormValidatorNecApplicative.ValidationResult
  import validation.FormValidatorNecApplicative.given Applicative[ValidationResult]

  val listOfOptions: List[Option[Int]] = List(Some(1), Some(2), Some(3))

  println:
    listOfOptions.sequence

  println:
    val optionOfValidated: Option[ValidationResult[RegistrationData]] = Some(
      FormValidatorNecApplicative.validateForm(
        username = "fake$Us#rname",
        password = "password"
      )
    )

    optionOfValidated.sequence

  println:
    val optionOfValidated: Option[ValidationResult[RegistrationData]] = Some(
      FormValidatorNecApplicative.validateForm(
        username = "correctUsername",
        password = "Password123#"
      )
    )

    optionOfValidated.sequence

  println:
    val optionOfValidated: Option[ValidationResult[RegistrationData]] = None

    optionOfValidated.sequence
