
import Ov
ov = Ov()

import proto

CACHE = {}

def get_rf(name):
  rf = CACHE.get(name,None)
  if not rf:
      rf = proto.ReflFunc()
      for m in Ov.getDeclaredMethods():
          if m.name == name:
              rf.addMethod(m)
      CACHE[name] = rf
  return rf

def tryval(*v):  
    print "args:",v
    for i in range(2,51):
        name = "ov%d" % i
    
        print getattr(ov,name)(*v),"vs.",
        print get_rf(name)(ov,v,[])

tryval(0)
tryval("string")
tryval('c')

tryval(())
