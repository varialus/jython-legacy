
import sys

def load(fn):
    dict = {}
    execfile(fn,dict)
    try:
        del dict['__builtins__']
    except KeyError:
        pass
    return dict


def printlist(lst):
    for el in lst:
        print " ",el

locs = {}

def normalize(lst):
    normalized = []
    for el in lst:
        rbrk = el.find(']')
        if rbrk != -1:
            loc = el[1:rbrk]
            name = el[rbrk+1:]
            try:
                el = "[%s]%s" % (locs[loc],name)
            except KeyError:
                pass
        normalized.append(el)
    return normalized

class ResultsBag:
    def __init__(self,values):
        self.__dict__.update(values)
        self.bad = normalize(self.bad)
        self.good = normalize(self.good)
        self.skipped = normalize(self.skipped)
        
    def tot(self):
        return len(self.bad+self.good+self.skipped)

def intersect(l1,l2):
    r = []
    for x in l1:
        if x in l2:
            r.append(x)
    return r

def bothdeltas(l1,l2):
    common = intersect(l1,l2)

    onlyin1,onlyin2 = [],[]

    for onlyin,l in [(onlyin1,l1),(onlyin2,l2)]:
        for x in l:
            if x not in common:
                onlyin.append(x)

    return onlyin1,onlyin2

def report_number(fmt,n,prev=None):
    print fmt % n,
    if prev is not None:
        delta = n - prev
        if delta < 0:
            print " (%d)" % delta
        elif delta == 0:
            print " (=)"
        else:
            print " (+%d)" % delta
    else:
        print
        
def report_change(desc,l,prev):
    chg = intersect(l,prev)
    if chg:
        print len(chg),desc
    printlist(chg)

def diffs(results,prev):
    if hasattr(prev,'_diffs'):
        return prev._diffs
    else:
        all = results.good+results.bad+results.skipped
        prevall = prev.good+prev.bad+prev.skipped
        prev._diffs = bothdeltas(all,prevall)
        return prev._diffs
    
def report_kind(kind,results,prev):
    kindattr = { 'failed': 'bad', 'passed': 'good', 'skipped': 'skipped' }[kind]
    l = getattr(results,kindattr)
    lprev = prev and getattr(prev,kindattr)
    report_number("%%d %s" % kind,len(l),prev and len(lprev))    
    if not prev:
        printlist(l)
    else:
        report_change("%s, were %s" % (kind,kind),l,lprev)
        curonly,prevonly = diffs(results,prev)
        l = intersect(l,curonly)
        if l:
            print "%d new %s" % (len(l),kind)
            printlist(l)
        lprev = intersect(lprev,prevonly)
        if lprev:
            print "%d vanished %s" % (len(lprev),kind)
            printlist(lprev)
        

def tot(results,prev=None):
    report_number("%d test(s)",results.tot(),prev and prev.tot())
    report_number("%d passed",len(results.good),prev and len(prev.good))

def regr(results,prev=None):
    if not prev: return
    report_change("failed, were passed",results.bad,prev.good)
    report_change("skipped, were passed",results.skipped,prev.good)

def impro(results,prev=None):
    if not prev: return
    report_change("passed, were failed",results.good,prev.bad)
    report_change("passed, were skipped",results.good,prev.skipped)

def skipped(results,prev=None):
    report_kind("skipped",results,prev)
    if prev:
        report_change("skipped, were failed",results.skipped,prev.bad)        

def failed(results,prev=None):
    report_kind("failed",results,prev)
    if prev:
        report_change("failed, were skipped",results.bad,prev.skipped)
    
def passed(results,prev=None):
    report_kind("passed",results,prev)

def main():
    args = sys.argv[1:]
    if args[0] == '-locs':
        del args[0]
        locs_f = open(args[0],"r")
        while 1:
            line = locs_f.readline()
            if not line: break
            print "LOC",line.rstrip()
            from_,to = line.split('=')
            from_ = from_.strip()
            to = to.strip()
            locs[from_] = to
        del args[0]
    
    results = ResultsBag(load(args[0]))
    prev = None
    if len(args)==2:
        prev = ResultsBag(load(args[1]))

    tot(results,prev)
    regr(results,prev)
    impro(results,prev)
    skipped(results,prev)
    failed(results,prev)
    passed(results,prev)

if __name__=='__main__':
    main()
