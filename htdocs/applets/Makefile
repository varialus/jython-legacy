
PYTHON= python
HT2HTML= /opt/ht2html-1.1

.SUFFIXES:: .ht .html

.ht.html:
     $(PYTHON) $(HT2HTML)/ht2html.py -s JPyGenerator -r .. $?


HTMLFILES = \
	button.html \
	checkbox.html \
	choice.html \
	converter.html \
	coordinates.html \
	index.html \
	issues.html \
	label.html \
	list.html \
	problems.html

all:  $(HTMLFILES)


