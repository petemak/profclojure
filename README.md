# Code and Excercises from Professional Clojure

## cljbasics or ch1
Contains code from the initial chapter. Code handles basic concepts like
* Principle of values
* Referential transparency
* Tail recursion .. loop/recur
* Mutual recursion ... trampoline
* High order functions
* Partial funtions
* Composition
* Laziness
* *Atoms* for synchronous uncordinated single state change
* *Refs*  for coordinated state change with multiple objectd
* Defmulti defmethod for polymorhic dispatch
* Deftype and Defrecord for descriptions and actions
* Protocols for a named set of functions
* Persistent data structures

## ch2
Rapid feedback cycles. 
* REPL - basic use doc, find-doc, source, javadoc
* REPL - starting a nREPL server and client with _lein repl_ or _lein repl :start :host localhost :port xxxxx_
* REPL - starting a headless nREPL server with _lein repl :headless :host localhost :port xxxxx_
* REPL - connecting to server with _lein repl :connect localhost:xxxxx_
* REPL - embedding REPL is server application with _lein-ring_ plugin and _:nrepl {:start? true}..._
* REPL - code reload with _org.clojure/tools.namespace_ and _:reload_


## ch3
Web Services. Utilising coljure features e.g. expressivenes, immutable data structures, concurrency and re-use to easily create web services.
* Project structure with the *compojure* template: _lein new compojure <project name>_
* *Ring* for HTTP abstraction
* *Compojure* for routing
* *example-project* works through *ring* and *compojure* concepts
* *ring-shortener* summary project for CH3 