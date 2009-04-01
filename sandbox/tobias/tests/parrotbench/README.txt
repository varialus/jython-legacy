Parrot benchmark 1.0.4
======================

[This is version 1.0.4, with a bugfix for the Mac OSX issue that Dan
reported, two further bugfixes for the dict ordering problem noted by
Samuele Pedroni, and an updated Makefile and t.py driver for Windows.]

This is a benchmark to be run in front of a live audience at OSCON
2004 between Python and Parrot.  The bytecode must be Python 2.3
bytecode frozen in December 2003 (which is almost over as I write this
:-).

For some more background, see the python-dev thread around
  http://mail.python.org/pipermail/python-dev/2003-December/040977.html

The benchmark here is intended to make Dan Sugalski's life difficult:
there are some standard benchmark thingies (simple random algorithms
using basic data types) but also a lot of play with odd corners of the
language definition, and Python's extremely dynamic object model:
funky descriptors, mutable classes, that sort of thing.  The benchmark
is supposed to run with either Python 2.3 or Python 2.4.

Brief description per file:

Makefile -- Various ways of running the benchmark and sub-benchmarks.

b.py  -- The full benchmark output is defined as "python -O b.py".

b0.py -- Lots of fun with a parser for a subset of Python (just
         because this is a classic OO-heavy application).

b1.py -- Explore recursion.  Unbounded recursion is expected to raise
         RuntimeError after about 1000 levels.  (If Parrot supports much
         more, this part will be slower; the point is to be able to
         tweak the recursion limit.)

b2.py -- Calculate PI using a generator.

b3.py -- Sorting, and various ways of setting the comparison function
         for list.sort().

b4.py -- Another test for the code from b0.py, this time with code
         derived from Python 2.3's heapq.py.

b5.py -- Test the standard library, including three standard Unicode
         codecs (ASCII, Latin-1 and UTF-8), various descriptors, and
         mutable classes.

b6.py -- Check speed of iterating over several common iterators.

Note that per agreement I'm not allowed to use any built-in extension
modules, not even __builtin__ or sys.  This means there's no access to
sys.args (so no command line arguments), sys.stdout (but we have
"print >>file") or to sys.exc_info().  Dan could save some nanoseconds
by not supporting tracebacks.  But that would be cheating.  Also
cheating would be to do all the work at compile time (this is
theoretically possible since the program has no input).

Per agreement the following builtins are out: open(), file(), input(),
raw_input(), compile(), eval(), execfile(), and the exec statement.
But I assume the print statement is okay (the program must communicate
its success or failure *somehow*).

I'm being nice, and am voluntarily refraining from using __builtins__,
basestring, callable(), __import__(), reload(), dir(), globals(),
locals(), vars(), help(), apply(), buffer(), coerce(), or intern().
I'm also not digging around in the func_code attribute of function
objects.

On the use of __debug__: this is a built-in variable, set to True
normally and to False when Python is invoked with -O.  I use this to
produce verbose output without -O but minimal output with it.  For the
actual benchmark, Python should use -O, and Dan can simply set
__debug__ to False in the builtins if he prefers.

On timing: there's a requirement that the benchmark runs for at least
30 seconds.  It currently runs for nearly a minute on my home box,
which is a four-year-old 650 MHz Pentium box.  If the contest hardware
is so fast that it runs under a minute, there's a number in b.py that
can be cranked up to increase the number of runs.  (It takes 27
seconds on my recent work desktop, and on my screaming IBM T40 laptop
it runs in 15 seconds, so I suspect that we should at least double the
number of runs from 2 to 4.)

December 31, 2003,

--Guido van Rossum (home page: http://www.python.org/~guido/)
