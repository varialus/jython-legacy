===============
 README: jysite
===============

---------------------------------------------------------------------------
An HTML writer for docutils to generate jython website with navigation-bars
---------------------------------------------------------------------------

:Author:    Frank Wierzbicki
:Contact:   fwierzbicki@gmail.com

.. contents::

Introduction
============

This is the writer used to build the jython site http://www.jython.org

System Requirements
===================

Docutils and everything needed to use docutils has to be
installed.

Installation
============

Perform the usual ``setup.py install`` procedure.

Usage
=====

Most parts of the writer are inherited from David Goodger's ``html4css1``.

This is a heavily modified version of Gunnar Schwant's rst2htmlnav.py
Where Gunnar's module is generic, this module is highly specific to
creating the Jython website.

.nav-files
----------

In order to get navigation-bars in the output-page you have to create
``.nav``-files in the destination directory. There are 4 types of 
``.nav``-files:

[file].nav
  This defines the left navigation-bar of the output-file ``[file].html``.
  (``[file]`` is the name of the output-file without extension.) 

left.nav
  This defines the left navigation-bar of all output-files for which
  no ``[file].nav``-file is present.

top.nav
  This defines the top navigation-bar of all output-files.

.. important:: At least one of the files ``[file].nav`` and ``left.nav`` 
               has to be present. Otherwise no navigation-bars will be
               added to the output file.

.nav-file-entries
-----------------

``.nav``-files contain a one-liner for each navigation-bar-entry.
In general such a one-liner is of the form

::

  parameter | value 1 | value 2

These are the different types of parameters:

cornerpic
~~~~~~~~~

:Used in: ``top.nav``
:Value 1: Path to the graphics file which will be displayed in the upper 
          left corner of the top navigation-bar. (The *width* of a 
          corner-picture should be 150 pixel.)
:Value 2: Not used.
:Example: ``cornerpic | pics/home.png`` sets the path of the corner-picture
          to ``pics/home.png``. 

raw
~~~

:Used in: ``[file].nav``, ``left.nav``
:Value 1: Any kind of text.
:Value 2: Not used.
:Example: ``raw | <br>`` will create an empty cell (a spacer) in 
          the navigation-bar.

section
~~~~~~~

:Used in: ``[file].nav``, ``left.nav``, ``right.nav``
:Value 1: Title of the section.
:Value 2: Not used.
:Example: ``section | Home`` will create a section with title *Home* in 
          the navigation-bar.

link
~~~~

:Used in: ``[file].nav``, ``left.nav``, ``top.nav``
:Value 1: Text to be displayed.
:Value 2: URL
:Example: ``link | Docutils | http://docutils.sf.net`` creates a link to 
          Docutils' homepage in the navigation-bar.

