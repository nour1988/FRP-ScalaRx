package tests


object example28 extends App{
  import rx.async._
  import rx.async.Platform._
  import scala.concurrent.duration._

  val t = Timer(100 millis)
  var count = 0
  val o = t.trigger {
    count = count + 1
  }

  println(count) // 3
  //println(count) // 8
  //println(count) // 13

}
