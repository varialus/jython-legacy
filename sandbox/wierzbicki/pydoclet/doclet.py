import os

from org.python.util.pydoclet import Processor

class doclet(Processor):
    def __init__(self):
        pass

    def process(self, root):
        #for clazz in root.classes():
        #    print clazz.name()
        #    for method in clazz.methods():
        #        print method.name()
        print "***Packages***"
        for pac in root.specifiedPackages():
            path = pac.name().replace(".", "/")
            if not os.path.exists(path):
                os.makedirs(path)
            init = open(os.path.join(path, "__init__.py"), 'a')
            
            for clazz in pac.allClasses():
                print >> init, "class %s:" % clazz.name()
                for method in clazz.methods():
                    print >> init, "    def %s():\n        pass\n" % method.name()
 
