package tests

import rx._
import scala.util.Failure


object example10 extends App{

  val a = Var(1)
  val b = Rx{ 1 / a() }
  println(b.now) // 1
  println(b.toTry) // Success(1)
  a() = 0


}
