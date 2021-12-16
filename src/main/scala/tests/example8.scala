package tests

object example8 extends App{
  import rx._

  val a = Var(1)
  val b = Rx{ 2 * a() }
  var target = 0
  val o = b.trigger {
    target = b.now
  }
  println(target) // 2
  a() = 2
  println(target) // 4
  o.kill()
  a() = 3
  println(target) // 4

}
