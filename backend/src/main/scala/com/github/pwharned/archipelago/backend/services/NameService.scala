package com.github.pwharned.archipelago.backend.services
import cats.effect.{ IO, Resource }
import com.github.pwharned.archipelago.backend.modules.DatabaseModule
import com.github.pwharned.archipelago.shared.protocol.*
class NameService(db: DatabaseModule):
  def handle(msg: ClientMessage.SubmitName): IO[ServerMessage] =
    db.name.updateAndGet(x => msg.name).map(msg => ServerMessage.SubmitNameAccepted(msg))
object NameService:
  def make(db: DatabaseModule): Resource[IO, NameService] =
    Resource.pure(NameService(db))
