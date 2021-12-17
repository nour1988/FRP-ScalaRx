package tests

object example05 extends App{
  import rx._

  val a = Var(1)
  var count = 0
  val o = a.trigger {
    count = a.now + 1
  }
  println(count) // 2
  a() = 4
  println(count) // 5

}
