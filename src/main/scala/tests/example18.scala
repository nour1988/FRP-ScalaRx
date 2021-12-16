package tests

object example18 extends App{
  import rx._

  /* error: No implicit Ctx.Data is available here!
  def foo()(implicit ctx: Ctx.Owner) = {
    val a = rx.Var(1)
    a()
    a
  }
  val x = rx.Rx{val y = foo(); y() = y() + 1; println("done!") }
*/

  def foo()(implicit ctx: Ctx.Owner, data: Ctx.Data) = {
    val a = rx.Var(1)
    a()
    a
  }
  val x = rx.Rx{val y = foo(); y() = y() + 1; println("done!") }

}
