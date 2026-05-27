package com.github.pwharned.archipelago.shared.protocol
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import com.github.plokhotnyuk.jsoniter_scala.core.*
sealed trait ClientMessage
object ClientMessage:
  case object Increment              extends ClientMessage
  case object Decrement              extends ClientMessage
  case object Reset                  extends ClientMessage
  case class SubmitName(name: String) extends ClientMessage
  given JsonValueCodec[ClientMessage] = JsonCodecMaker.make
sealed trait ServerMessage
object ServerMessage:
  case class CountUpdated(value: Int)       extends ServerMessage
  case class Error(msg: String)             extends ServerMessage
  case class SubmitNameAccepted(name: String) extends ServerMessage
  given JsonValueCodec[ServerMessage] = JsonCodecMaker.make

