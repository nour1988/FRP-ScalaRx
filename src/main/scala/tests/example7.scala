package tests

object example7 extends App{
  import rx._

  val a = Var(1)
  var count = 0
  val o = a.triggerLater {
    count = count + 1
  }
  println(count) // 0
  a() = 2
  println(count) // 1

}
