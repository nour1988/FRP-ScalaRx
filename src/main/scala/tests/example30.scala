package tests

import rx.Var

object example30 extends App {
  import rx.async._
  import rx.async.Platform._
  import scala.concurrent.duration._

  val a = Var(10)
  val b = a.debounce(200 millis)
  a() = 5
  println(b.now) // 5

  a() = 2
  println(b.now) // 5

  //eventually{
    println(b.now) // 2
 // }

  a() = 1
  println(b.now) // 2

  //eventually{
    println(b.now) // 1
  //}

}
