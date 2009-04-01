PYTHON=	echo SPECIFY PYTHON
DIFF=	diff
ECHO=	echo
TIME=	time
CMP=	cmp
SCP=	scp
RM=	rm

FILES=	README.txt Makefile b.py b[0-9].py t.py out out[0-9]
TARFILE=parrotbench.tgz
TARGET=	python.org:~ftp/pub/python/parrotbench/parrotbench.tgz

time:
	$(TIME) $(PYTHON) b.py >@out
	$(CMP) @out out

cmp:
	$(PYTHON) -O b.py >@out
	$(CMP) @out out

diff:
	$(PYTHON) -O b.py >@out
	$(DIFF) @out out

dist: $(TARFILE)

$(TARFILE): $(FILES)
	tar czf $(TARFILE) $(FILES)

upload: dist
	$(SCP) $(TARFILE) $(TARGET)

clean:
	-$(RM) -f @* *~ *.pyc *.pyo $(TARFILE)

times:
	for i in 0 1 2 3 4 5 6; do \
	    $(ECHO) b$$i.py; \
	    $(TIME) $(PYTHON) b$$i.py >@out$$i; \
	    $(CMP) @out$$i out$$i; \
	done

cmps:
	for i in 0 1 2 3 4 5 6; do \
	    $(ECHO) b$$i.py; \
	    $(PYTHON) b$$i.py >@out$$i; \
	    $(CMP) @out$$i out$$i; \
	done

diffs:
	for i in 0 1 2 3 4 5 6; do \
	    $(ECHO) b$$i.py; \
	    $(PYTHON) b$$i.py >@out$$i; \
	    $(DIFF) @out$$i out$$i; \
	done

all: out out0 out1 out2 out3 out4 out5 out6

out: b.py b?.py
	$(PYTHON) -O b.py >out

out0: b0.py
	$(PYTHON) b0.py >out0

out1: b1.py
	$(PYTHON) b1.py >out1

out2: b2.py
	$(PYTHON) b2.py >out2

out3: b3.py
	$(PYTHON) b3.py >out3

out4: b4.py b0.py
	$(PYTHON) b4.py >out4

out5: b5.py
	$(PYTHON) b5.py >out5

out6: b6.py
	$(PYTHON) b6.py >out6
