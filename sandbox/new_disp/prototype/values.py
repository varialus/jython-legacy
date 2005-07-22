from java.lang import Object,Class,Number

d = {}

o = Object()
d[o] = 'o'

import Foo,SubFoo

class C(Object):
 pass

c = C()
d[c] = 'c'

class SubFoo2(Foo):
 pass

class N(Number):
 pass

foo,subfoo,subfoo2,n = Foo(),SubFoo(),SubFoo2(),N()

for _v,_id in zip([foo,subfoo,subfoo2,n],['foo','subfoo','subfoo2','n']):
  d[_v] = _id

from java.lang import Long,Integer,Short,Character,Byte,Boolean,Double,Float,String
from java.math import BigInteger

values = [
0,
0L,
0.0,
'c',
"string",
(),
o,
c,
foo,
subfoo,
subfoo2,
n,
Object,
Class,
Foo,
SubFoo,
C,
SubFoo2,
N,
Integer,
Long(0),
Integer(0),
Short(0),
Character('c'),
Byte(0),
Boolean(0),
Double(0),
Float(0),
String("js"),
BigInteger("0")
]

def pp(v):
  inst = d.get(v,None)
  if inst is not None:
     return inst
  if hasattr(v,'__name__'):
     return v.__name__
  if isinstance(v,(String,Number,Character,Boolean)):
     n = v.__class__.__name__
     return "%s[%s]" % (n[n.rfind('.')+1:],v)
  return repr(v)  

maxpp = max(map(len,map(pp,values)))








