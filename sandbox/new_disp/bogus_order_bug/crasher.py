from example import Element_foo as Efoo
from example import Element_bar as Ebar

efoo = lambda : Efoo("",Efoo.EMPTY_CONTENT)
ebar = lambda : Ebar("",[])
cont = lambda *x: [ Efoo.Content(e) for e in x ]

efoo3 = efoo()
bar2 = Ebar("",[efoo3])
foo2 = Efoo("",cont(bar2))
efoo2 = efoo()
efoo1 = efoo()
bar1 = Ebar("",[efoo1])
foo1 = Efoo("",cont(bar1,efoo2))
ebar0 = ebar()

from example import Element_root as Er
def choices(*x):
   w = { Efoo: Er.Choice_1_Alt_1, Ebar: Er.Choice_1_Alt_2 }
   return [ w[e.__class__](e) for e in x ]

ch = choices(ebar0,foo1,foo2)

r=Er("",ch)

from example import Document_root as Dr
doc=Dr(r)

from example import Visitor

import java

class MyVisitor(Visitor):
 def __init__(self):
   self.tags = []
 def visit(self,el):
   tag = None
   if isinstance(el,Ebar):
     tag = 'bar'
   elif isinstance(el,Efoo):
     tag = 'foo'
   if tag: self.tags.append(tag)
   Visitor.visit(self,el)
   if tag:
     tags = self.tags
     if tags[-1] == tag:
       tags[-1] = tag+'/'
     else:
       tags.append('/'+tag)

print "*using Visitor*"

v=Visitor()
root = doc.getDocumentElement()
print root.__class__
v.visit(root)

print "*using MyVisitor*"

myv = MyVisitor()
myv.visit(doc.getDocumentElement())

print myv.tags
