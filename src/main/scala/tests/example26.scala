package tests

object example26 extends App{

  import scala.concurrent.Promise
  import scala.concurrent.ExecutionContext.Implicits.global
  import rx.async._

  val p = Promise[Int]()
  val a = p.future.toRx(10)
  println(a.now) //10
  p.success(5)
  println(a.now) //5
}
