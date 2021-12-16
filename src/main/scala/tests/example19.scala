package tests

object example19 extends App{
  import rx._
  val a = Var(10)
  val b = Rx{ a() + 2 }
  val c = a.map(_*2)
  val d = b.map(_+3)
  println(c.now) // 20
  println(d.now) // 15
  a() = 1
  println(c.now) // 2
  println(d.now) // 6

}
