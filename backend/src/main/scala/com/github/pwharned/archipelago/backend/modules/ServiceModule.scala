package com.github.pwharned.archipelago.backend.modules
import cats.effect.{ IO, Resource }
import com.github.pwharned.archipelago.shared.protocol.*
import com.github.pwharned.archipelago.backend.services._
import com.github.pwharned.archipelago.backend.MessageHandler
class ServiceModule(
  counter: CounterService,
  name: NameService
) extends MessageHandler[IO, ClientMessage, ServerMessage]:
  def handle(msg: ClientMessage): IO[Delivery[ServerMessage]] =
    msg match
      case msg: ClientMessage.Increment.type => counter.handle(msg)
      case msg: ClientMessage.Decrement.type => counter.handle(msg)
      case msg: ClientMessage.Reset.type     => counter.handle(msg)
      case msg: ClientMessage.SubmitName     => name.handle(msg)
object ServiceModule:
  def make(db: DatabaseModule): Resource[IO, ServiceModule] =
    for
      counter <- CounterService.make(db)
      name <- NameService.make(db)
    yield ServiceModule(counter, name)
