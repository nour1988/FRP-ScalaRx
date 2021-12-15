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
