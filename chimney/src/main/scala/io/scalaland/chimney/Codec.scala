package io.scalaland.chimney

final case class Codec[Domain, Dto](encode: Transformer[Domain, Dto], decode: PartialTransformer[Dto, Domain])
    extends Codec.AutoDerived[Domain, Dto]
object Codec {

  def derive[Domain, Dto](implicit
      encode: Transformer.AutoDerived[Domain, Dto],
      decode: PartialTransformer.AutoDerived[Dto, Domain]
  ): Codec[Domain, Dto] = Codec[Domain, Dto](encode = safeUpcast[Domain, Dto], decode = safeUpcastPartial[Dto, Domain])

  // TODO: define

  private def safeUpcast[From, To](implicit t: Transformer.AutoDerived[From, To]): Transformer[From, To] =
    t match {
      case _: Transformer[?, ?] => t.asInstanceOf[Transformer[From, To]]
      case _                    => (src: From) => t.transform(src)
    }

  private def safeUpcastPartial[From, To](implicit
      t: PartialTransformer.AutoDerived[From, To]
  ): PartialTransformer[From, To] =
    t match {
      case _: PartialTransformer[?, ?] => t.asInstanceOf[PartialTransformer[From, To]]
      case _                           => (src: From, failFast: Boolean) => t.transform(src, failFast)
    }

  trait AutoDerived[Domain, Dto] {
    val encode: Transformer[Domain, Dto]
    val decode: PartialTransformer[Dto, Domain]
  }
  object AutoDerived {

    implicit def derive[Domain, Dto](implicit
        encode: Transformer.AutoDerived[Domain, Dto],
        decode: PartialTransformer.AutoDerived[Dto, Domain]
    ): Codec.AutoDerived[Domain, Dto] = Codec.derive(encode, decode)
  }
}
