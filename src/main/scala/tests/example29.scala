package tests

import rx.Var


object example29 extends App{
  import rx.async._
  import rx.async.Platform._
  import scala.concurrent.duration._

  val a = Var(10)
  val b = a.delay(250 millis)

  a() = 5
  println(b.now) // 10
  //eventually{
    println(b.now) // 5
  //}

  a() = 4
  println(b.now) // 5
  //eventually{
    println(b.now) // 4
  //}
}
