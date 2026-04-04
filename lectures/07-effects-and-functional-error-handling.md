---
title: Ефекти и функционална обработка на грешки
---

# Ефекти?

::: { .fragment }

Описват влияния извън нормалното изпълнение на функциите:

:::

::: incremental

* Частичност/частични функции
* Изключения/грешки
* Недетерминизъм
* Логване/мониторинг
* Изменимо състояние
* Вход/изход
* Асинхронност и конкурентност
* Ресурси
* ...

:::

# Ефекти

* Ефекти в изрази, които не са референтно прозрачни, са **странични ефекти**
* Езици като Scala, Haskel, Idris следят ефектите референтно прозрачно през типовата си система
  * Scala и Haskell – като return types
  * Idris – [ефекти](https://docs.idris-lang.org/en/latest/effects/index.html)
  * Експериментално в Scala – [capabilities](https://www.youtube.com/watch?v=p-iWql7fVRg)

# Роб Норис

Effects are good, side effects are bugs!

![](images/07-effects-and-functional-error-handling/rob-norris.jpg)

[Functional Programming with Effects](https://www.youtube.com/watch?v=po3wmq4S15A)

# Ефектни типове

* Частичност/частични функции – `Option`
* Изключения/грешки – `Try`/`Either`
* Недетерминизъм – `List`
* Логване/мониторинг – `Writer`
* Изменимо състояние – `State`
* Вход/изход – `IO`
* Асинхронност и конкурентност – `IO`, `Future`
* Ресурси – `Resource`

Всички следват структурата `F[_]`

# Обработка на грешки. Изключения

# Код когато прихващаме изключения

```java
try {
    try {
    } finally {
    }
    try {
        try {
        } catch(...) {
        } catch (...) {
        }
    } catch(...) {
    } finally {
    }
} finally {
    try {
    } finally {
    }
}
```

# Код когато прихващаме изключения

![](images/07-effects-and-functional-error-handling/rob-norris-facepalm.jpg){ height=320 }

# Проблеми с хвърлянето на изключения

* Не са референтно прозрачни
  * Рефакторирането на код с тях е error-prone
* Трудни за композиране
  * Прекъсват изчислението на кода
* Не са стойност
* Вързани са винаги към текущата нишка
  * не може грешката да се прехване от друга нишк

# Стъпка 1: Option

::: { .fragment }

От частични към тотални функции

```scala
val reciprocal: PartialFunction[Int,Double] =
  case x if x != 0 => 1.toDouble / x

reciprocal.lift(10) // Some(0.1)
reciprocal.lift(0) // None
```

:::

# Стъпка 2: Try

```scala
import scala.util.{Try,Success,Failure}

Success(1)
Failure(new RuntimeException("Something went wrong"))

Try("123".toInt) // Success(123)
Try("one-two-three".toInt) // Failure(java.lang.NumberFormatException: For input string: "one-two-three")
```

# Стъпка 3: Either

```scala
val right: Either[String, Int] = Right(1)
val left : Either[String, Int] = Left("Something went wrong")
```

> Either is what's right or whatever's left

# Дефинирани от нас грешки

```scala
sealed trait ProcessingError extends
case class KeyNotFound(key: String) extends ProcessingError
case class NotNumeric(s: String) extends ProcessingError
case object DivisionByZero extends ProcessingError
```

# Ефект за (синхронен) вход/изход – IO

# Ползи

* Реферетно прозрачна връзка с външния свят
* IO-то е стойност
  * може да се трансформира
  * може да се предава между нишки
  * може да се преизползва – например за имплементация на retry, отложено изпълнение или изпълнение по график
* Възможност за различни различни интерпретатори и инспекция
  * всички странични ефекти се имплементират на едно място, в преизползваем runtime
  * ...който може да бъде оптимизиран и настройван за съответната среда
  * повече контрол при тестване, например пропускане на заложени time out-и
* По-късно в курса ще видим как може да изразява асинхронност и канселации

# Комбиниране на ефекти

# Въпроси?
