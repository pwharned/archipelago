package com.github.pwharned.archipelago.backend.services
import cats.effect.{ IO, Resource }
import com.github.pwharned.archipelago.backend.modules._
import com.github.pwharned.archipelago.shared.protocol.*
class CounterService(db: DatabaseModule):
  def handle(msg: ClientMessage.Increment.type): IO[Delivery[ServerMessage]] =
    db.counter.updateAndGet(_ + 1).map(x => Delivery.Unicast(ServerMessage.CountUpdated(x)))
  def handle(msg: ClientMessage.Decrement.type): IO[Delivery[ServerMessage]] =
    db.counter.updateAndGet(_ - 1).map(x => Delivery.Broadcast(ServerMessage.CountUpdated(x)))
  def handle(msg: ClientMessage.Reset.type): IO[Delivery[ServerMessage]] =
    db.counter.set(0).as(Delivery.Unicast(ServerMessage.CountUpdated(0)))
object CounterService:
  def make(db: DatabaseModule): Resource[IO, CounterService] =
    Resource.pure(CounterService(db))
