package tests

object example23 extends App{
  import rx._
  val a = Var(2)
  val b = a.filter(_ > 5)
  println(b.now)

}
