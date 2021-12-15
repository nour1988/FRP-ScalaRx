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
