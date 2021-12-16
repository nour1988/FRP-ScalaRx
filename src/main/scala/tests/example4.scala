package tests

object example4 extends App{
  import rx._

  val a = Var(1)
  println(a.now) // 1

  val b = Var(2)
  println(b.now) // 2

  val c = Rx{ a() + b() }
  println(c.now) // 3

  val d = Rx{ c() * 5 }
  println(d.now)// 15

  val e = Rx{ c() + 4 }
  println(e.now)// 7

  val f = Rx{ d() + e() + 4 }
  println(f.now) // 26

  a() = 3
  println(f.now) // 38
}
