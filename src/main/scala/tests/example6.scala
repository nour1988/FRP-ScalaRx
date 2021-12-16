package tests

object example6 extends App{
  import rx._

  val a = Var(1)
  var count = 0
  val o = a.foreach{ x =>
    count = x + 1
  }
  println(count) // 2
  a() = 4
  println(count) // 5

}
