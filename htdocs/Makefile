
PYTHON= python
HT2HTML= /opt/ht2html-1.1

.SUFFIXES:: .ht .html

.ht.html:
     $(PYTHON) $(HT2HTML)/ht2html.py -s JPyGenerator $?


HTMLFILES = \
	download.html \
	index.html \
	platform.html \
	install.html \
	users.html \
	license.html \
	MacOS_Install.html \
	MacOS_Install2.html \
	MacOS_Install3.html \
	MacOS_Install4.html \
	MacOS_Install5.html \
	NEWS.html

all:  $(HTMLFILES)


