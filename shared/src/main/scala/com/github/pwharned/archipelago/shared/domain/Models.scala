package com.github.pwharned.archipelago.shared.domain

import com.github.plokhotnyuk.jsoniter_scala.macros.*
import com.github.plokhotnyuk.jsoniter_scala.core.*
case class User(id: String, username: String)
object User:
  given JsonValueCodec[User] = JsonCodecMaker.make
