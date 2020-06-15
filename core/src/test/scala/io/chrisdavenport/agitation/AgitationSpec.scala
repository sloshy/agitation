package io.chrisdavenport.agitation

import org.specs2._
import cats.effect._
import cats.implicits._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

object AgitationSpec extends mutable.Specification {

  "Agitation" should {
    "never return in an empty agitation" in {
      implicit val cs = IO.contextShift(ExecutionContext.global)
      implicit val t = IO.timer(ExecutionContext.global)

      val test = for {
        ag <- Agitation.create[IO]
        out <- Concurrent[IO].race(
          ag.settled,
          Timer[IO].sleep(1.second)
        )
      } yield out must_=== Right(())

      test.unsafeToFuture
    }

    "return agitation if it settles" in {
      implicit val cs = IO.contextShift(ExecutionContext.global)
      implicit val t = IO.timer(ExecutionContext.global)

      val test = for {
        ag <- Agitation.create[IO]
        out <- Concurrent[IO].race(
          ag.settled,
          ag.agitate(2.seconds) >> Timer[IO].sleep(3.seconds)
        )
      } yield out must_=== Left(())

      test.unsafeToFuture
    }
  }

}