package tests

object example14 extends App{
  import rx._
  var count = 0
  val a = Var(1); val b = Var(2)
  def mkRx(i: Int) = Rx.unsafe { count += 1; i + b() }
  val c = Rx{
    val newRx = mkRx(a())
    newRx()
  }
  println(c.now, count) //(3,1)
  a() = 4
  println(c.now, count) //(6,2)

}
