package tests

object example12 extends App{
  import rx._

  var fakeTime = 123
  trait WebPage{
    def fTime = fakeTime
    val time = Var(fTime)
    def update(): Unit  = time() = fTime
    val html: Rx[String]
  }
  class HomePage(implicit ctx: Ctx.Owner) extends WebPage {
    val html = Rx{"Home Page! time: " + time()}
  }
  class AboutPage(implicit ctx: Ctx.Owner) extends WebPage {
    val html = Rx{"About Me, time: " + time()}
  }

  val url = Var("www.mysite.com/home")
  val page = Rx{
    url() match{
      case "www.mysite.com/home" => new HomePage()
      case "www.mysite.com/about" => new AboutPage()
    }
  }

  println(page.now.html.now) // "Home Page! time: 123"

  fakeTime = 234
  page.now.update()
  println(page.now.html.now) // "Home Page! time: 234"

  fakeTime = 345
  url() = "www.mysite.com/about"
  println(page.now.html.now) // "About Me, time: 345"

  fakeTime = 456
  page.now.update()
  println(page.now.html.now) // "About Me, time: 456"


}
