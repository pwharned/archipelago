package com.github.pwharned.archipelago.frontend.islands
import com.raquo.laminar.api.L.*
import org.scalajs.dom
import scala.scalajs.js.annotation.JSExportTopLevel
import com.github.pwharned.archipelago.shared.protocol.*
import com.github.pwharned.archipelago.frontend.AppBus
object SubmitIsland:
  @JSExportTopLevel("mountSubmit", moduleID = "submit")
  def mount(el: dom.Element): Unit =
    val nameVar = Var("")
    val greetingVar = Var("")
    val test = AppBus.incoming.events
      .collect { case ServerMessage.SubmitNameAccepted(v) => greetingVar.set("Hello " + v + "!") }
      .foreach(print)(using unsafeWindowOwner)
    // .collect { case ServerMessage.SubmitNameAccepted(v) => v }
    // .foreach(nameVar.set)(using unsafeWindowOwner)
    val island = div(
      h2("Submit your name"),
      p(
        text <-- greetingVar
      ),
      div(
        input(
          controlled(value <-- nameVar.signal, onInput.mapToValue --> nameVar.writer)
        )
      ),
      button("Submit", onClick --> (_ => AppBus.outgoing.emit(ClientMessage.SubmitName(nameVar.signal.now()))))
    )
    render(el, island)
