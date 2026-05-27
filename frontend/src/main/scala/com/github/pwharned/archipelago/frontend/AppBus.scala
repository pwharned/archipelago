package com.github.pwharned.archipelago.frontend
import com.raquo.laminar.api.L.*
import com.github.pwharned.archipelago.shared.protocol.*
enum ConnectionState:
  case Connecting
  case Connected
  case Reconnecting
object AppBus:
  val outgoing:        EventBus[ClientMessage] = new EventBus
  val incoming:        EventBus[ServerMessage] = new EventBus
  val connectionState: Var[ConnectionState]    = Var(ConnectionState.Connecting)


