#!/usr/bin/env python

# Author: Frank Wierzbicki
#
# Started with Gunnar Schwant's rst2htmlnav.py
#
# Copyright: This module has been placed in the public domain.

"""
Generates the Jython website (with navigation) from ReStructuredText docs.
"""

try:
    import locale
    locale.setlocale(locale.LC_ALL, '')
except:
    pass

from docutils.core import publish_cmdline, default_description

description = ('Generates (X)HTML documents from standalone reStructuredText '
               'sources for the Jython website.  ' + default_description)

publish_cmdline(writer_name='jysite', description=description)
