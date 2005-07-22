
from example import Visitor
 
# check for bug

print "*checking Visitor.visit*"

cls = [x and x.args[0] or None for x in Visitor.visit.argslist]

cls = cls[:-1]


for i in range(len(cls)):
    cl0 = cls[i]
    for cl1 in cls[i+1:]:
       if issubclass(cl1,cl0):
          print 'wrong sig sorting',cl0,'<->',cl1
