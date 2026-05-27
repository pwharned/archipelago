package com.github.pwharned.archipelago.frontend.islands
import com.raquo.laminar.api.L.*
import org.scalajs.dom
import scala.scalajs.js.annotation.JSExportTopLevel
import com.github.pwharned.archipelago.shared.protocol.*
import com.github.pwharned.archipelago.frontend.AppBus
object CounterIsland:
  @JSExportTopLevel("mountCounter", moduleID = "counter")
  def mount(el: dom.Element): Unit =
    val countVar = Var(0)
    val _ = AppBus.incoming.events.collect:
      case ServerMessage.CountUpdated(v) => v
    .foreach(countVar.set)(using unsafeWindowOwner)
    val island = div(
      h2("Counter"),
      div(
        button("-", onClick --> (_ => AppBus.outgoing.emit(ClientMessage.Decrement))),
        span(child.text <-- countVar.signal.map(_.toString)),
        button("+", onClick --> (_ => AppBus.outgoing.emit(ClientMessage.Increment)))
      ),
      button("Reset", onClick --> (_ => AppBus.outgoing.emit(ClientMessage.Reset)))
    )
    render(el, island)

