package tests

object example25 extends App{
  import rx._
  val a = Var(1)
  val b = a.fold(List.empty[Int])((acc,elem) => elem :: acc)
  a() = 2
  println(b.now) // List(2,1)
  a() = 3
  println(b.now) // List(3,2,1)
  a() = 4
  println(b.now) // List(4,3,2,1)

}
