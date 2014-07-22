# nounou

Nounou is a JVM-based interface for neurophysiological data written mainly in Scala.

The package has two goals:

* **to provide an adapter to dynamically load neurophysiology data** into MATLAB, Mathematica, Scala REPL, and Java. 

     * Current file readers read the whole file into memory at once. This becomes problematic for large/long files. Nounou can read multiple large files as streams, loaded-on-demand from the harddisk.

     * Since nounou is JVM-based, it is easily accessed from MATLAB/Mathematica for analyses and graphing.


* **to provide building blocks for computationally intensive neurophysiology analysis algorithms**, such as flow-analysis [1]. 

     * Scala is suited for this, because of its easy-and-safe parallelization, and because of its ability to use fast compiled routines even from a REPL.

     * The object-oriented nature of Scala is key to keep track of complex data structures.

     * functional paradigms and the Scala collections framework allow for easy distributed computing with clean code.
 

* (perhaps) to provide a JavaFX-based graphical interface for browsing neurophysiology data


Many of Nounou's routines are based on the numerical processing library [breeze](http://github.com/scalanlp/breeze).


# Documentation #

see [wiki](https://github.com/ktakagaki/nounou/wiki)
