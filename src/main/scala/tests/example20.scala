package tests

object example20 extends App{
  import rx._
  val a = Var(10)
  val b = Var(1)
  val c = a.flatMap(a => Rx { a*b() })
  println(c.now) // 10
  b() = 2
  println(c.now) // 20
}
