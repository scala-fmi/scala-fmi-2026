package http

import util.HttpServiceUrls
import io.{IO, IORuntime}

@main def runHttpRequestsExample =
  val myIp = HttpClient
    .getIO("http://icanhazip.com")
    .map(_.getResponseBody)

  val app = myIp >>= IO.println

  val randomNumber = HttpClient
    .getIO(HttpServiceUrls.randomNumberUpTo(256))
    .map(_.getResponseBody)

  val app2 = randomNumber.zipMap(randomNumber)((a, b) => s"Number are $a and $b") >>= IO.println

//  app2.unsafeRunSync(IORuntime.default)

  val example = HttpClient
    .getIO("http://example.org")
    .map(_.getResponseBody)

  val endResult = for
    (ipResult, exampleResult) <- myIp zip example
    _ <- IO.println(ipResult)
    _ <- IO.println(exampleResult)
    r <- HttpClient.getIO(HttpServiceUrls.randomNumberUpTo(ipResult.length + exampleResult.length))
  yield r.getResponseBody

  (endResult >>= IO.println).unsafeRun()

  // We will talk about how to manage resource later. Now we need to shut them down manually
  HttpClient.shutdown()
  IORuntime.default.shutdown()
