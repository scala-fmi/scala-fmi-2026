package fmi.utils

import io.circe.{Codec, Decoder, Encoder}

object CirceUtils:
  def unwrappedCodec[W, U : {Encoder, Decoder}](wrap: U => W)(unwrap: W => U): Codec[W] =
    Codec.implied.iemap(u => Right(wrap(u)))(unwrap)
