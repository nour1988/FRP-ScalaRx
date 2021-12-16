package tests

object example24 extends App{
  import rx._
  val a = Var(1)
  val b = a.reduce(_ * _)
  a() = 2
  println(b.now) // 2
  a() = 3
  println(b.now) // 6
  a() = 4
  println(b.now) // 24

}
