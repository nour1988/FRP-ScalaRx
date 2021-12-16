package tests

object example22 extends App{
  import rx._
  val a = Var(10)
  val b = a.filter(_ > 5)
  a() = 1
  println(b.now) // 10
  a() = 6
  println(b.now) // 6
  a() = 2
  println(b.now) // 6
  a() = 19
  println(b.now) // 19

}
