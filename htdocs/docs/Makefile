
PYTHON= python
HT2HTML= /opt/ht2html-1.1

.SUFFIXES:: .ht .html

.ht.html:
     $(PYTHON) $(HT2HTML)/ht2html.py -s JPyGenerator -r .. $?


HTMLFILES = \
	compile.html \
	differences.html \
	embedding.html \
	index.html \
	interpreter.html \
	jarray.html \
	jreload.html \
	jythonc.html \
	properties.html \
	registry.html \
	subclassing.html \
	usejava.html \
	whatis.html \


all:  $(HTMLFILES)


