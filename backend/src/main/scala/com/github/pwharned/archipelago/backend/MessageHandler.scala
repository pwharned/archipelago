package com.github.pwharned.archipelago.backend

import com.github.pwharned.archipelago.shared.protocol.Delivery
trait MessageHandler[F[_], In, Out]:
  def handle(in: In): F[Delivery[Out]]
