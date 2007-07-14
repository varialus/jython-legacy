require 'java'

module TestCache
  include_package 'org.python.core.testcache'
end

jarpaths = ['jars']
classpaths = []
f = java.io.File.new("cachedir")
c = TestCache::CacheManager.new(f, classpaths, jarpaths)
