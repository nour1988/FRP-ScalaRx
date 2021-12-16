package tests

object example11 extends App{
  import rx._
  val a = Var(1)
  val b = Rx{
    (Rx{ a() }, Rx{ math.random })
  }
  val r = b.now._2.now
  a() = 2
  println(b.now._2.now) // r


}
