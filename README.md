# nounou

Nounou is a JVM-based interface for loading neurophysiological data, written mainly in Scala. It is a full rewrite of a prior version in Java (Takagaki, 2011), and old functions are gradually being translated to Scala and refactored in as needed.

## Package Goals

1. **to provide an adapter to dynamically load neurophysiology data** into MATLAB, Mathematica, Scala REPL, and Java (and Python). 
     - Other file readers are designed to read whole data files into memory at once. This can become quite problematic for large/long files.
     - Nounou is designed around data streams, where the data can be loaded as-needed on demand from disk/network.
     - Nounou can handle channel layout data within the main data structure to facilitate geometric analyses. This was originally for voltage-sensitive dye/intrinsic imaging, but is applicable for ECoG arrays and multishank electrodes too.
2. **to provide building blocks for computationally intensive neurophysiology analysis algorithms**, such as flow-analysis [1]. 
3. (perhaps at some point) to provide a rudimentary JavaFX-based graphical interface for browsing neurophysiology data
4. Nounou is in no way intended as an "uber package" to serve as a one-step analysis tool. Instead, it is intended for interactive use with a REPL/notebook such as Mathematica/Matlab/iPython Notebook and for incorporation into your custom programs as a citable, Git-versioned library. **Nounou focuses only on (1) loading data in a coherent way, and (2) performing very basic but calculation-intensive analyses.**   

Many of Nounou's routines are based on the numerical processing library [breeze](http://github.com/scalanlp/breeze).

## Why [Scala](http://www.scala-lang.org/)?
- JVM Benefits
     - Since Scala is JVM-based, it is easily accessed from MATLAB/Mathematica for further advanced analyses and graphing.
     - The JVM ensures unmatched cross-platform compatiblity.
     - Takes advantage of the latest processor-specific optimizations in the Oracle JVM. This can make tightly-written Java-based programs faster than non-optimized generic C code in certain circumstances.
- Scala Benefits
     - Fast compiled routines can be used even from a REPL.
     - Scala makes parallel/distributed programming simple and relatively safe
     - The object-oriented nature of Scala is key to keep track of complex data structures
     - The functional programming capabilities of Scala make parallelization and streaming safer/much easier
     - Scala is both theoretically well concieved and also practical. It is cool.
- Why not Python: speed issues with uncompiled Python and there is already a relatively large Python project available  ([neo](http://neuralensemble.org/neo/))
(A Python bridge should be relatively simple to make at some point)
     


## Documentation

see [wiki](https://github.com/ktakagaki/nounou/wiki)


## Code Use

The license is as stated.

Academically, there is no citable publication as of yet.  Please cite the following, which uses a prior version of the code:

[1] Takagaki, et. al. (2011) Flow detection of propagating waves with temporospatial correlation of activity. J Neurosci Meth. 200(2):207-218


## Contributions

Please contribute if you share the above goals! Significant contributions may also contribute to a potential future publication (~2016 timeframe)
