
import java

bx_names = "Long Integer Short Character Byte Double Float Boolean".split()

bxtypes = [ getattr(java.lang,name) for name in bx_names] 
basic = [ bx.TYPE for bx in bxtypes ] + [java.lang.String]

bxtypes += [java.math.BigInteger, java.lang.Number]

import Foo,SubFoo

foo = [SubFoo,Foo]

top = [java.lang.Class,java.io.Serializable,java.lang.Object]

import org.python.core

pyo = [org.python.core.PyObject]

print "basic,bxtypes,foo,top,pyo #:",len(bxtypes+basic+foo+top+pyo)

comp=org.python.core.ReflectedArgs.compare

def checktbl(l):
    for i in range(len(l)):
        x = l[i]
        for j in range(len(l)):
            r = comp(x,l[j])
            if r == 0:
                r = '0'
            elif r>0:
                r = '+'
            else:
                r = '-'
            print "%s" % r,
        print

def ar(typ):
    import jarray
    return java.lang.Object.getClass(jarray.zeros(0,typ))

def decl(typ):
    if java.lang.Class.isArray(typ):
        return decl(java.lang.Class.getComponentType(typ))+"[]"
    else:
        return str(typ)

# !!! no pyo, no bxtypes for now

scal = basic+foo+top
mixed = basic+map(ar,basic)+foo+map(ar,foo)+map(ar,top)+top
arr = map(ar,basic)+map(ar,map(ar,basic))+map(ar,foo+top[:1])+map(ar,map(ar,foo+top[:1]))+map(ar,map(ar,top[1:]))+map(ar,top[1:])

print "scal#:",len(scal)
print "mixed#:",len(mixed)
print "arr#:",len(arr)

def outmeth(lbl,sign):
    sign = map(decl,sign)
    parms = zip(sign,['a','b','c','d','e','f'])
    parms = ','.join(["%s %s" % (dcl,n) for dcl,n in parms])
    return 'public String ov_%s(%s) { return "(%s)"; }' % (lbl,parms,','.join(sign)) 

def outovmeth(f,lbl,signs):
    print >>f,"// ov_%sXX" % lbl
    c = 1
    for st in range(len(signs)):
        for i in range(st,len(signs)):
            print >>f,' ',outmeth("%s%s" % (lbl,c),signs[i])
        print >>f
        c += 1
    print >>f
    


scal = [ (x,) for x in scal ]
mixed = [ (x,) for x in mixed ]
arr = [ (x,) for x in arr ]

posprec = [ (basic[1],basic[0]), (basic[0],basic[1]) ] # long,int ; int,long

f=open("JOverload.java",'w')

print >>f,"public class JOverload {"
outovmeth(f,'posprec',posprec)
outovmeth(f,'scal',scal)
print >>f,"}"


f.close()
