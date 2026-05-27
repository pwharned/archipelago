package com.github.pwharned.archipelago.backend
trait MessageHandler[F[_], In, Out]:
  def handle(in: In): F[Out]
