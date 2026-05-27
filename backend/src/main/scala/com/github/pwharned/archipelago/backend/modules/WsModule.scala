package com.github.pwharned.archipelago.backend.modules
import cats.effect.{IO, Ref, Resource}
import fs2.{Pipe, Stream}
import fs2.concurrent.Topic
import org.http4s.websocket.WebSocketFrame
import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import com.github.pwharned.archipelago.shared.protocol.*
import com.github.pwharned.archipelago.backend.MessageHandler
class WsModule(
  handler: MessageHandler[IO, ClientMessage, ServerMessage],
  topic:   Topic[IO, ServerMessage],
  clients: Ref[IO, Int]
):
  val send: Stream[IO, WebSocketFrame] =
    Stream.eval(clients.update(_ + 1)).drain ++
      topic
        .subscribe(100)
        .map(msg => WebSocketFrame.Text(writeToString(msg)))
        .onFinalize(clients.update(_ - 1))
  val receive: Pipe[IO, WebSocketFrame, Unit] = _.evalMap:
    case WebSocketFrame.Text(text, _) =>
      IO(readFromString[ClientMessage](text))
        .flatMap(handler.handle)
        .flatMap(topic.publish1(_).void)
        .handleErrorWith(e => IO(println(s"WS error: ${e.getMessage}")))
    case WebSocketFrame.Close(_) => IO.unit
    case _                       => IO.unit
object WsModule:
  def make(handler: MessageHandler[IO, ClientMessage, ServerMessage]): Resource[IO, WsModule] =
    for
      topic   <- Resource.eval(Topic[IO, ServerMessage])
      clients <- Resource.eval(Ref.of[IO, Int](0))
    yield WsModule(handler, topic, clients)
