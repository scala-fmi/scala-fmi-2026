package callbacks

import product.{Product, ProductFactory, Verification}

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{Executor, Executors}
import scala.annotation.tailrec

object Callbacks:
  val threadPool: Executor = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors)
  def execute(work: => Any): Unit = threadPool.execute(() => work)

  def produceBook(onComplete: Product => Unit): Unit =
    execute:
      val product = ProductFactory.produceProduct("Book")

      execute(onComplete(product))

  def produce2Books(onComplete: (Product, Product) => Unit): Unit =
    val maybeFirstBook: AtomicReference[Option[Product]] = AtomicReference(None)

    @tailrec
    def callback(newBook: Product): Unit =
      maybeFirstBook.get() match
        case Some(firstBook) => onComplete(firstBook, newBook)
        case None => 
          if !maybeFirstBook.compareAndSet(None, Some(newBook)) then
            callback(newBook)

    produceBook(callback)
    produceBook(callback)

  @main def run: Unit = execute:
    produce2Books: (book1, book2) =>
      println(s"Book 1: $book1")
      println(s"Book 2: $book2")

  def verifyProduct(product: Product)(onVerified: Verification => Unit): Unit = execute:
    val verification = ProductFactory.verifyProduct(product)

    execute(onVerified(verification))

  def produceInPipeline(callback: (List[Product], List[Verification]) => Unit): Unit =
    // Callback hell
    produceBook { a =>
      verifyProduct(a) { aVerification =>
        produceBook { b =>
          verifyProduct(b) { bVerification =>
            callback(List(a, b), List(aVerification, bVerification))
          }
        }
      }
    }
