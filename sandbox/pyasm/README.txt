About the ASM based bytecode generation framework for Jython
============================================================

Table of Contents
-----------------

 1. What does it do?
    1.1 What it does not do
 2. Installation
 3. Using PyASM
 4. The parts of PyASM
 5. How PyASM works
 6. Mappings from Python Bytecode to Java Bytecode
    6.1 Explanation of python and java bytecode features, and their differences
 7. Future extensions


 1. What does it do?
 ===================

PyASM is a framework based around Python bytecode. Combined with the ASM
library this is used to convert Python bytecode to Java bytecode.

 1.1 What it does not do
 -----------------------

 * PyASM does not expose all features of ASM to python. Although some of these
   aspects might be interesting to expose in some way in future extensions.
 * PyASM does not generate pure java classes, It generates PyCode objects by
   subclassing PyFunctionTable.


 2. Installation
 ===============

PyASM is at the moment a separate component from Jython and therefore needs to
be installed into Jython. This is done with the included ant script
(build.xml). The default target of this ant script builds Jython and copies
the required files from pyasm to the Jython Lib directory. In order for the
ant script to find your Jython working directory the path needs to be
specified in the file 'build.properties', as the variable 'jython.dir'.

Please note that rebuilding Jython with the standard Jython building procedure
after installing PyASM might overwrite some of the installed PyASM-files. At
the moment this is know to happen to the file 'marshal.py'.


 3. Using PyASM
 ===============

PyASM operates on python bytecode, and therefore depends on the C
implementation of python to generate bytecode. For instructions on how to
generate python bytecode from your python code, please refere to the python
documentaion.
To read python bytecode from .pyc-files and generate PyCode-objects from that
via generation of java bytecode there is a extra function called _readPyc in
the implementation of the marshal library included in this distribution. This
function accepts a filename as input and reads the bytecode from that file.
To simplify the process of generating python bytecode there is a script called
test.py that reads a python source file (specified by a command line argument)
and invokes the CPython implementation to compile it to bytecode and then uses
that bytecode to generate the java bytecode. test.py expects python 2.5 to be
available as "python" on the system path.
Example use of test.py:
$ jython test.py <your_python_file_here>.py


 4. The parts of PyASM
 =====================

TODO


 5. How PyASM works
 ==================

TODO


 6. Mappings from Python Bytecode to Java Bytecode
 =================================================

 6.1 Explanation of python and java bytecode features, and their differences
 ---------------------------------------------------------------------------
 
TODO


 7. Future extensions
 ====================

TODO

