from org.python.core.testcache import CacheManager
from java.io import File
jarpaths = ['jars']
classpaths = []
c = CacheManager(File("cachedir"), classpaths, jarpaths)
