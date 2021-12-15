# Scala.Rx 0.4.1
Scala.Rx è una libreria di propagazione del cambiamento per Scala. Scala.Rx fornisce le variabili reattive (Rxs), che sono variabili intelligenti che si aggiornano automaticamente quando i valori da cui dipendono cambiano. L'implementazione sottostante è un FRP basato su push basato sulle idee in Deprecating the Observer Pattern.   
Un semplice esempio che dimostra il comportamento è:  
```
import rx._
val a = Var(1); val b = Var(2) 
val c = Rx{ a() + b() }
println(c.now) // 3
a() = 4
println(c.now) // 6 
```
L'idea è che il 99% delle volte, quando ricalcoli una variabile, la ricalcoli nello stesso modo in cui l'hai calcolata inizialmente. Inoltre, lo si ricalcola solo quando uno dei valori da cui dipende cambia. Scala.Rx lo fa automaticamente per te e gestisce tutta la noiosa logica di aggiornamento per consentirti di concentrarti su altre cose più interessanti!

Oltre alla propagazione delle modifiche di base, Scala.Rx fornisce una serie di altre funzionalità, come un set di combinatori per costruire facilmente il grafico del flusso di dati, controlli del tempo di compilazione per un alto grado di correttezza e interoperabilità senza soluzione di continuità con il codice Scala esistente. Ciò significa che può essere facilmente incorporato in un'applicazione Scala esistente.

## Getting Started
Scala.Rx è disponibile su Maven Central. Per iniziare, aggiungi semplicemente quanto segue al tuo `build.sbt`:
```
libraryDependencies += "com.lihaoyi" %% "scalarx" % "0.4.1"
```
Successivamente, aprire la console sbt e incollare l'esempio sopra nella console dovrebbe funzionare.
## ScalaJS
Oltre a funzionare sulla JVM, Scala.Rx compila anche su Scala-Js. Questo artefatto è attualmente su Maven Central e può essere utilizzato tramite il seguente frammento SBT:  
```
libraryDependencies += "com.lihaoyi" %%% "scalarx" % "0.4.1"
```
Ci sono alcune piccole differenze tra l'esecuzione di Scala.Rx sulla JVM e in Javascript, in particolare per quanto riguarda le operazioni asincrone, il modello di parallelismo e il modello di memoria. In generale, tuttavia, tutti gli esempi forniti nella documentazione di seguito funzioneranno perfettamente se compilati in modo incrociato in javascript ed eseguiti nel browser.

Scala.rx 0.4.1 è compatibile solo con ScalaJS 0.6.5+.
## Utilizzo di Scala.Rx
Le operazioni primarie richiedono solo `import rx._` prima di essere utilizzate, con operazioni aggiuntive che richiedono anche `import rx.ops._`. Alcuni degli esempi seguenti utilizzano anche varie importazioni da `scala.concurrent` o `scalatest`.
###  Utilizzo di base
```
import rx._

val a = Var(1); val b = Var(2)
val c = Rx{ a() + b() }
println(c.now) // 3
a() = 4
println(c.now) // 6
```
L'esempio sopra è un programma eseguibile. In generale, `import rx._` è sufficiente per iniziare con `Scala.Rx` e verrà assunto in tutti gli altri esempi.

Le entità di base di cui devi preoccuparti sono Var, Rx e Obs:
- `Var`: una variabile intelligente che puoi ottenere usando a() e impostare usando a() = .... Ogni volta che il suo valore cambia, esegue il ping di qualsiasi entità a valle che deve essere ricalcolata.
- `Rx`: una definizione reattiva che cattura automaticamente qualsiasi Var o altro Rx che viene chiamato nel suo corpo, contrassegnandoli come dipendenze e ricalcolandoli ogni volta che uno di essi cambia. Come un Var, puoi usare la sintassi a() per recuperare il suo valore, ed esegue anche il ping delle entità a valle quando il valore cambia.
- `Obs`: un osservatore su uno o più Var s o Rx s, che esegue qualche effetto collaterale quando il nodo osservato cambia valore e gli invia un ping.

Utilizzando questi componenti, è possibile costruire facilmente un grafico del flusso di dati e mantenere aggiornati i vari valori all'interno del grafico del flusso di dati quando gli input al grafico cambiano:
```
val a = Var(1) // 1

val b = Var(2) // 2

val c = Rx{ a() + b() } // 3
val d = Rx{ c() * 5 } // 15
val e = Rx{ c() + 4 } // 7
val f = Rx{ d() + e() + 4 } // 26

println(f.now) // 26
a() = 3
println(f.now) // 38
```
Il grafico del flusso di dati per questo programma ha il seguente aspetto:
![image](https://user-images.githubusercontent.com/63450698/146184977-c17ca96d-f073-445b-9257-87bf40fb653f.png)

Dove le Var sono rappresentate da quadrati, le Rx da cerchi e le dipendenze da frecce. Ogni Rx è etichettato con il suo nome, il suo corpo e il suo valore.
La modifica del valore di `a` fa sì che le modifiche si propaghino attraverso il grafico del flusso di dati
![image](https://user-images.githubusercontent.com/63450698/146185165-3785090c-726b-4a0e-b0b9-1dd25679c418.png)

Come si può vedere sopra, la modifica del valore di `a` fa sì che la modifica si propaghi attraverso `c` `d` `e` fino a `f`. Puoi usare Var e Rx ovunque tu usi una variabile normale.

Le modifiche si propagano attraverso il grafico del flusso di dati in onde. Ogni aggiornamento di un Var innesca una propagazione, che spinge le modifiche da quel Var `a` qualsiasi Rx che è (direttamente o indirettamente) dipendente dal suo valore. Nel processo, è possibile ricalcolare una Rx più di una volta.

### Observers
Come accennato, gli Obs s possono essere creati da Rx s o Var s ed essere utilizzati per eseguire effetti collaterali quando cambiano:
```
val a = Var(1)
var count = 0
val o = a.trigger {
  count = a.now + 1
}
println(count) // 2
a() = 4
println(count) // 5
```
Questo crea un grafico del flusso di dati che assomiglia a:
![image](https://user-images.githubusercontent.com/63450698/146186212-8b8d325f-98a9-4455-9039-16a1e3504b8c.png)

Quando `a` viene modificato, l'osservatore `o` eseguirà l'effetto collaterale:
![image](https://user-images.githubusercontent.com/63450698/146186298-5c027b9e-92c9-4c94-aab9-ba3f8fd2aaec.png)

Il corpo di Rxs dovrebbe essere privo di effetti collaterali, poiché possono essere eseguiti più di una volta per propagazione. Dovresti usare Obs s per eseguire i tuoi effetti collaterali, poiché è garantito che vengano eseguiti solo una volta per propagazione dopo che i valori per tutti gli Rx si sono stabilizzati.

Scala.Rx fornisce un comodo combinatore `.foreach()`, che fornisce un modo alternativo per creare un Obs da un Rx:
```
val a = Var(1)
var count = 0
val o = a.foreach{ x =>
  count = x + 1
}
println(count) // 2
a() = 4
println(count) // 5
```
Questo esempio fa la stessa cosa del codice sopra.

Nota che il corpo degli Obs viene eseguito una volta inizialmente quando viene dichiarato. Ciò corrisponde al modo in cui ogni Rx viene calcolato una volta quando viene dichiarato inizialmente. ma è ipotizzabile che si voglia un Obs che si attivi per la prima volta solo quando l'Rx che sta ascoltando cambia. Puoi farlo usando la sintassi alternativa `triggerLater`:
```
val a = Var(1)
var count = 0
val o = a.triggerLater {
  count = count + 1
}
println(count) // 0
a() = 2
println(count) // 1
```
Un Obs agisce per incapsulare il callback che esegue. Possono essere passati in giro, archiviati in variabili, ecc. Quando Obs viene raccolto, il callback smetterà di attivarsi. Pertanto, un Obs dovrebbe essere memorizzato nell'oggetto che interessa: se il callback riguarda solo quell'oggetto, non importa quando l'Obs stesso viene raccolto spazzatura, poiché accadrà solo dopo che l'oggetto che lo tiene diventa irraggiungibile, nel qual caso i suoi effetti non possono essere osservati comunque. Un Obs può anche essere disattivato attivamente, se è necessaria una garanzia più forte:
```
val a = Var(1)
val b = Rx{ 2 * a() }
var target = 0
val o = b.trigger {
  target = b.now
}
println(target) // 2
a() = 2
println(target) // 4
o.kill()
a() = 3
println(target) // 4
```
Dopo aver chiamato manualmente `.kill()`, Obs non si attiva più. Oltre a `.kill()`ing Obss, puoi anche uccidere Rxs, che impedisce ulteriori aggiornamenti.

In generale, Scala.Rx ruota attorno alla costruzione di grafici del flusso di dati che mantengono automaticamente le cose sincronizzate, con cui puoi interagire facilmente da un codice imperativo esterno. Ciò comporta l'utilizzo di:
- Vars come input al grafico del flusso di dati dal mondo imperativo
- Rxs come nodi intermedi nei grafici del flusso di dati
- Obss come output dal grafico del flusso di dati nel mondo imperativo

### Complex Reactives

Le Rx non sono limitate a `Int`s. `String`s, `Seq[Int]`s, `Seq[String]`s, qualsiasi cosa può andare all'interno di un Rx:
```
val a = Var(Seq(1, 2, 3))
val b = Var(3)
val c = Rx{ b() +: a() }
val d = Rx{ c().map("omg" * _) }
val e = Var("wtf")
val f = Rx{ (d() :+ e()).mkString }

println(f.now) // "omgomgomgomgomgomgomgomgomgwtf"
a() = Nil
println(f.now) // "omgomgomgwtf"
e() = "wtfbbq"
println(f.now) // "omgomgomgwtfbbq"
```
### Error Handling
Poiché il corpo di un Rx può essere qualsiasi codice Scala arbitrario, può generare eccezioni. Propagare l'eccezione nello stack di chiamate non avrebbe molto senso, poiché il codice che valuta l'Rx probabilmente non ha il controllo del motivo per cui non è riuscito. Invece, qualsiasi eccezione viene rilevata dalla stessa Rx e memorizzata internamente come `Try`.

Questo può essere visto nel seguente unit test:
```
val a = Var(1)
val b = Rx{ 1 / a() }
println(b.now) // 1
println(b.toTry) // Success(1)
a() = 0
intercept[ArithmeticException]{
  b()
}
assert(b.toTry.isInstanceOf[Failure])
```
Inizialmente, il valore di `a` è `1` e quindi anche il valore di `b` è `1`. Puoi anche estrarre il `Try` interno usando `b.toTry`, che all'inizio è `Success(1)`.

Tuttavia, quando il valore di `a` diventa `0`, il corpo di `b` genera un'ArithmeticException. Questo viene catturato da `b` e rilanciato se si tenta di estrarre il valore da `b` usando `b()`. È possibile estrarre l'intero `Try` usando `toTry` e il pattern match su di esso per gestire sia il caso `Success` che il caso `Failure`.

Quando si hanno molti Rx concatenati, le eccezioni si propagano in avanti seguendo il grafico delle dipendenze, come ci si aspetterebbe. Il seguente codice:
```
val a = Var(1)
val b = Var(2)

val c = Rx{ a() / b() }
val d = Rx{ a() * 5 }
val e = Rx{ 5 / b() }
val f = Rx{ a() + b() + 2 }
val g = Rx{ f() + c() }

inside(c.toTry){case Success(0) => () }
inside(d.toTry){case Success(5) => () }
inside(e.toTry){case Success(2) => () }
inside(f.toTry){case Success(5) => () }
inside(g.toTry){case Success(5) => () }

b() = 0

inside(c.toTry){case Failure(_) => () }
inside(d.toTry){case Success(5) => () }
inside(e.toTry){case Failure(_) => () }
inside(f.toTry){case Success(3) => () }
inside(g.toTry){case Failure(_) => () }
```
Crea un grafico delle dipendenze simile al seguente:
![image](https://user-images.githubusercontent.com/63450698/146188717-64e1882e-6d5b-4702-b3fc-cab3a88c9009.png)

In questo esempio, inizialmente tutti i valori per `a`, `b`, `c`, `d`, `e`, `f` e `g` sono ben definiti. Tuttavia, quando `b` è impostato su `0`:
![image](https://user-images.githubusercontent.com/63450698/146188917-b34b9ddd-f2d9-4880-9c98-4e2c9021a578.png)

`c` ed `e` risultano entrambi in eccezioni e l'eccezione da `c` si propaga a `g`. Il tentativo di estrarre il valore da g utilizzando `g.now`, ad esempio, genererà nuovamente l'ArithmeticException. Anche in questo caso, l'utilizzo di `toTry` funziona.
### Nesting
Le Rx possono contenere altre Rx, arbitrariamente in profondità. Questo esempio mostra gli Rx annidati a due livelli di profondità:
```
val a = Var(1)
val b = Rx{
    (Rx{ a() }, Rx{ math.random })
}
val r = b.now._2.now
a() = 2
println(b.now._2.now) // r
```
In questo esempio, possiamo vedere che, sebbene abbiamo modificato `a`, questo riguarda solo l'Rx interno sinistro, né l'Rx interno destro (che assume un valore diverso e casuale ogni volta che viene ricalcolato) o l'Rx esterno ( che farebbe ricalcolare l'intera cosa) sono interessati. Un esempio un po' meno forzato potrebbe essere:
```
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
```
In questo caso, definiamo una pagina web che ha un valore `html` (a `Rx[String]`). Tuttavia, a seconda dell'`URL`, potrebbe essere una `HomePage` o una `AboutPage`, quindi il nostro `page` oggetto  è un `Rx[WebPage]`.

Avere una `Rx[WebPage]`, dove la `WebPage` ha una `Rx[String]` all'interno, sembra naturale e ovvio, e Scala.Rx ti permette di farlo in modo semplice e naturale. Questo tipo di situazione di oggetti all'interno degli oggetti si presenta in modo molto naturale quando si modella un problema in modo orientato agli oggetti. La capacità di Scala.Rx di gestire con grazia i corrispondenti Rx all'interno di Rxs gli consente di adattarsi con grazia a questo paradigma.
 ## Ownership Context
 Nell'ultimo esempio sopra, abbiamo dovuto introdurre il concetto di proprietà in cui viene utilizzato `Ctx.Owner`. Infatti, se tralasciamo `(implicit ctx: Ctx.Owner)`, otterremmo il seguente errore di compilazione:
 ```
 error: This Rx might leak! Either explicitly mark it unsafe (Rx.unsafe) or ensure an implicit RxCtx is in scope!
           val html = Rx{"Home Page! time: " + time()}
```
Per capire la `ownership` è importante capire il problema che risolve: `leaks`. Ad esempio, considera questa leggera modifica al primo esempio:
```
var count = 0
val a = Var(1); val b = Var(2)
def mkRx(i: Int) = Rx.unsafe { count += 1; i + b() }
val c = Rx{ 
  val newRx = mkRx(a()) 
  newRx() 
}
println(c.now, count) //(3,1)
```
In questa versione è stata aggiunta la funzione `mkRx`, ma per il resto il valore calcolato di `c` rimane invariato. E la modifica di `a` sembra comportarsi come previsto:
```
a() = 4
println(c.now, count) //(6,2)
```
Ma se modifichiamo `b` potremmo iniziare a notare qualcosa che non va:
```
b() = 3 
println(c.now, count) //(7,5) -- 5??

(0 to 100).foreach { i => a() = i }
println(c.now, count) //(103,106)

b() = 4
println(c.now, count) //(104,211) -- 211!!!
```
In questo esempio, anche se `b` viene aggiornato solo poche volte, il valore del conteggio inizia a salire man mano che a viene modificato. Questo è `mkRx` che perde! Cioè, ogni volta che `c` viene ricalcolato, costruisce un `Rx` completamente nuovo che rimane e continua a valutare, anche dopo che non è più raggiungibile come dipendenza dai dati e dimenticato. Quindi, dopo aver eseguito quell'istruzione `(da 0 a 100).foreach`, ci sono oltre 100 `Rx` che si attivano tutti ogni volta che `b` viene modificato. Questo chiaramente non è desiderabile.

Tuttavia, aggiungendo un esplicito `owner` (e rimuovendo `unsafe`), possiamo correggere la perdita:
```
var count = 0
val a = Var(1); val b = Var(2)
def mkRx(i: Int)(implicit ctx: Ctx.Owner) = Rx { count += 1; i + b() }
val c = Rx{ 
  val newRx = mkRx(a()) 
  newRx() 
}
println(c.now,count) // (3,1)
a() = 4
println(c.now,count) // (6,2)
b() = 3
println(c.now,count) // (7,4)
(0 to 100).foreach { i => a() = i }
println(c.now,count) //(103,105)
b() = 4
println(c.now,count) //(104,107)
```
L’ownership corregge le perdite consentendo a un genitore `Rx` di tenere traccia della sua `Rx` nidificata "owned". Cioè ogni volta che un `Rx` ricacola, prima uccide tutte le sue dipendenze di proprietà, assicurandosi che non perdano. In questo esempio, `c` è il proprietario di tutti gli `Rx` creati in `mkRx` e li uccide automaticamente ogni volta che `c` ricalcola.
  ##  Data Context
Dato un Rx o un Var usando `()` (aka `apply`) scarta il valore corrente e si aggiunge come dipendenza a qualunque `Rx` che sta attualmente valutando. In alternativa, `.now` può essere utilizzato semplicemente per scartare il valore e saltare la dipendenza dai dati:
```
val a = Var(1); val b = Var(2)
val c = Rx{ a.now + b.now } //not a very useful `Rx`
println(c.now) // 3
a() = 4
println(c.now) // 3 
b() = 5
println(c.now) // 3 
```
Per comprendere la necessità di un `Data` context e in che modo i `Data` contexts differiscono dai `owner` contexts, si consideri il seguente esempio:
```
def foo()(implicit ctx: Ctx.Owner) = {
  val a = rx.Var(1)
  a()
  a
}

val x = rx.Rx{val y = foo(); y() = y() + 1; println("done!") }
```
Con il concetto di proprietà, se `a()` è autorizzato a creare una dipendenza dati dal suo proprietario, entrerebbe in una ricorsione infinita e esploderebbe lo stack! Invece, il codice sopra fornisce questo errore di compilazione:
```
<console>:17: error: No implicit Ctx.Data is available here!
        a()
```
Possiamo "correggere" l'errore consentendo esplicitamente le dipendenze dei dati (e vedere che lo stack esplode):
```
def foo()(implicit ctx: Ctx.Owner, data: Ctx.Data) = {
  val a = rx.Var(1)
  a()
  a
}
val x = rx.Rx{val y = foo(); y() = y() + 1; println("done!") }
...
at rx.Rx$Dynamic$Internal$$anonfun$calc$2.apply(Core.scala:180)
  at scala.util.Try$.apply(Try.scala:192)
  at rx.Rx$Dynamic$Internal$.calc(Core.scala:180)
  at rx.Rx$Dynamic$Internal$.update(Core.scala:184)
  at rx.Rx$.doRecalc(Core.scala:130)
  at rx.Var.update(Core.scala:280)
  at $anonfun$1.apply(<console>:15)
  at $anonfun$1.apply(<console>:15)
  at rx.Rx$Dynamic$Internal$$anonfun$calc$2.apply(Core.scala:180)
  at scala.util.Try$.apply(Try.scala:192)
...
```
Il `Data` context è il meccanismo che un `Rx` utilizza per decidere quando ricacolare. L’ownership risolve il problema delle perdite. Mescolare i due può portare a una ricorsione infinita: quando qualcosa è sia di proprietà che una dipendenza dai dati dello stesso genitore Rx.

Fortunatamente però è quasi sempre il caso che sia necessario solo l'uno o l'altro contesto. quando si tratta di grafici dinamici, è quasi sempre il caso che sia necessario solo il contesto di proprietà, ovvero le funzioni più spesso hanno la forma:
```
def f(...)(implicit ctx: Ctx.Owner) = Rx { ... }
```
Il Data context è necessario meno spesso ed è utile, ad esempio, nel caso in cui sia desiderabile ASCIUGARE del codice Rx ripetuto. Tale funzione avrebbe questa forma:
```
def f(...)(implicit data: Ctx.Data) = ...
```
Ciò consentirebbe di estrarre una certa dipendenza dai dati condivisi dal corpo di ogni `Rx` e nella funzione condivisa.

Suddividendo i concetti ortogonali di `ownership` e `data` dependencies , il problema della ricorsione infinita, come descritto sopra, è notevolmente limitato. Le dipendenze esplicite dei dati rendono anche più chiaro quando l'uso di un `Var` o `Rx` è inteso come una dipendenza dei dati e non solo una semplice lettura del valore corrente (ad esempio `.now`). Senza questa distinzione, è più facile introdurre dipendenze di dati "accidentali" inattese e non volute.
 ##  Operazioni aggiuntive
 Oltre ai mattoni di base di `Var/Rx/Obs`, Scala.Rx fornisce anche un insieme di combinatori che ti permettono di trasformare facilmente i tuoi Rx; ciò consente al programmatore di evitare di riscrivere costantemente la logica per le modalità comuni di costruzione del grafo del flusso di dati. I cinque combinatori di base: `map()`, `flatMap()`, `filter()`, `reduce()` e `fold()` sono tutti modellati sulla libreria delle collezioni scala e forniscono un modo semplice per trasformare i valori che escono da un Rx.
 ### Map
 ```
 val a = Var(10)
val b = Rx{ a() + 2 }
val c = a.map(_*2)
val d = b.map(_+3)
println(c.now) // 20
println(d.now) // 15
a() = 1
println(c.now) // 2
println(d.now) // 6
```
`map` fa quello che ti aspetteresti, creando un nuovo Rx con il valore del vecchio Rx trasformato da qualche funzione. Ad esempio, `a.map(_*2)` è essenzialmente equivalente a `Rx{ a() * 2 }`, ma in qualche modo più comodo da scrivere.
 ### FlatMap
```
val a = Var(10)
val b = Var(1)
val c = a.flatMap(a => Rx { a*b() })
println(c.now) // 10
b() = 2
println(c.now) // 20
```
`flatMap` è analogo a flatMap dalla libreria delle collezioni in quanto consente di unire Rx nidificati di tipo `Rx[Rx[_]]` in un singolo `Rx[_]`.

Questo in combinazione con il map combinator consente alla sintassi di comprensione di scala di funzionare con Rx e Var :
```
val a = Var(10)
val b = for {
  aa <- a
  bb <- Rx { a() + 5}
  cc <- Var(1).map(_*2)
} yield {
  aa + bb + cc
}
```
### Filter
```
val a = Var(10)
val b = a.filter(_ > 5)
a() = 1
println(b.now) // 10
a() = 6
println(b.now) // 6
a() = 2
println(b.now) // 6
a() = 19
println(b.now) // 19
```
Il `filter` ignora le modifiche al valore della Rx che non superano il predicato.

Si noti che nessuno dei metodi di filtro è in grado di filtrare il primo valore iniziale di un Rx, poiché non esiste un valore "più vecchio" a cui ricorrere. Quindi questo:
```
val a = Var(2)
val b = a.filter(_ > 5)
println(b.now)
```
stamperà "2".
### Reduce
```
val a = Var(1)
val b = a.reduce(_ * _)
a() = 2
println(b.now) // 2
a() = 3
println(b.now) // 6
a() = 4
println(b.now) // 24
```
L'operatore di `reduce` combina insieme i valori successivi di un Rx, a partire dal valore iniziale. Ogni modifica alla Rx originale viene abbinata al valore precedentemente memorizzato e diventa il nuovo valore della Rx ridotta.
### Fold
```
val a = Var(1)
val b = a.fold(List.empty[Int])((acc,elem) => elem :: acc)
a() = 2
println(b.now) // List(2,1)
a() = 3
println(b.now) // List(3,2,1)
a() = 4
println(b.now) // List(4,3,2,1)
```
`Fold` consente l'accumulo in modo simile per ridurre, ma può accumularsi in un tipo diverso da quello della sorgente Rx.

Ciascuno di questi cinque combinatori ha una controparte nello spazio dei nomi `.all` che opera su `Try[T]` anziché su T, nel caso in cui sia necessaria la flessibilità aggiuntiva per gestire i `Failure` in qualche modo speciale.
