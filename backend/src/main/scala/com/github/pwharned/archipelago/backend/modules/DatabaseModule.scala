package com.github.pwharned.archipelago.backend.modules
import cats.effect.{IO, Ref, Resource}
class DatabaseModule(
  val counter: Ref[IO, Int],
  val name:    Ref[IO, String]
)
object DatabaseModule:
  def make: Resource[IO, DatabaseModule] =
    for
      counter <- Resource.eval(Ref.of[IO, Int](0))
      name    <- Resource.eval(Ref.of[IO, String](""))
    yield DatabaseModule(counter, name)
