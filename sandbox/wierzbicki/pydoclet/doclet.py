import os

from org.python.util.pydoclet import Processor

class doclet(Processor):
    def __init__(self):
        pass

    def process(self, root):
        for pac in root.specifiedPackages():
            print "working..."
            path = pac.name().replace(".", "/")
            if not os.path.exists(path):
                os.makedirs(path)
            init = open(os.path.join(path, "__init__.py"), 'w')
            
            for clazz in pac.allClasses():
                print >> init, "class %s:\n    '''%s'''" % (clazz.name(), clazz.commentText())
                for method in clazz.methods():
                    print >> init, "    def %s():\n        '''%s'''\n        pass\n" % (method.name(), method.commentText())
 
