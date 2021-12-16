package tests


object example27 extends App{

  import scala.concurrent.Promise
  import scala.concurrent.ExecutionContext.Implicits.global
  import rx.async._
  import rx._


  var p = Promise[Int]()
  val a = Var(1)

  val b: Rx[Int] = Rx {
    val f =  p.future.toRx(10)
    f() + a()
  }
  println(b.now) //11
  p.success(5)
  println(b.now) //6

  p = Promise[Int]()
  a() = 2
  println(b.now) //12

  p.success(7)
  println(b.now) //9
}
