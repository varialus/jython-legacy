
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


all:  $(HTMLFILES)


