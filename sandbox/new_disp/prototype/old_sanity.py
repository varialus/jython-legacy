import values
import Ov

ov = Ov()

def invoke(name,i,*v):
  return getattr(ov,"%s%d" % (name,i))(*v)

non_serializable = [values.o,values.c,values.foo,values.subfoo,values.subfoo2]

c = 0

for i in range(51):
  print "-- with ov%d" % i
  for v in values.values:
    if i == 49 and v in non_serializable: # !!! workaround for bug
       continue
    print "  %-*s  :" % (values.maxpp,values.pp(v)),
    print invoke("ov",i,v) 
    print "[ %-*s ]:" % (values.maxpp,values.pp(v)),
    print invoke("ov",i,[v]) 
    c += 2

for i in range(52):
  print "-- with ov_ar%d" % i
  for v in values.values:
    print " [ %-*s ] :" % (values.maxpp,values.pp(v)),
    print invoke("ov_ar",i,[v]) 
    print "[[ %-*s ]]:" % (values.maxpp,values.pp(v)),
    print invoke("ov_ar",i,[[v]]) 
    c += 2

for i in range(2):
  print "-- with ov_posprec%d" % i
  print "[ 0, 0 ]:",
  print invoke("ov_posprec",i,0,0)  
  c += 1

print "%d invocation(s)" % c