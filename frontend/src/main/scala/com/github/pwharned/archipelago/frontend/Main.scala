

package com.github.pwharned.archipelago.frontend
import scala.scalajs.js.annotation.JSExportTopLevel
import com.raquo.laminar.api.L.*
object Main:
  @JSExportTopLevel("init", moduleID = "main")
  def init(): Unit =
    WsClient.connect()
