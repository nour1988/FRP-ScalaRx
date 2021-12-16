package tests

object example9 extends App{
  import rx._

  val a = Var(Seq(1, 2, 3))
  val b = Var(3)
  val c = Rx{ b() +: a() }
  val d = Rx{ c().map("omg" * _) }
  val e = Var("wtf")
  val f = Rx{ (d() :+ e()).mkString }

  println(f.now) // "omgomgomgomgomgomgomgomgomgwtf"
  a() = Nil
  println(f.now) // "omgomgomgwtf"
  e() = "wtfbbq"
  println(f.now) // "omgomgomgwtfbbq"

}
