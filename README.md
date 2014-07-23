# nounou

Nounou is a JVM-based interface for loading neurophysiological data, written mainly in Scala. It is a full rewrite of a prior version in Java.

## Package Goals

1. **to provide an adapter to dynamically load neurophysiology data** into MATLAB, Mathematica, Scala REPL, and Java (and Python). 
     - Other file readers are designed to read the whole data file into memory at once. This becomes quite problematic for large/long files. Nounou is designed around data streams, where the data can be loaded on demand from disk/network.
     - Since nounou is JVM-based, it is easily accessed from MATLAB/Mathematica for advanced analyses and graphing.
     - At some point, we will use a Java/Python bridge to allow transparent access from Python. I have avoided direct implementation in Python for this rewrite, because of (1) the large project already available ([neo](http://neuralensemble.org/neo/)), because (2) Python requires C compilation to run at reasonable speeds, and because (3) the speed/elegance benefits of Scala/breeze as listed below, especially in terms of parallelization.  
2. **to provide building blocks for computationally intensive neurophysiology analysis algorithms**, such as flow-analysis [1]. 
     * Scala is suited for this, because of its easy-and-safe parallelization, and because of its ability to use fast compiled routines even from a REPL.
     * The object-oriented nature of Scala is key to keep track of complex data structures.
     * functional paradigms and the Scala collections framework allow for easy distributed computing with clean code.
3. (perhaps) to provide a JavaFX-based graphical interface for browsing neurophysiology data
4. Nounou is in no way intended as an "uber package" to serve as a one-step analysis tool. Instead, it is intended for interactive use with a REPL/notebook such as Mathematica/Matlab/iPython and for incorporation into your custom programs as a citable, Git-versioned library. **Nounou focuses only on loading data in a coherent way, and performing very very basic but calculation-intensive analyses.**   

Many of Nounou's routines are based on the numerical processing library [breeze](http://github.com/scalanlp/breeze).



## Documentation

see [wiki](https://github.com/ktakagaki/nounou/wiki)


## Code Use

The license is as stated.

Academically, there is no citable publication as of yet.  Please cite the following, which uses a prior version of the code:

[1] Takagaki, et. al. (2011) Flow detection of propagating waves with temporospatial correlation of activity. J Neurosci Meth. 200(2):207-218


## Contributions

Please contribute if you share the above goals! Significant contributions may also contribute to a potential future publication (~2016 timeframe)
