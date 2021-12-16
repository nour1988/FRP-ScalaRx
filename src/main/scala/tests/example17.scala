package tests

object example17 extends App{
  import rx._

  val a = Var(1); val b = Var(2)
  val c = Rx{ a.now + b.now } //not a very useful `Rx`
  println(c.now) // 3
  a() = 4
  println(c.now) // 3
  b() = 5
  println(c.now) // 3
}
