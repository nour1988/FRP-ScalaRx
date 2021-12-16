package tests

  object example21 extends App{
    import rx._
    val a = Var(10)
    val b = for {
      aa <- a
      bb <- Rx { a() + 5}
      cc <- Var(1).map(_*2)
    } yield {
      aa + bb + cc
}
  }
